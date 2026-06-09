package com.example.noteai.presentation.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.noteai.data.remote.GeminiService
import com.example.noteai.data.remote.GeneratedTopic
import com.example.noteai.domain.model.Folder
import com.example.noteai.domain.model.Topic
import com.example.noteai.domain.repository.NoteRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val repository: NoteRepository,
    private val geminiService: GeminiService
) : ViewModel() {

    private val _state = MutableStateFlow(HomeState())
    val state: StateFlow<HomeState> = _state

    init {
        loadGeminiTopics()
    }

    fun loadGeminiTopics() {
        viewModelScope.launch {
            _state.value = _state.value.copy(isLoadingAiTopics = true)
            try {
                val topics = geminiService.getSuggestedTopics()
                _state.value = _state.value.copy(
                    aiSuggestedTopics = topics,
                    isLoadingAiTopics = false
                )
            } catch (e: Exception) {
                // Show defaults if Gemini fails
                _state.value = _state.value.copy(
                    aiSuggestedTopics = geminiService.getDefaultTopics(),
                    isLoadingAiTopics = false
                )
            }
        }
    }

    fun refreshAiTopics() = loadGeminiTopics()
}

data class HomeState(
    val aiSuggestedTopics: List<GeneratedTopic> = emptyList(),
    val isLoadingAiTopics: Boolean = false,
    val curatedCategories: List<String> = listOf(
        "OOP", "Algorithms", "Data Structures", "System Design",
        "Databases", "OS Concepts", "Networking", "Design Patterns"
    ),
    val defaultFolders: List<String> = listOf(
        "Interview Prep", "Java Basics", "Kotlin", "Data Structures"
    )
)
