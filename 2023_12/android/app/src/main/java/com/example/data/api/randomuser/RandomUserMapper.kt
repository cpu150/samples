package com.example.data.api.randomuser

import com.example.data.api.randomuser.model.ErrorRandomUserDTO
import com.example.data.api.randomuser.model.GetRandomUsersDTO
import com.example.domain.Logger
import com.example.domain.model.User
import com.example.domain.state.RemoteRequestState

interface RandomUserMapper {
    fun map(errorDTO: ErrorRandomUserDTO): RemoteRequestState.Error
    fun map(usersDTO: GetRandomUsersDTO, logger: Logger? = null): RemoteRequestState<List<User>>
}
