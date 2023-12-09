package com.example.domain.randomuser

import com.example.domain.model.User
import com.example.domain.state.DataState

interface GetRandomUsersUseCase {
    suspend fun fetch(nbUsers: Int = 10): DataState<List<User>>
}
