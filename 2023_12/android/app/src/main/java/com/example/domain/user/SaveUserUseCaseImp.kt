package com.example.domain.user

import com.example.domain.model.User
import javax.inject.Inject

class SaveUserUseCaseImp @Inject constructor(
    private val repository: UserRepository
) : SaveUserUseCase {

    override suspend fun save(user: User) = repository.saveLocalUser(user)
}
