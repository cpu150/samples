package com.example.ui.userlist

import kotlinx.coroutines.flow.StateFlow

interface UserListViewModel {
    val state: StateFlow<UserListScreenState>
    fun fetchUsers(nbUsers: Int)
}
