package com.example.domain.randomuser

import javax.inject.Inject

class GetRandomUsersUseCaseImp @Inject constructor(
    private val repository: RandomUserRepository,
) : GetRandomUsersUseCase {

    suspend fun fetch(nbUsers: Int = 10) = repository.fetchRandomUsers(nbUsers)
}
