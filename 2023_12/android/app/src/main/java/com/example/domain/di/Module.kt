package com.example.domain.di

import com.example.data.randomuser.RepositoryImp
import com.example.domain.randomuser.GetRandomUsersUseCase
import com.example.domain.randomuser.GetRandomUsersUseCaseImp
import com.example.domain.randomuser.RandomUserRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object Module {

    @Provides
    @Singleton
    fun provideRandomUserRepository(repository: RepositoryImp): RandomUserRepository = repository

    @Provides
    @Singleton
    fun provideGetRandomUserUseCase(
        useCase: GetRandomUsersUseCaseImp
    ): GetRandomUsersUseCase = useCase
}
