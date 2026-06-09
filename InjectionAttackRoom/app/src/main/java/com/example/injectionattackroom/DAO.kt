package com.example.injectionattackroom

import androidx.room3.Dao
import androidx.room3.Query

@Dao
interface UserDao {

    @Query("SELECT * FROM User WHERE name = ':name")
    fun searchSafe(name: String): List<User>


    @Query("SELECT * FROM User")
    fun getAll(): List<User>
}