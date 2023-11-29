package com.example.ui.main

import kotlinx.coroutines.flow.StateFlow

interface MainViewModel {
    val state: StateFlow<MainState>
}
