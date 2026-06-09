package com.example.noteai.data.remote

import android.util.Log
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONArray
import org.json.JSONObject
import java.util.concurrent.TimeUnit

// Definition of the data class used throughout the app
data class GeneratedTopic(
    val title: String,
    val content: String,
    val notes: String,
    val difficulty: String,
    val codeExample: String? = null,
    val keyConcepts: List<String> = emptyList(),
    val source: String = "Gemini AI"
)

class GeminiService {

    // ⚠️ UPDATE THIS KEY WITH YOUR NEW ONE
    private val API_KEY = "AIzaSyCMpDw0LTwIzTitqauK_yZawLMh7G3Xtcc"
    private val TAG = "GeminiService"
    private val BASE_URL =
        "https://generativelanguage.googleapis.com/v1beta/models/gemini-2.5-flash-lite:generateContent"
    private val client by lazy {
        OkHttpClient.Builder()
            .connectTimeout(30, TimeUnit.SECONDS)
            .readTimeout(60, TimeUnit.SECONDS)
            .build()
    }

    private val cache = mutableMapOf<String, GeneratedTopic>()

    suspend fun getSuggestedTopics(): List<GeneratedTopic> = withContext(Dispatchers.IO) {
        val titles = listOf("OOP Principles", "Data Structures", "Sorting Algorithms", "System Design")
        titles.map { title -> fetchTopic(title) }
    }

    suspend fun getTopicDetail(titleRaw: String): GeneratedTopic = withContext(Dispatchers.IO) {
        // Fix URL encoding: "the+role+of+static" → "the role of static"
        val title = titleRaw.replace("+", " ").trim()
        fetchTopic(title)
    }

    suspend fun searchTopics(query: String): List<GeneratedTopic> = withContext(Dispatchers.IO) {
        if (query.isBlank()) return@withContext emptyList()

        val cleanQuery = query.replace("+", " ").trim()
        val prompt = """
            You are a software interview coach.
            Generate 3 interview topics related to: "$cleanQuery"

            For EACH topic use EXACTLY this format, separated by ===:
            TITLE: [topic name]
            CONTENT: [2-3 sentences explaining the topic]
            NOTES: [one interview tip]
            DIFFICULTY: Easy or Medium or Hard
            CONCEPTS: c1, c2, c3, c4
            CODE:
            [4-8 lines of Kotlin code with proper indentation]
            ===

            No intro text, just the 3 topics.
        """.trimIndent()

        val raw = callGeminiRaw(prompt)
        if (raw != null) {
            val results = raw.split("===")
                .filter { it.contains("TITLE:") }
                .map { parseSingle(it.trim(), "Search Result") }
            // ✅ Cache each by title so detail page reuses result without new API call
            results.forEach { if (it.title.isNotBlank()) cache[it.title] = it }
            results
        } else {
            emptyList()
        }
    }



    private suspend fun fetchTopic(title: String): GeneratedTopic {
        cache[title]?.let { return it }
        val prompt = buildTopicPrompt(title)
        val raw = callGeminiRaw(prompt)
        val result = if (raw != null) parseSingle(raw, title) else defaultTopic(title)
        cache[title] = result
        return result
    }

    private fun callGeminiRaw(prompt: String): String? {
        return try {
            val body = JSONObject().apply {
                put("contents", JSONArray().apply {
                    put(JSONObject().apply {
                        put("parts", JSONArray().apply {
                            put(JSONObject().apply { put("text", prompt) })
                        })
                    })
                })
            }.toString()

            val request = Request.Builder()
                .url("$BASE_URL?key=$API_KEY")
                .post(body.toRequestBody("application/json".toMediaType()))
                .build()

            client.newCall(request).execute().use { response ->
                val responseBody = response.body?.string() ?: ""
                if (!response.isSuccessful) {
                    Log.e(TAG, "API Error ${response.code}: $responseBody")
                    return null
                }
                val json = JSONObject(responseBody)
                json.getJSONArray("candidates")
                    .getJSONObject(0)
                    .getJSONObject("content")
                    .getJSONArray("parts")
                    .getJSONObject(0)
                    .getString("text")
            }
        } catch (e: Exception) {
            Log.e(TAG, "Network Error: ${e.message}")
            null
        }
    }

    private fun buildTopicPrompt(title: String): String {
        val cleanTitle = title.replace("+", " ").trim()
        return """
            You are a software engineering interview coach.
            Explain "$cleanTitle" in detail for interview preparation.

            Reply using EXACTLY this format, no extra text:
            TITLE: $cleanTitle
            CONTENT: Write 3 detailed sentences explaining what this topic is, how it works, and why it matters.
            NOTES: Write one highly practical interview tip that helps candidates stand out.
            DIFFICULTY: Easy or Medium or Hard (pick one)
            CONCEPTS: Write exactly 4 key concepts, comma-separated, no extra text.
            CODE:
            [Write a clean Kotlin code example of 6-10 lines demonstrating the concept. Use proper indentation. NEVER write NONE or skip this.]
        """.trimIndent()
    }

