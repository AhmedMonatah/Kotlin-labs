package com.example.noteai.presentation.topicdetail

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.noteai.data.remote.GeminiService
import com.example.noteai.data.remote.GeneratedTopic
import com.example.noteai.domain.repository.NoteRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import java.net.URLDecoder
import java.nio.charset.StandardCharsets
import javax.inject.Inject

@HiltViewModel
class TopicDetailViewModel @Inject constructor(
    private val repository: NoteRepository,
    private val geminiService: GeminiService,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val topicIdArg: Int? = savedStateHandle.get<Int>("topicId")
    private val topicTitleArg: String? = savedStateHandle.get<String>("topicTitle")

    private val _state = MutableStateFlow<TopicDetailState>(TopicDetailState.Loading)
    val state: StateFlow<TopicDetailState> = _state

    init {
        when {
            topicTitleArg != null && topicTitleArg.isNotBlank() -> {
                // Decode URL-encoded title: "the+role+of+static" → "the role of static"
                val cleanTitle = topicTitleArg
                    .replace("+", " ")
                    .let { URLDecoder.decode(it, StandardCharsets.UTF_8.toString()) }
                    .trim()
                loadFromGemini(cleanTitle)
            }
            topicIdArg != null -> {
                loadFromDb(topicIdArg)
            }
            else -> {
                // No args — show first default topic
                _state.value = TopicDetailState.Success(
                    geminiService.getDefaultTopics().firstOrNull()
                        ?: GeneratedTopic("OOP", "", "", "Easy")
                )
            }
        }
    }

    private fun loadFromGemini(title: String) {
        viewModelScope.launch {
            _state.value = TopicDetailState.Loading
            val topic = geminiService.getTopicDetail(title)
            _state.value = TopicDetailState.Success(topic)
        }
    }

    private fun loadFromDb(id: Int) {
        viewModelScope.launch {
            _state.value = TopicDetailState.Loading
            try {
                val topics = repository.getAllTopics().first()
                val found = topics.find { it.id == id }
                if (found != null) {
                    _state.value = TopicDetailState.Success(
                        GeneratedTopic(
                            title = found.title,
                            content = found.content,
                            notes = found.notes,
                            difficulty = found.difficulty ?: "Medium",
                            source = found.source ?: "Saved"
                        )
                    )
                } else {
                    val topic = geminiService.getTopicDetail("OOP Principles")
                    _state.value = TopicDetailState.Success(topic)
                }
            } catch (e: Exception) {
                _state.value = TopicDetailState.Success(
                    geminiService.getDefaultTopics().firstOrNull()
                        ?: GeneratedTopic("OOP", "", "", "Easy")
                )
            }
        }
    }

    fun saveTopicLocally() {
        val current = (_state.value as? TopicDetailState.Success)?.topic ?: return
        viewModelScope.launch {
            repository.insertTopic(
                com.example.noteai.domain.model.Topic(
                    title = current.title,
                    content = current.content,
                    notes = current.notes,
                    difficulty = current.difficulty,
                    source = current.source
                )
            )
        }
    }
}

sealed class TopicDetailState {
    object Loading : TopicDetailState()
    data class Success(val topic: GeneratedTopic) : TopicDetailState()
    data class Error(val message: String) : TopicDetailState()
}
