package com.example.noteai.data.repository

import com.example.noteai.data.local.NoteDao
import com.example.noteai.data.local.TopicEntity
import com.example.noteai.data.local.FolderEntity
import com.example.noteai.domain.model.Folder
import com.example.noteai.domain.model.Topic
import com.example.noteai.domain.repository.NoteRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class NoteRepositoryImpl(
    private val dao: NoteDao
) : NoteRepository {
    override fun getAllTopics(): Flow<List<Topic>> = 
        dao.getAllTopics().map { it.map { entity -> entity.toTopic() } }

    override fun getTopicsByFolder(folderId: Int): Flow<List<Topic>> = 
        dao.getTopicsByFolder(folderId).map { it.map { entity -> entity.toTopic() } }

    override fun getAllFolders(): Flow<List<Folder>> = 
        dao.getAllFolders().map { it.map { entity -> entity.toFolder() } }

    override suspend fun insertTopic(topic: Topic) = 
        dao.insertTopic(topic.toEntity())

    override suspend fun insertFolder(folder: Folder) = 
        dao.insertFolder(folder.toEntity())

    override suspend fun deleteTopic(topic: Topic) = 
        dao.deleteTopic(topic.toEntity())

    override fun searchTopics(query: String): Flow<List<Topic>> = 
        dao.searchTopics(query).map { it.map { entity -> entity.toTopic() } }
}

// Mappers
fun TopicEntity.toTopic() = Topic(id, title, content, notes, folderId, lastOpened, isSavedOffline, difficulty, source)
fun FolderEntity.toFolder() = Folder(id, name, iconRes)
fun Topic.toEntity() = TopicEntity(id, title, content, notes, folderId, lastOpened, isSavedOffline, difficulty, source)
fun Folder.toEntity() = FolderEntity(id, name, iconRes)
