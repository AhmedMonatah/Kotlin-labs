package com.example.noteai.domain.model

data class Topic(
    val id: Int = 0,
    val title: String,
    val content: String,
    val notes: String = "",
    val folderId: Int? = null,
    val lastOpened: Long = System.currentTimeMillis(),
    val isSavedOffline: Boolean = true,
    val difficulty: String? = null, // "Easy", "Important", "Interview"
    val source: String? = null
)

data class Folder(
    val id: Int = 0,
    val name: String,
    val iconRes: Int? = null
)
