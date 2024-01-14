package com.example.data.di

import android.content.Context
import androidx.room.Room
import com.example.data.storage.Database
import com.example.data.storage.user.RoomUserMapperImp
import com.example.data.storage.user.StorageUserMapper
import com.example.domain.Logger
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Singleton
    @Provides
    fun provideDatabase(
        @ApplicationContext appContext: Context,
        logger: Logger,
    ) = Room.databaseBuilder(
        appContext.applicationContext,
        Database::class.java,
        Database.DatabaseName
    ).addMigrations(*Database.getMigrations(logger)).build()

    @Singleton
    @Provides
    fun provideUserDAO(db: Database) = db.getUserDao()

    @Singleton
    @Provides
    fun provideUserMapper(): StorageUserMapper = RoomUserMapperImp()
}
