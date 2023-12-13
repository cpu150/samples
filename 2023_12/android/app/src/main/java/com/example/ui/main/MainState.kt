package com.example.ui.main

import com.example.domain.model.User
import com.example.domain.state.State

data class MainState(
    val users: List<User> = emptyList(),
    val userFetchError: String? = null,
    val userSaveError: String? = null,
) : State
