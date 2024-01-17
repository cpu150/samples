package com.example.domain.user

import com.example.domain.Logger
import javax.inject.Inject

class LoadLocalUsersUseCaseImp @Inject constructor(
    private val repository: UserRepository,
    private val logger: Logger?,
) : LoadLocalUsersUseCase {

    override suspend fun all() = repository.getLocalUsers(logger)
}
