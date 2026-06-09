package com.example.noteai.presentation.addtopic

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.noteai.domain.model.Topic
import com.example.noteai.domain.repository.NoteRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AddTopicViewModel @Inject constructor(
    private val repository: NoteRepository
) : ViewModel() {

    private val _eventFlow = MutableSharedFlow<UiEvent>()
    val eventFlow = _eventFlow.asSharedFlow()

    fun saveTopic(title: String, folderId: Int?, content: String, notes: String, difficulty: String?) {
        viewModelScope.launch {
            if (title.isBlank()) {
                _eventFlow.emit(UiEvent.ShowSnackbar("Title cannot be empty"))
                return@launch
            }
            repository.insertTopic(
                Topic(
                    title = title,
                    folderId = folderId,
                    content = content,
                    notes = notes,
                    difficulty = difficulty
                )
            )
            _eventFlow.emit(UiEvent.SaveTopic)
        }
    }

    sealed class UiEvent {
        object SaveTopic : UiEvent()
        data class ShowSnackbar(val message: String) : UiEvent()
    }
}
