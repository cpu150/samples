package com.example.ui.userdetails

import com.example.domain.model.User

data class UserDetailsScreenState(
    val user: User,
    val isUserSaved: Boolean? = null,
    val localUsersError: String? = null,
    val saveUsersError: String? = null,
    val deleteUsersError: String? = null,
)
