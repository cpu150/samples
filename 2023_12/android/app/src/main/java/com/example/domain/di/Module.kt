package com.example.domain.di

import com.example.data.UserRepositoryImp
import com.example.domain.user.GetRandomUsersUseCase
import com.example.domain.user.GetRandomUsersUseCaseImp
import com.example.domain.user.LoadLocalUsersUseCase
import com.example.domain.user.LoadLocalUsersUseCaseImp
import com.example.domain.user.SaveUserUseCase
import com.example.domain.user.SaveUserUseCaseImp
import com.example.domain.user.UserRepository
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

    @Provides
    @Singleton
    fun provideLoadLocalUsersUseCase(
        useCase: LoadLocalUsersUseCaseImp
    ): LoadLocalUsersUseCase = useCase
}
