package com.example.data.storage.user

import com.example.data.storage.user.model.UserEntity
import com.example.domain.Logger
import com.example.domain.model.User
import com.example.domain.state.RemoteRequestState

interface StorageUserMapper {
    fun map(userEntities: List<UserEntity>, logger: Logger? = null): RemoteRequestState<List<User>>
    fun map(user: User): UserEntity
}
