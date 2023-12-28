package com.example.ui.main

import com.example.domain.model.User
import com.example.domain.state.State

interface MainState : State {
    val remoteRandomUsers: List<User>
    val randomUsersError: String?
    val localUsers: List<User>
    val localUsersError: String?
    val saveUsersError: String?
}

data class MutableMainState(
    override var remoteRandomUsers: List<User> = emptyList(),
    override var randomUsersError: String? = null,
    override var localUsers: List<User> = emptyList(),
    override var localUsersError: String? = null,
    override var saveUsersError: String? = null,
) : MainState {

    override fun toString() = """
        remoteRandomUsers: $remoteRandomUsers
        randomUsersError: $randomUsersError
        localUsers: $localUsers
        localUsersError: $localUsersError
        saveUsersError: $saveUsersError
    """.trimIndent()
}
