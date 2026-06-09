package com.example.noteai.di

import android.content.Context
import androidx.room.Room
import com.example.noteai.data.local.NoteDao
import com.example.noteai.data.local.NoteDatabase
import com.example.noteai.data.remote.GeminiService
import com.example.noteai.data.repository.NoteRepositoryImpl
import com.example.noteai.domain.repository.NoteRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    @Singleton
    fun provideNoteDatabase(@ApplicationContext context: Context): NoteDatabase {
        return Room.databaseBuilder(
            context,
            NoteDatabase::class.java,
            "note_db"
        ).build()
    }

    @Provides
    @Singleton
    fun provideNoteDao(db: NoteDatabase): NoteDao = db.dao

    @Provides
    @Singleton
    fun provideNoteRepository(dao: NoteDao): NoteRepository = NoteRepositoryImpl(dao)

    @Provides
    @Singleton
    fun provideGeminiService(): GeminiService = GeminiService()
}
