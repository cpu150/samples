package com.example.domain.user

import com.example.domain.model.User
import javax.inject.Inject

class DeleteLocalUserUseCaseImp @Inject constructor(
    private val repository: UserRepository
) : DeleteLocalUserUseCase {

    override suspend fun delete(user: User) = repository.deleteLocalUser(user)
}
