package com.example.injectionattackroom

import androidx.room3.Entity
import androidx.room3.PrimaryKey

@Entity
data class User(
    @PrimaryKey val id: Int,
    val name: String,
    val email: String
)