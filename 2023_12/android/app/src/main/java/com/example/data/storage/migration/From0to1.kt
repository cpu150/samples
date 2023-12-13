package com.example.data.storage.migration

import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import com.example.data.storage.user.UserTable.tableName
import com.example.domain.Logger
import javax.inject.Inject

class From0to1 @Inject constructor(
    private val logger: Logger,
) : Migration(0, 1) {
    override fun migrate(db: SupportSQLiteDatabase) {
        logger.d("Starting DB migration from version 0 to 1")
        migrateUser(db)
        logger.d("Ending DB migration from version 0 to 1")
    }

    private fun migrateUser(database: SupportSQLiteDatabase) {
        database.execSQL(
            """
            ALTER TABLE `$tableName`
            ADD COLUMN pub_year INTEGER
            """
        )
    }
}
