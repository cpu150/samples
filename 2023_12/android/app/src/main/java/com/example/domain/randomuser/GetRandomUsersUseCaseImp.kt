package com.example.domain.randomuser

import javax.inject.Inject

class GetRandomUsersUseCaseImp @Inject constructor(
    private val repository: UserRepository,
) : GetRandomUsersUseCase {

    override suspend fun fetch(nbUsers: Int) = repository.fetchRemoteRandomUsers(nbUsers)
}
