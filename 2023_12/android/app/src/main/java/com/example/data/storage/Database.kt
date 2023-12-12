package com.example.data.storage

import androidx.room.RoomDatabase
import com.example.data.storage.migration.From0to1
import com.example.data.storage.user.UserDAO
import com.example.data.storage.user.model.UserEntity
import com.example.domain.Logger

@androidx.room.Database(
    version = 0,
    entities = [
        UserEntity::class,
    ]
)
abstract class Database : RoomDatabase() {

    companion object {
        const val DatabaseName = "AppDatabase"

        fun getMigrations(logger: Logger) = arrayOf(
            From0to1(logger),
        )
    }

    abstract fun getUserDao(): UserDAO
}
