package com.example.domain.user

import com.example.domain.model.User
import com.example.domain.state.LocalRequestState

interface SaveUserUseCase {
    suspend fun save(user: User): LocalRequestState<User>
}
