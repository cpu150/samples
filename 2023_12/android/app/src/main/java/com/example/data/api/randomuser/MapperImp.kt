package com.example.data.api.randomuser

import com.example.data.api.randomuser.model.ErrorRandomUserDTO
import com.example.data.api.randomuser.model.GetRandomUsersDTO
import com.example.data.api.randomuser.model.RandomUserDTO
import com.example.data.api.randomuser.model.RandomUserNameDTO
import com.example.domain.model.User
import com.example.domain.state.DataState
import javax.inject.Inject

class MapperImp @Inject constructor() : Mapper {

    override fun map(errorDTO: ErrorRandomUserDTO) = errorDTO.map() ?: DataState.Error()

    override fun map(usersDTO: GetRandomUsersDTO) = usersDTO.map()?.let {
        if (it.isEmpty()) {
            DataState.Empty
        } else {
            DataState.Success(it)
        }
    } ?: DataState.Error()
}

fun ErrorRandomUserDTO.map() = takeIf { isValid() }?.let { DataState.Error(msg = error) }

fun ErrorRandomUserDTO.isValid() = error != null

fun GetRandomUsersDTO.map(): List<User>? = takeIf { isValid() }?.results?.mapNotNull { it.map() }

fun GetRandomUsersDTO.isValid() = results != null && info?.results?.takeIf { it >= 0 } != null

fun RandomUserDTO.map() = takeIf { isValid() }
    ?.let {
        User(
            name = name?.map() ?: ""
        )
    }

fun RandomUserDTO.isValid() = name != null

fun RandomUserNameDTO.map() = takeIf { isValid() }
    ?.let { "$title $firstName $lastName" }

fun RandomUserNameDTO.isValid() = title != null && firstName != null && lastName != null
