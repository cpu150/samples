package com.example.domain.randomuser

import com.example.domain.DataResponse
import com.example.domain.model.User

interface RandomUserRepository {
    suspend fun fetchRandomUsers(numberOfUser: Int): DataResponse<List<User>>
}
