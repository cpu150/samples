package com.example.ui.main

import com.example.domain.model.User
import kotlinx.coroutines.flow.StateFlow

interface MainViewModel {
    val state: StateFlow<MainState>
    fun fetchRandomUsers(nbUsers: Int)
    fun saveUser(user: User)
}