    /**
     * Parses a block of text containing KEY: value pairs.
     * Handles multi-line values (e.g. CODE blocks spanning multiple lines).
     */
    private fun parseSingle(raw: String, fallback: String): GeneratedTopic {
        return try {
            val knownKeys = listOf("TITLE:", "CONTENT:", "NOTES:", "DIFFICULTY:", "CONCEPTS:", "CODE:")
            val lines = raw.lines()

            // Collect all lines belonging to a key (until the next known key)
            fun getBlock(key: String): String {
                val startIdx = lines.indexOfFirst { it.trim().startsWith(key) }
                if (startIdx < 0) return ""
                val firstLine = lines[startIdx].substringAfter(":").trim()
                val subsequent = lines.drop(startIdx + 1)
                    .takeWhile { line -> knownKeys.none { k -> line.trim().startsWith(k) } }
                    .map { it.trimEnd() }  // preserve indentation, remove trailing spaces
                return if (subsequent.isEmpty()) firstLine
                else (listOf(firstLine) + subsequent).joinToString("\n").trimEnd()
            }

            val rawCode = getBlock("CODE:")
                // Remove markdown code fences if Gemini wraps in ```kotlin ... ```
                .replace(Regex("^```[a-zA-Z]*\n?"), "")
                .replace(Regex("\n?```$"), "")
                .trim()

            GeneratedTopic(
                title = getBlock("TITLE:").ifBlank { fallback }.replace("+", " ").trim(),
                content = getBlock("CONTENT:"),
                notes = getBlock("NOTES:"),
                difficulty = getBlock("DIFFICULTY:").ifBlank { "Medium" }.let {
                    when { it.contains("easy", true) -> "Easy"; it.contains("hard", true) -> "Hard"; else -> "Medium" }
                },
                codeExample = rawCode.takeIf { it.isNotBlank() },
                keyConcepts = getBlock("CONCEPTS:").split(",").map { it.trim() }.filter { it.isNotBlank() }.take(4)
            )
        } catch (e: Exception) {
            Log.e(TAG, "Parse error: ${e.message}")
            defaultTopic(fallback)
        }
    }

    // ── Static offline defaults ───────────────────────────────────────────────

    fun getDefaultTopics(): List<GeneratedTopic> = listOf(
        GeneratedTopic(
            title = "OOP Principles",
            content = "Object-Oriented Programming organizes code into objects that hold data and behavior. The four pillars are Encapsulation, Inheritance, Polymorphism, and Abstraction.",
            notes = "Common question: 'Difference between abstraction and encapsulation?'",
            difficulty = "Easy",
            keyConcepts = listOf("Encapsulation", "Inheritance", "Polymorphism", "Abstraction"),
            codeExample = """
                // Encapsulation Example
                class BankAccount(private var balance: Double) {
                    fun deposit(amount: Double) {
                        if (amount > 0) balance += amount
                    }
                    fun getBalance() = balance
                }
            """.trimIndent(),
            source = "Offline"
        ),
        GeneratedTopic(
            title = "Data Structures",
            content = "Data structures organize and store data for efficient access and modification. Common types include arrays, linked lists, trees, and hash maps.",
            notes = "Know time complexities cold: HashMap O(1), BST O(log n).",
            difficulty = "Medium",
            keyConcepts = listOf("Arrays", "Linked List", "HashMaps", "Trees"),
            codeExample = """
                // HashMap Example
                val map = mutableMapOf<String, Int>()
                map["Kotlin"] = 1
                map["Java"] = 2
                
                println(map["Kotlin"]) // Output: 1
            """.trimIndent(),
            source = "Offline"
        ),
        GeneratedTopic(
            title = "Sorting Algorithms",
            content = "Sorting algorithms arrange data in a defined order. QuickSort and MergeSort are the most commonly tested algorithms.",
            notes = "MergeSort: always O(n log n) but O(n) space.",
            difficulty = "Medium",
            keyConcepts = listOf("QuickSort", "MergeSort", "BubbleSort", "Complexity"),
            codeExample = """
                // Bubble Sort Example
                fun bubbleSort(arr: IntArray) {
                    for (i in 0 until arr.size - 1) {
                        for (j in 0 until arr.size - i - 1) {
                            if (arr[j] > arr[j + 1]) {
                                val temp = arr[j]
                                arr[j] = arr[j + 1]
                                arr[j + 1] = temp
                            }
                        }
                    }
                }
            """.trimIndent(),
            source = "Offline"
        ),
        GeneratedTopic(
            title = "System Design",
            content = "System design covers creating scalable architecture for large distributed systems. It involves scalability, availability, and consistency trade-offs.",
            notes = "Always clarify requirements first. Know the CAP theorem.",
            difficulty = "Hard",
            keyConcepts = listOf("Scalability", "Load Balancing", "Caching", "CAP Theorem"),
            codeExample = """
                // Simple Singleton Pattern (Thread Safe)
                object DatabaseProvider {
                    private var connection: String? = null
                    fun getConnection(): String {
                        if (connection == null) connection = "Connected"
                        return connection!!
                    }
                }
            """.trimIndent(),
            source = "Offline"
        )
    )

    private fun defaultTopic(title: String) =
        getDefaultTopics().firstOrNull { it.title.equals(title, ignoreCase = true) }
            ?: GeneratedTopic(
                title = title,
                content = "Study local resources for $title.",
                notes = "Fundamentals are key.",
                difficulty = "Medium",
                codeExample = """
                    // Generic example for $title
                    fun main() {
                        println("Exploring $title...")
                        // Implementation details here
                    }
                """.trimIndent(),
                source = "Offline"
            )
}