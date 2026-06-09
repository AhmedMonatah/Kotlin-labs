package com.example.kotlintest

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class MyViewModel : ViewModel() {
    private val _state = MutableStateFlow(CounterState())
    val state: StateFlow<CounterState> = _state

    fun handleIntent(intent: CounterIntent) {
        when (intent) {

            is CounterIntent.Increase -> {
                _state.value = _state.value.copy(
                    counter = _state.value.counter + 1
                )
            }

            is CounterIntent.Decrease -> {
                _state.value = _state.value.copy(
                    counter = _state.value.counter - 1
                )
            }
        }
    }
}