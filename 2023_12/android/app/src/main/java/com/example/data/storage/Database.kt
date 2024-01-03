package com.example.data.storage

import androidx.room.RoomDatabase
import com.example.data.storage.migration.From1to2
import com.example.data.storage.user.UserDAO
import com.example.data.storage.user.model.UserEntity
import com.example.domain.Logger

@androidx.room.Database(
    version = 2,
    entities = [
        UserEntity::class,
    ],
    // It is an auto migration example, in this project the manual migration system is used
//    autoMigrations = [
//        AutoMigration(from = 1, to = 2)
//    ],
)
abstract class Database : RoomDatabase() {

    companion object {
        const val DatabaseName = "AppDatabase"

        fun getMigrations(logger: Logger? = null) = arrayOf(
            From1to2(logger),
        )
    }

    abstract fun getUserDao(): UserDAO
}
