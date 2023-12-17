package com.example.domain.user

import com.example.domain.model.User
import com.example.domain.state.LocalRequestState
import kotlinx.coroutines.flow.Flow

interface LoadLocalUsersUseCase {
    suspend fun all(): Flow<LocalRequestState<List<User>>>
}
