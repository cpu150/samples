package com.example.domain.randomuser

import com.example.domain.model.User
import com.example.domain.state.DataState

interface RandomUserRepository {
    suspend fun fetchRandomUsers(numberOfUser: Int): DataState<List<User>>
}
