package com.example.data.randomuser

import com.example.data.randomuser.model.ErrorRandomUserDTO
import com.example.data.randomuser.model.GetRandomUsersDTO
import com.example.domain.DataResponse
import com.example.domain.model.User

interface Mapper {
    fun map(errorDTO: ErrorRandomUserDTO): DataResponse.Error
    fun map(usersDTO: GetRandomUsersDTO): DataResponse<List<User>>
}
