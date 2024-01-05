package com.example.domain.state

sealed interface ScreenState<out S : State> {
    data object Initializing : ScreenState<Nothing>
    data class Loading(val progress: Float) : ScreenState<Nothing>
    data class Error(val message: String) : ScreenState<Nothing>
    class View<S : State>(val state: S) : ScreenState<S> {
        override fun toString() = """|{
            |  $state
            |}""".trimMargin()
    }
}
