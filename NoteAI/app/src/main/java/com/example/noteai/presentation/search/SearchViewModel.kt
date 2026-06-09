package com.example.noteai.presentation.search

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.noteai.data.remote.GeminiService
import com.example.noteai.data.remote.GeneratedTopic
import com.example.noteai.domain.repository.NoteRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SearchViewModel @Inject constructor(
    private val repository: NoteRepository,
    private val geminiService: GeminiService
) : ViewModel() {

    private val _query = MutableStateFlow("")
    val query: StateFlow<String> = _query

    private val _state = MutableStateFlow(SearchState())
    val state: StateFlow<SearchState> = _state

    private var searchJob: Job? = null

    fun onSearchQueryChange(newQuery: String) {
        _query.value = newQuery
        searchJob?.cancel()

        if (newQuery.isBlank()) {
            _state.value = _state.value.copy(searchResults = emptyList(), isLoading = false)
            return
        }

        searchJob = viewModelScope.launch {
            delay(700) // Debounce
            _state.value = _state.value.copy(isLoading = true)

            try {
                // Parallel approach: try Gemini, fall back to defaults if it fails
                val aiResults = geminiService.searchTopics(newQuery)

                // Also check local saved topics
                val localResults = try {
                    repository.searchTopics(newQuery)
                        .catch { emit(emptyList()) }
                        .first()
                        .map { t ->
                            GeneratedTopic(
                                title = t.title,
                                content = t.content,
                                notes = t.notes,
                                difficulty = t.difficulty ?: "Medium",
                                source = "Saved"
                            )
                        }
                } catch (e: Exception) {
                    emptyList()
                }

                // Merge: AI results first + unique local results
                val aiTitles = aiResults.map { it.title.lowercase() }.toSet()
                val uniqueLocal = localResults.filter { it.title.lowercase() !in aiTitles }
                val combined = aiResults + uniqueLocal

                _state.value = _state.value.copy(
                    searchResults = combined.ifEmpty {
                        // Last resort: filter defaults by query
                        geminiService.getDefaultTopics().filter {
                            it.title.contains(newQuery, ignoreCase = true) ||
                            it.keyConcepts.any { c -> c.contains(newQuery, ignoreCase = true) }
                        }
                    },
                    isLoading = false
                )
            } catch (e: Exception) {
                // Show filtered defaults on any error
                _state.value = _state.value.copy(
                    searchResults = geminiService.getDefaultTopics().filter {
                        it.title.contains(newQuery, ignoreCase = true) ||
                        it.keyConcepts.any { c -> c.contains(newQuery, ignoreCase = true) }
                    },
                    isLoading = false
                )
            }
        }
    }
}

data class SearchState(
    val searchResults: List<GeneratedTopic> = emptyList(),
    val isLoading: Boolean = false,
    val popularChips: List<String> = listOf(
        "Dynamic Programming", "System Design", "Sorting Algorithms",
        "Binary Trees", "Recursion", "Design Patterns"
    )
)
