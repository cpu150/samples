package com.example.data.storage.migration

import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import com.example.data.storage.user.UserTable.columnFirstName
import com.example.data.storage.user.UserTable.columnLastName
import com.example.data.storage.user.UserTable.columnTitle
import com.example.data.storage.user.UserTable.primaryKeys
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
        val tmpTableName = "${tableName}_tmp"
        val primaryKeys = primaryKeys.joinToString(separator = ",") { "`$it`" }
        logger.d("primaryKeys: $primaryKeys")
        database.execSQL(
            """
            CREATE TABLE IF NOT EXISTS `$tmpTableName` (
                `$columnTitle` TEXT NOT NULL,
                `$columnLastName` TEXT NOT NULL,
                `$columnFirstName` TEXT NOT NULL,
                PRIMARY KEY($primaryKeys)
            )
            """
        )
        database.execSQL(
            """
                INSERT INTO `$tmpTableName` (`$columnTitle`, `$columnFirstName`,`$columnLastName`)
                SELECT `$columnTitle`, `$columnFirstName`,`$columnLastName`
                FROM $tableName
            """
        )
        database.execSQL("DROP TABLE `$tableName`")
        database.execSQL("ALTER TABLE `$tmpTableName` RENAME TO `$tableName`")
    }
}
