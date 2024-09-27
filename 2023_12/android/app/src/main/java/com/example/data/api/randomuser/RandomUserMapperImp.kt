package com.example.data.api.randomuser

import com.example.common.toDateTime
import com.example.common.toURL
import com.example.data.api.randomuser.model.DobDTO.Companion.BIRTH_DATE_FORMAT
import com.example.data.api.randomuser.model.ErrorRandomUserDTO
import com.example.data.api.randomuser.model.GetRandomUsersDTO
import com.example.data.api.randomuser.model.RandomUserDTO
import com.example.data.api.randomuser.model.RandomUserNameDTO
import com.example.domain.Logger
import com.example.domain.model.User
import com.example.domain.model.UserGender
import com.example.domain.model.UserTitle
import com.example.domain.state.RemoteRequestState
import javax.inject.Inject

class RandomUserMapperImp @Inject constructor() : RandomUserMapper {

    override fun map(errorDTO: ErrorRandomUserDTO) = errorDTO.map() ?: RemoteRequestState.Error()

    override fun map(
        usersDTO: GetRandomUsersDTO,
        logger: Logger?,
    ) = usersDTO.map(logger)?.let {
        if (it.isEmpty()) {
            RemoteRequestState.Empty
        } else {
            RemoteRequestState.Success(it)
        }
    } ?: RemoteRequestState.Error()
}

fun ErrorRandomUserDTO.map() = takeIf { isValid() }?.let { RemoteRequestState.Error(msg = error) }

fun ErrorRandomUserDTO.isValid() = error != null

fun GetRandomUsersDTO.map(logger: Logger? = null) =
    takeIf { isValid() }?.results?.mapNotNull { it.map(logger) }

fun GetRandomUsersDTO.isValid() = results != null && info?.results?.takeIf { it >= 0 } != null

fun RandomUserDTO.map(logger: Logger? = null) = takeIf { isValid() }?.let {
    User(
        title = UserTitle.fromEntity(it.name?.title),
        firstName = it.name?.firstName ?: "",
        lastName = it.name?.lastName ?: "",
        gender = UserGender.fromEntity(it.gender, logger),
        email = it.email,
        birthDate = it.dob?.date?.toDateTime(BIRTH_DATE_FORMAT, logger),
        age = it.dob?.age,
        picLargeUrl = it.picture?.large?.toURL(logger),
        picMediumUrl = it.picture?.medium?.toURL(logger),
        picSmallUrl = it.picture?.thumbnail?.toURL(logger),
    )
}

fun RandomUserDTO.isValid() = name?.isValid() == true

fun RandomUserNameDTO.isValid() = title != null && firstName != null && lastName != null
