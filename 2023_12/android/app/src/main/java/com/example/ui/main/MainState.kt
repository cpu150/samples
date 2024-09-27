package com.example.ui.main

import com.example.domain.model.User
import com.example.ui.ScreenState

data class MainState(
    val screenState: ScreenState = ScreenState.Initializing,
    val remoteRandomUsers: List<User> = emptyList(),
    val randomUsersError: String? = null,
    val localUsers: List<User> = emptyList(),
    val localUsersError: String? = null,
    val saveUsersError: String? = null,
)
