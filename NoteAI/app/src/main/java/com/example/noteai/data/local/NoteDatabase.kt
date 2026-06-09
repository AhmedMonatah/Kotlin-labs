package com.example.noteai.data.local

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Entity(tableName = "topics")
data class TopicEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val title: String,
    val content: String,
    val notes: String,
    val folderId: Int?,
    val lastOpened: Long,
    val isSavedOffline: Boolean,
    val difficulty: String?,
    val source: String?
)

@Entity(tableName = "folders")
data class FolderEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val name: String,
    val iconRes: Int?
)

@Dao
interface NoteDao {
    @Query("SELECT * FROM topics ORDER BY lastOpened DESC")
    fun getAllTopics(): Flow<List<TopicEntity>>

    @Query("SELECT * FROM topics WHERE folderId = :folderId")
    fun getTopicsByFolder(folderId: Int): Flow<List<TopicEntity>>

    @Query("SELECT * FROM folders")
    fun getAllFolders(): Flow<List<FolderEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTopic(topic: TopicEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertFolder(folder: FolderEntity)

    @Delete
    suspend fun deleteTopic(topic: TopicEntity)

    @Query("SELECT * FROM topics WHERE title LIKE '%' || :query || '%'")
    fun searchTopics(query: String): Flow<List<TopicEntity>>
}

@Database(entities = [TopicEntity::class, FolderEntity::class], version = 1)
abstract class NoteDatabase : RoomDatabase() {
    abstract val dao: NoteDao
}
