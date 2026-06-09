package com.example.noteai.presentation.topicdetail

import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.noteai.data.remote.GeneratedTopic
import com.example.noteai.ui.theme.*

// ─────────────────────────────────────────────────────────────────────────────

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TopicDetailScreen(
    topicTitle: String?,
    onNavigateBack: () -> Unit,
    viewModel: TopicDetailViewModel = hiltViewModel()
) {
    val stateVal by viewModel.state.collectAsState()
    var isSaved by remember { mutableStateOf(false) }

    // ✅ No bottomBar — this is a detail page, not a nav destination
    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        topicTitle?.replace("+", " ") ?: "Topic Details",
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 17.sp,
                        maxLines = 1
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, "Back")
                    }
                },
                actions = {
                    if (stateVal is TopicDetailState.Success) {
                        IconButton(onClick = {
                            isSaved = !isSaved
                            if (isSaved) viewModel.saveTopicLocally()
                        }) {
                            Icon(
                                if (isSaved) Icons.Default.Bookmark else Icons.Default.BookmarkBorder,
                                contentDescription = "Save",
                                tint = if (isSaved) PrimaryBlue else TextSecondary
                            )
                        }
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Surface,
                    titleContentColor = OnBackground,
                    navigationIconContentColor = OnBackground
                )
            )
        }
    ) { padding ->
        when (val state = stateVal) {
            is TopicDetailState.Loading -> ShimmerDetailScreen(padding)
            is TopicDetailState.Success -> TopicContent(state.topic, padding)
            is TopicDetailState.Error   -> ErrorScreen(padding, onNavigateBack)
        }
    }
}

// ─── Shimmer Loading ──────────────────────────────────────────────────────────

@Composable
private fun ShimmerDetailScreen(padding: PaddingValues) {
    val transition = rememberInfiniteTransition(label = "shimmer")
    val alpha by transition.animateFloat(
        initialValue = 0.2f, targetValue = 0.6f,
        animationSpec = infiniteRepeatable(tween(900, easing = LinearEasing), RepeatMode.Reverse),
        label = "a"
    )
    LazyColumn(
        Modifier.fillMaxSize().padding(padding).background(Background),
        contentPadding = PaddingValues(bottom = 32.dp)
    ) {
        item {
            Box(
                Modifier.padding(16.dp).fillMaxWidth().height(120.dp)
                    .clip(RoundedCornerShape(24.dp))
                    .background(PrimaryBlue.copy(alpha = alpha * 0.7f))
            )
        }
        repeat(3) {
            item {
                Column(Modifier.padding(horizontal = 16.dp, vertical = 6.dp)) {
                    Box(Modifier.height(14.dp).fillMaxWidth(0.35f).clip(RoundedCornerShape(7.dp)).background(SurfaceVariant.copy(alpha = alpha)))
                    Spacer(Modifier.height(10.dp))
                    Box(Modifier.fillMaxWidth().height(72.dp).clip(RoundedCornerShape(16.dp)).background(SurfaceVariant.copy(alpha = alpha)))
                }
            }
        }
        item {
            Column(Modifier.padding(horizontal = 16.dp, vertical = 6.dp)) {
                Box(Modifier.height(14.dp).fillMaxWidth(0.3f).clip(RoundedCornerShape(7.dp)).background(SurfaceVariant.copy(alpha = alpha)))
                Spacer(Modifier.height(10.dp))
                Box(Modifier.fillMaxWidth().height(160.dp).clip(RoundedCornerShape(16.dp)).background(Color(0xFF0D1117).copy(alpha = alpha)))
            }
        }
    }
}

// ─── Error ────────────────────────────────────────────────────────────────────

