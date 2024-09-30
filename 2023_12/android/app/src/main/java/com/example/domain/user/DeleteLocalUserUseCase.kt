package com.example.domain.user

import com.example.domain.model.User
import com.example.domain.state.LocalRequestState

interface DeleteLocalUserUseCase {
    suspend fun delete(user: User): LocalRequestState<User>
}
