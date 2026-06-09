package com.example.myapplication

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

fun main(){
    val job= GlobalScope.launch {
        repeat(10){
            delay(200)
            println("hey from monatah")
        }
    }

    val job2= CoroutineScope(Dispatchers.Default).launch {
        delay(200)
        println("hey from monatah coroutine")
    }
}