@Composable
private fun ErrorScreen(padding: PaddingValues, onBack: () -> Unit) {
    Box(Modifier.fillMaxSize().padding(padding).background(Background), contentAlignment = Alignment.Center) {
        Column(horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.spacedBy(12.dp)) {
            Text("⚠️", fontSize = 48.sp)
            Text("Failed to load topic", fontWeight = FontWeight.SemiBold, color = OnBackground)
            Text("Check your connection and try again", color = TextSecondary, fontSize = 13.sp)
            Spacer(Modifier.height(4.dp))
            Button(
                onClick = onBack,
                colors = ButtonDefaults.buttonColors(containerColor = PrimaryBlue),
                shape = RoundedCornerShape(12.dp)
            ) { Text("← Go Back") }
        }
    }
}

// ─── Content ──────────────────────────────────────────────────────────────────

@Composable
private fun TopicContent(topic: GeneratedTopic, padding: PaddingValues) {
    val diffColor = when (topic.difficulty.lowercase()) {
        "easy" -> DifficultyEasy
        "hard" -> DifficultyHard
        else   -> DifficultyMedium
    }

    LazyColumn(
        Modifier.fillMaxSize().padding(padding).background(Background),
        contentPadding = PaddingValues(bottom = 48.dp)
    ) {
        // ── Hero card ──────────────────────────────────────────────────────────
        item {
            Box(
                Modifier
                    .padding(horizontal = 16.dp, vertical = 12.dp)
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(24.dp))
                    .background(
                        Brush.linearGradient(
                            colors = listOf(Color(0xFF1565C0), Color(0xFF1E88E5), Color(0xFF42A5F5))
                        )
                    )
                    .padding(24.dp)
            ) {
                Column {
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        Surface(shape = RoundedCornerShape(8.dp), color = Color.White.copy(alpha = 0.22f)) {
                            Text(topic.difficulty, fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Color.White,
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp))
                        }
                        if (topic.source == "Gemini AI") {
                            Surface(shape = RoundedCornerShape(8.dp), color = Color.White.copy(alpha = 0.22f)) {
                                Row(
                                    Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                                ) {
                                    Icon(Icons.Default.AutoAwesome, null, tint = Color.White, modifier = Modifier.size(11.dp))
                                    Text("AI Generated", fontSize = 11.sp, color = Color.White, fontWeight = FontWeight.Bold)
                                }
                            }
                        }
                    }
                    Spacer(Modifier.height(14.dp))
                    Text(topic.title, color = Color.White, fontWeight = FontWeight.Bold, fontSize = 22.sp, lineHeight = 30.sp)
                }
            }
        }

        // ── Definition ─────────────────────────────────────────────────────────
        item {
            SectionCard(title = "📖 Definition") {
                Text(topic.content, fontSize = 15.sp, lineHeight = 25.sp, color = OnBackground)
            }
        }

        // ── Interview Tip ──────────────────────────────────────────────────────
        if (topic.notes.isNotBlank()) {
            item {
                SectionCard(title = "💡 Interview Tip", cardColor = Color(0xFFFFF8E1)) {
                    Row(
                        verticalAlignment = Alignment.Top,
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Surface(shape = CircleShape, color = Color(0xFFFFF0B2)) {
                            Icon(Icons.Default.EmojiObjects, null, tint = Color(0xFFF57F17),
                                modifier = Modifier.padding(6.dp).size(16.dp))
                        }
                        Text(topic.notes, fontSize = 14.sp, lineHeight = 22.sp, color = Color(0xFF4E3620))
                    }
                }
            }
        }

        // ── Key Concepts ───────────────────────────────────────────────────────
        if (topic.keyConcepts.isNotEmpty()) {
            item {
                SectionCard(title = "🧠 Key Concepts") {
                    val chunked = topic.keyConcepts.take(4).chunked(2)
                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        chunked.forEach { row ->
                            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                row.forEach { concept ->
                                    Surface(shape = RoundedCornerShape(20.dp), color = PrimaryLight) {
                                        Text(concept, color = PrimaryBlue, fontWeight = FontWeight.Medium,
                                            fontSize = 13.sp, maxLines = 1,
                                            modifier = Modifier.padding(horizontal = 14.dp, vertical = 8.dp)
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }

        // ── Code Example ───────────────────────────────────────────────────────
        if (!topic.codeExample.isNullOrBlank()) {
            item { CodeBlock(code = topic.codeExample) }
        }
    }
}

// ─── Syntax-highlighted Code Block ───────────────────────────────────────────

// Kotlin color palette (GitHub Dark theme)
private val colorKeyword  = Color(0xFFFF7B72)   // red-pink  — fun, val, class …
private val colorType     = Color(0xFFE3B341)    // gold      — String, Int …
private val colorString   = Color(0xFFA5D6FF)    // sky-blue  — "text"
private val colorComment  = Color(0xFF8B949E)    // gray      — // comment
private val colorNumber   = Color(0xFF79C0FF)    // blue      — 42, 3.14
private val colorFunction = Color(0xFFD2A8FF)    // purple    — functionCall()
private val colorPlain    = Color(0xFFE6EDF3)    // white-ish — everything else

private val kotlinKeywords = setOf(
    "fun", "val", "var", "class", "object", "interface", "data", "sealed",
    "enum", "abstract", "open", "override", "private", "public", "protected",
    "internal", "companion", "suspend", "return", "if", "else", "when", "for",
    "while", "do", "in", "is", "as", "null", "true", "false", "this", "super",
    "try", "catch", "finally", "throw", "import", "package", "by", "init",
    "constructor", "get", "set", "lateinit", "typealias", "it", "break", "continue"
)

private val kotlinTypes = setOf(
    "String", "Int", "Long", "Double", "Float", "Boolean", "Char", "Byte",
    "Short", "Any", "Unit", "Nothing", "List", "Map", "Set", "MutableList",
    "MutableMap", "MutableSet", "Array", "Pair", "Triple", "Result", "Flow",
    "StateFlow", "MutableStateFlow", "ViewModel", "Context", "Activity"
)

/** Tokenises a single line and builds a coloured AnnotatedString. */
private fun highlightLine(line: String): AnnotatedString = buildAnnotatedString {
    // Full-line comment
    val trimmed = line.trimStart()
    if (trimmed.startsWith("//")) {
        withStyle(SpanStyle(color = colorComment)) { append(line) }
        return@buildAnnotatedString
    }

    var i = 0
    while (i < line.length) {
        // Inline comment
        if (i + 1 < line.length && line[i] == '/' && line[i + 1] == '/') {
            withStyle(SpanStyle(color = colorComment)) { append(line.substring(i)) }
            break
        }

        // String literal
        if (line[i] == '"') {
            val end = line.indexOf('"', i + 1).let { if (it < 0) line.length else it + 1 }
            withStyle(SpanStyle(color = colorString)) { append(line.substring(i, end)) }
            i = end
            continue
        }

        // Number literal
        if (line[i].isDigit() && (i == 0 || !line[i - 1].isLetterOrDigit())) {
            var j = i
            while (j < line.length && (line[j].isDigit() || line[j] == '.')) j++
            withStyle(SpanStyle(color = colorNumber)) { append(line.substring(i, j)) }
            i = j
            continue
        }

        // Word token — keyword / type / function / plain
        if (line[i].isLetter() || line[i] == '_') {
            var j = i
            while (j < line.length && (line[j].isLetterOrDigit() || line[j] == '_')) j++
            val word = line.substring(i, j)
            // Is next char '('? → function call
            val isFunc = j < line.length && line[j] == '('
            val color = when {
                word in kotlinKeywords -> colorKeyword
                word in kotlinTypes    -> colorType
                isFunc                 -> colorFunction
                else                   -> colorPlain
            }
            withStyle(SpanStyle(color = color)) { append(word) }
            i = j
            continue
        }

        // Everything else (operators, brackets …)
        withStyle(SpanStyle(color = colorPlain)) { append(line[i]) }
        i++
    }
}

@Composable
private fun CodeBlock(code: String) {
    val clipboard = LocalClipboardManager.current
    var copied by remember { mutableStateOf(false) }

    Column(Modifier.padding(horizontal = 16.dp, vertical = 6.dp)) {
        // Section header + copy button
        Row(
            Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                Icon(Icons.Default.Code, null, tint = PrimaryBlue, modifier = Modifier.size(16.dp))
                Text("💻 Code Example", fontWeight = FontWeight.SemiBold, fontSize = 15.sp, color = OnBackground)
            }
            Surface(
                onClick = {
                    clipboard.setText(AnnotatedString(code))
                    copied = true
                },
                shape = RoundedCornerShape(8.dp),
                color = if (copied) DifficultyEasy.copy(alpha = 0.12f) else SurfaceVariant
            ) {
                Row(
                    Modifier.padding(horizontal = 10.dp, vertical = 5.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Icon(
                        if (copied) Icons.Default.Check else Icons.Default.ContentCopy, null,
                        tint = if (copied) DifficultyEasy else TextSecondary,
                        modifier = Modifier.size(13.dp)
                    )
                    Text(
                        if (copied) "Copied!" else "Copy",
                        fontSize = 11.sp, fontWeight = FontWeight.Medium,
                        color = if (copied) DifficultyEasy else TextSecondary
                    )
                }
            }
        }

        Spacer(Modifier.height(10.dp))

        // Code container
        Box(Modifier.fillMaxWidth().clip(RoundedCornerShape(16.dp)).background(Color(0xFF0D1117))) {
            Column {
                // Top bar: traffic lights + language badge
                Row(
                    Modifier.fillMaxWidth().background(Color(0xFF161B22))
                        .padding(horizontal = 16.dp, vertical = 10.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                        listOf(Color(0xFFFF5F57), Color(0xFFFFBD2E), Color(0xFF28CA41)).forEach { c ->
                            Box(Modifier.size(11.dp).clip(CircleShape).background(c))
                        }
                    }
                    Surface(shape = RoundedCornerShape(4.dp), color = Color(0xFF238636).copy(alpha = 0.3f)) {
                        Text("Kotlin", fontSize = 10.sp, color = Color(0xFF7EE787),
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp))
                    }
                }

                // Lines with numbers + syntax highlighting
                val lines = code.lines()
                Row(
                    Modifier
                        .fillMaxWidth()
                        .horizontalScroll(rememberScrollState())
                        .padding(horizontal = 16.dp, vertical = 14.dp)
                ) {
                    // Line number gutter
                    Column(
                        Modifier.padding(end = 18.dp),
                        verticalArrangement = Arrangement.spacedBy(0.dp)
                    ) {
                        lines.forEachIndexed { idx, _ ->
                            Text(
                                "${idx + 1}",
                                fontSize = 13.sp,
                                fontFamily = FontFamily.Monospace,
                                color = Color(0xFF484F58),
                                lineHeight = 22.sp
                            )
                        }
                    }
                    // Highlighted code
                    Column(verticalArrangement = Arrangement.spacedBy(0.dp)) {
                        lines.forEach { line ->
                            Text(
                                text = highlightLine(line),
                                fontSize = 13.sp,
                                fontFamily = FontFamily.Monospace,
                                lineHeight = 22.sp
                            )
                        }
                    }
                }
            }
        }
    }
}

// ─── Reusable section card ────────────────────────────────────────────────────

@Composable
private fun SectionCard(
    title: String,
    cardColor: Color = Surface,
    content: @Composable ColumnScope.() -> Unit
) {
    Column(Modifier.padding(horizontal = 16.dp, vertical = 6.dp)) {
        Text(title, fontWeight = FontWeight.SemiBold, fontSize = 15.sp,
            color = OnBackground, modifier = Modifier.padding(bottom = 8.dp))
        Surface(
            shape = RoundedCornerShape(16.dp), color = cardColor,
            shadowElevation = 1.dp, modifier = Modifier.fillMaxWidth()
        ) {
            Column(Modifier.padding(18.dp)) { content() }
        }
    }
}
