package com.example.data.storage.user

import com.example.data.storage.user.model.UserEntity
import com.example.domain.model.User
import com.example.domain.model.UserTitle
import com.example.domain.state.RemoteRequestState

class UserMapperImp : UserMapper {
    override fun map(userEntities: List<UserEntity>) =
        RemoteRequestState.Success(userEntities.map { it.map() })

    override fun map(user: User): UserEntity = user.map()
}

fun UserEntity.map() = User(
    title = UserTitle.from(title),
    firstName = firstName,
    lastName = lastName,
)

fun User.map() = UserEntity(
    title = title.value,
    firstName = firstName,
    lastName = lastName,
)
