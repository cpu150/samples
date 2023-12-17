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

class MutableMainState(
    remoteRandomUsers: List<User> = emptyList(),
    randomUsersError: String? = null,
    localUsers: List<User> = emptyList(),
    localUsersError: String? = null,
    override var saveUsersError: String? = null,
) : MainState {

    override var remoteRandomUsers: List<User> = remoteRandomUsers
        set(value) {
            if (randomUsersError != null) {
                randomUsersError = null
            }
            if (field != value) {
                field = value
            }
        }

    override var randomUsersError: String? = randomUsersError
        set(value) {
            if (field != value) {
                remoteRandomUsers = emptyList()
                field = value
            }
        }

    override var localUsers: List<User> = localUsers
        set(value) {
            if (localUsersError != null) {
                localUsersError = null
            }
            if (field != value) {
                field = value
            }
        }

    override var localUsersError: String? = localUsersError
        set(value) {
            if (field != value) {
                localUsers = emptyList()
                field = value
            }
        }
}
