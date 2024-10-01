package com.example.ui.di

import com.example.ui.nav.NavArgParser
import com.example.ui.nav.UserDetailsScreen
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
    fun provideUserDetailsScreenNavArgParser(): NavArgParser<UserDetailsScreen> =
        UserDetailsScreen.Companion
}
