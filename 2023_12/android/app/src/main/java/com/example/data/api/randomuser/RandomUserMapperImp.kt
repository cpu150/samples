package com.example.data.api.randomuser

import com.example.data.api.randomuser.model.ErrorRandomUserDTO
import com.example.data.api.randomuser.model.GetRandomUsersDTO
import com.example.data.api.randomuser.model.RandomUserDTO
import com.example.data.api.randomuser.model.RandomUserNameDTO
import com.example.domain.model.User
import com.example.domain.model.UserTitle
import com.example.domain.state.RemoteRequestState
import javax.inject.Inject

class RandomUserMapperImp @Inject constructor() : RandomUserMapper {

    override fun map(errorDTO: ErrorRandomUserDTO) = errorDTO.map() ?: RemoteRequestState.Error()

    override fun map(usersDTO: GetRandomUsersDTO) = usersDTO.map()?.let {
        if (it.isEmpty()) {
            RemoteRequestState.Empty
        } else {
            RemoteRequestState.Success(it)
        }
    } ?: RemoteRequestState.Error()
}

fun ErrorRandomUserDTO.map() = takeIf { isValid() }?.let { RemoteRequestState.Error(msg = error) }

fun ErrorRandomUserDTO.isValid() = error != null

fun GetRandomUsersDTO.map(): List<User>? = takeIf { isValid() }?.results?.mapNotNull { it.map() }

fun GetRandomUsersDTO.isValid() = results != null && info?.results?.takeIf { it >= 0 } != null

fun RandomUserDTO.map() = takeIf { isValid() }?.let {
    User(
        title = UserTitle.from(it.name?.title),
        firstName = it.name?.firstName ?: "",
        lastName = it.name?.lastName ?: "",
    )
}

fun RandomUserDTO.isValid() = name?.isValid() == true

fun RandomUserNameDTO.isValid() = title != null && firstName != null && lastName != null
