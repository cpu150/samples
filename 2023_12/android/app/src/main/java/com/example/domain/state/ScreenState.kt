package com.example.domain.state

sealed interface ScreenState {
    data object Initializing : ScreenState
    data object Loading : ScreenState
    data class Error(val message: String) : ScreenState
    data class View<S : State>(val state: S) : ScreenState
}
