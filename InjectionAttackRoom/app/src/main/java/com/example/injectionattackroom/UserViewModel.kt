package com.example.injectionattackroom

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class UserViewModel(
    private val repository: UserRepository
) : ViewModel() {

    private val _users = MutableStateFlow<List<User>>(emptyList())
    val users: StateFlow<List<User>> = _users

    fun searchUnsafe(name: String) {
        viewModelScope.launch {
            _users.value = repository.searchUnsafe(name)
        }
    }

    fun searchSafe(name: String) {
        viewModelScope.launch {
            _users.value = repository.searchSafe(name)
        }
    }
}