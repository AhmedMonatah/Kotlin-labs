package com.example.injectionattackroom

import androidx.room3.Database
import androidx.room3.RoomDatabase

@Database(entities = [User::class], version = 1)
abstract class AppDatabase : RoomDatabase() {
    abstract fun userDao(): UserDao
}