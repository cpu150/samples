package com.example.data.di

import com.example.data.android.logger.LoggerImp
import com.example.domain.Logger
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AndroidModule {

    @Singleton
    @Provides
    fun provideUserMapper(): Logger = LoggerImp()
}
