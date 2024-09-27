package com.example.ui.userlist

import com.example.domain.model.User
import com.example.ui.ScreenState

data class UserListScreenState(
    val screenState: ScreenState = ScreenState.Initializing,
    val remoteRandomUsers: List<User> = emptyList(),
    val randomUsersError: String? = null,
    val localUsers: List<User> = emptyList(),
    val localUsersError: String? = null,
    val saveUsersError: String? = null,
)
