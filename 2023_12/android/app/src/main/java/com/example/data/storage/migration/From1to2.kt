package com.example.data.storage.migration

import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import com.example.data.storage.user.UserTable.columnAge
import com.example.data.storage.user.UserTable.columnBirthDate
import com.example.data.storage.user.UserTable.columnEmail
import com.example.data.storage.user.UserTable.columnGender
import com.example.data.storage.user.UserTable.columnPicLargeUrl
import com.example.data.storage.user.UserTable.columnPicMediumUrl
import com.example.data.storage.user.UserTable.columnPicSmallUrl
import com.example.data.storage.user.UserTable.tableName
import com.example.domain.Logger
import com.example.domain.model.UserGender
import javax.inject.Inject

class From1to2 @Inject constructor(
    private val logger: Logger,
) : Migration(1, 2) {

    override fun migrate(db: SupportSQLiteDatabase) {
        logger.d("Starting DB migration")
        try {
            migrateUser(db)
        } catch (e: Exception) {
            logger.e("DB migration failed", e)
        }
        logger.d("Ending DB migration")
    }

    private fun migrateUser(database: SupportSQLiteDatabase) {
        database.execSQL("ALTER TABLE `$tableName` ADD COLUMN `$columnGender` TEXT DEFAULT '${UserGender.UNKNOWN.value}'")
        database.execSQL("ALTER TABLE `$tableName` ADD COLUMN `$columnEmail` TEXT")
        database.execSQL("ALTER TABLE `$tableName` ADD COLUMN `$columnBirthDate` TEXT")
        database.execSQL("ALTER TABLE `$tableName` ADD COLUMN `$columnAge` INTEGER")
        database.execSQL("ALTER TABLE `$tableName` ADD COLUMN `$columnPicLargeUrl` TEXT")
        database.execSQL("ALTER TABLE `$tableName` ADD COLUMN `$columnPicMediumUrl` TEXT")
        database.execSQL("ALTER TABLE `$tableName` ADD COLUMN `$columnPicSmallUrl` TEXT")
    }
}
