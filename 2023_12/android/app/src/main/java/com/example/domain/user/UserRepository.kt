package com.example.domain.user

import com.example.domain.Logger
import com.example.domain.model.User
import com.example.domain.state.LocalRequestState
import com.example.domain.state.RemoteRequestState
import kotlinx.coroutines.flow.Flow

interface UserRepository {
    suspend fun fetchRemoteRandomUsers(numberOfUser: Int): RemoteRequestState<List<User>>
    suspend fun saveLocalUser(user: User): LocalRequestState<User>
    suspend fun getLocalUsers(logger: Logger? = null): Flow<LocalRequestState<List<User>>>
}
