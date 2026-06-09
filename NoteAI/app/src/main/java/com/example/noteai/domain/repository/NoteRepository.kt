package com.example.noteai.domain.repository

import com.example.noteai.domain.model.Folder
import com.example.noteai.domain.model.Topic
import kotlinx.coroutines.flow.Flow

interface NoteRepository {
    fun getAllTopics(): Flow<List<Topic>>
    fun getTopicsByFolder(folderId: Int): Flow<List<Topic>>
    fun getAllFolders(): Flow<List<Folder>>
    suspend fun insertTopic(topic: Topic)
    suspend fun insertFolder(folder: Folder)
    suspend fun deleteTopic(topic: Topic)
    fun searchTopics(query: String): Flow<List<Topic>>
}
