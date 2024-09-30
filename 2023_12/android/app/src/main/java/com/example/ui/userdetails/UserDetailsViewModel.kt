package com.example.ui.userdetails

import kotlinx.coroutines.flow.StateFlow

interface UserDetailsViewModel {
    val state: StateFlow<UserDetailsScreenState>
    fun saveUser()
    fun deleteUser()
}
