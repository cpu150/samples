package com.example.ui

sealed interface ScreenState {
    data object Initializing : ScreenState
    data class Loading(val progress: Float = 0f) : ScreenState
    data class Error(val message: String) : ScreenState
    data object Loaded : ScreenState
}
