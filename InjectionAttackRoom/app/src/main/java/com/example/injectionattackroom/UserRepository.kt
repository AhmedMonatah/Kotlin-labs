package com.example.injectionattackroom

class UserRepository(private val dao: UserDao) {

    fun searchSafe(name: String): List<User> {
        return dao.searchSafe(name)
    }


}