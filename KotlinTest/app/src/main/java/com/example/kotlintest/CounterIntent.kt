package com.example.kotlintest

sealed class CounterIntent {
    object Increase : CounterIntent()
    object Decrease : CounterIntent()
}