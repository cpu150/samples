package com.example.domain.user

import com.example.domain.model.User
import com.example.domain.state.RemoteRequestState

interface GetRandomUsersUseCase {
    suspend fun fetch(nbUsers: Int = 10): RemoteRequestState<List<User>>
}
