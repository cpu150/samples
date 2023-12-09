package com.example.data.api.randomuser

import com.example.data.api.randomuser.model.ErrorRandomUserDTO
import com.example.data.api.randomuser.model.GetRandomUsersDTO
import com.example.domain.model.User
import com.example.domain.state.DataState

interface Mapper {
    fun map(errorDTO: ErrorRandomUserDTO): DataState.Error
    fun map(usersDTO: GetRandomUsersDTO): DataState<List<User>>
}
