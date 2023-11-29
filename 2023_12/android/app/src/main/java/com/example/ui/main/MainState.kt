package com.example.ui.main

sealed class MainState {
    data object Loading : MainState()
    data class Success<T>(val data: T) : MainState()
    data class Error(val message: String? = null) : MainState()
}
