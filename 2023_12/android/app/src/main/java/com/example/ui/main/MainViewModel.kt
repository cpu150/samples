package com.example.ui.main

import com.example.domain.model.User
import com.example.domain.state.ScreenState
import kotlinx.coroutines.flow.StateFlow

interface MainViewModel {
    val state: StateFlow<ScreenState<MainState>>
    suspend fun fetchRandomUsers(nbUsers: Int)
    suspend fun saveUser(user: User)
}
