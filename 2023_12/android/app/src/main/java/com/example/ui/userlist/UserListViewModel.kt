package com.example.ui.userlist

import com.example.domain.model.User
import kotlinx.coroutines.flow.StateFlow

interface UserListViewModel {
    val state: StateFlow<UserListScreenState>
    fun fetchRandomUsers(nbUsers: Int)
    fun saveUser(user: User)
}
