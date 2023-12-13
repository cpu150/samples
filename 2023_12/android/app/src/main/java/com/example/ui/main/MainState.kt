package com.example.ui.main

import com.example.domain.model.User
import com.example.domain.state.State

interface MainState : State {
    val users: List<User>
    val userFetchError: String?
    val userSaveError: String?
}

class MutableMainState(
    users: List<User> = emptyList(),
    userFetchError: String? = null,
    userSaveError: String? = null,
) : MainState {

    override var users: List<User> = users
        set(value) {
            if (userFetchError != null) {
                userFetchError = null
            }
            if (userSaveError != null) {
                userSaveError = null
            }
            if (field != value) {
                field = value
            }
        }

    override var userFetchError: String? = userFetchError
        set(value) {
            if (field != value) {
                users = emptyList()
                userSaveError = null
                field = value
            }
        }

    override var userSaveError: String? = userSaveError
        set(value) {
            if (field != value) {
                users = emptyList()
                userFetchError = null
                field = value
            }
        }
}
