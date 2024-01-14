package com.example.data.storage.user

import com.example.common.toURL
import com.example.data.storage.user.model.UserEntity
import com.example.domain.Logger
import com.example.domain.model.User
import com.example.domain.model.UserGender
import com.example.domain.model.UserTitle
import com.example.domain.state.RemoteRequestState

class RoomUserMapperImp : StorageUserMapper {
    override fun map(userEntities: List<UserEntity>, logger: Logger?) =
        RemoteRequestState.Success(userEntities.map { it.map(logger) })

    override fun map(user: User): UserEntity = user.map()
}

fun UserEntity.map(logger: Logger? = null) = User(
    title = UserTitle.fromEntity(title, logger),
    firstName = firstName,
    lastName = lastName,
    gender = UserGender.fromEntity(gender, logger),
    email = email,
    birthDate = birthDate,
    age = age,
    picLargeUrl = picLargeUrl?.toURL(logger),
    picMediumUrl = picMediumUrl?.toURL(logger),
    picSmallUrl = picSmallUrl?.toURL(logger),
)

fun User.map() = UserEntity(
    title = title.entityValue,
    firstName = firstName,
    lastName = lastName,
    gender = gender.entityValue,
    email = email,
    birthDate = birthDate,
    age = age,
    picLargeUrl = picLargeUrl?.toString(),
    picMediumUrl = picMediumUrl?.toString(),
    picSmallUrl = picSmallUrl?.toString(),
)
