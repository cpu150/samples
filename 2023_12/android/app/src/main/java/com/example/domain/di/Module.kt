package com.example.domain.di

import com.example.data.UserRepositoryImp
import com.example.domain.randomuser.GetRandomUsersUseCase
import com.example.domain.randomuser.GetRandomUsersUseCaseImp
import com.example.domain.randomuser.SaveUserUseCase
import com.example.domain.randomuser.SaveUserUseCaseImp
import com.example.domain.randomuser.UserRepository
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
    fun provideRandomUserRepository(repository: UserRepositoryImp): UserRepository = repository

    @Provides
    @Singleton
    fun provideGetRandomUserUseCase(
        useCase: GetRandomUsersUseCaseImp
    ): GetRandomUsersUseCase = useCase

    @Provides
    @Singleton
    fun provideSaveUserUseCase(useCase: SaveUserUseCaseImp): SaveUserUseCase = useCase
}
