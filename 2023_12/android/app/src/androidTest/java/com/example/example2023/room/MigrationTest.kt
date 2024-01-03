package com.example.example2023.room

import android.content.ContentValues
import android.database.sqlite.SQLiteDatabase.CONFLICT_REPLACE
import androidx.room.Room
import androidx.room.testing.MigrationTestHelper
import androidx.sqlite.db.SupportSQLiteDatabase
import androidx.sqlite.db.framework.FrameworkSQLiteOpenHelperFactory
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import com.example.data.storage.Database
import com.example.data.storage.user.model.UserEntity
import com.example.domain.model.UserGender
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.test.runTest
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import java.io.IOException
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

@RunWith(AndroidJUnit4::class)
class MigrationTest {

    companion object {
        private const val TEST_DB = "${Database.DatabaseName}-test"
    }

    @get:Rule
    val helper = MigrationTestHelper(
        InstrumentationRegistry.getInstrumentation(),
        Database::class.java,
        listOf(),
        FrameworkSQLiteOpenHelperFactory(),
    )

    private val users = listOf(
        UserEntity(
            title = "Miss",
            firstName = "John",
            lastName = "Doe",
            gender = "female",
            email = "john@example.com",
            birthDate = LocalDateTime.now(),
            age = 34,
            picLargeUrl = "https://example.com/picLarge.jpg",
            picMediumUrl = "https://example.com/picMedium.jpg",
            picSmallUrl = "https://example.com/picSmall.jpg",
        )
    )

    private val insertFunctions = listOf<(SupportSQLiteDatabase) -> Unit>(
        { db -> insertUsersV1(db) },
//        { db -> insertUsersV2(db) },
    )

    private val compareFunctions = listOf<(UserEntity, UserEntity) -> Boolean>(
        { user, dbUser -> compareUserFromV1ToV2(user, dbUser) },
//        { user, dbUser -> compareUserFromV2ToV3(user, dbUser) },
    )

    private fun insertUsersV1(db: SupportSQLiteDatabase) = with(db) {
        insert(
            "user",
            CONFLICT_REPLACE,
            ContentValues(users.size).apply {
                users.forEach { user ->
                    put("title", user.title)
                    put("firstName", user.firstName)
                    put("lastName", user.lastName)
                }
            }
        )
    }

    private fun insertUsersV2(db: SupportSQLiteDatabase) = with(db) {
        val df = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSS")
        insert(
            "user",
            CONFLICT_REPLACE,
            ContentValues(users.size).apply {
                users.forEach { user ->
                    put("title", user.title)
                    put("firstName", user.firstName)
                    put("lastName", user.lastName)
                    put("gender", user.gender)
                    put("email", user.email)
                    put("birthDate", df.format(user.birthDate))
                    put("age", user.age)
                    put("picLargeUrl", user.picLargeUrl)
                    put("picMediumUrl", user.picMediumUrl)
                    put("picSmallUrl", user.picSmallUrl)
                }
            }
        )
    }

    private fun compareUserFromV1ToV2(user: UserEntity, dbUser: UserEntity) = with(dbUser) {
        user.title == title &&
                user.firstName == firstName &&
                user.lastName == lastName &&
                gender == UserGender.UNKNOWN.entityValue &&
                email == null &&
                birthDate == null &&
                age == null &&
                picLargeUrl == null &&
                picMediumUrl == null &&
                picSmallUrl == null
    }

    private fun compareUserFromV2ToV3(user: UserEntity, dbUser: UserEntity) = with(dbUser) {
        // TODO
        false
    }

    @Test
    @Throws(IOException::class)
    fun migrateAll() {
        // Create first version of the database
        helper.createDatabase(TEST_DB, 1).apply {
            insertFunctions.last()(this)
            close()
        }

        // Open latest version of the database. Room validates the schema once all migrations execute
        Room.databaseBuilder(
            InstrumentationRegistry.getInstrumentation().targetContext,
            Database::class.java,
            TEST_DB
        ).addMigrations(*Database.getMigrations()).build().apply {
            runTest {
                val dbUsers = this@apply.getUserDao().getAll().firstOrNull()
                assert(dbUsers?.size == users.size)
                { "Database after migration contains ${dbUsers?.size} users instead of ${users.size} - $dbUsers" }

                dbUsers?.forEachIndexed { index, dbUser ->
                    assert(compareFunctions.last()(users[index], dbUser))
                    { "compareFunctions.last() - ${users[index]} != $dbUser" }
                }
            }
            openHelper.writableDatabase.close()
        }
    }

    @Test
    @Throws(IOException::class)
    fun migrateOneByOne() {
        var version = 1
        val latestVersion = insertFunctions.size + 1
        val migration = Database.getMigrations()

        while (version < latestVersion) {
            // Create first version of the database
            helper.createDatabase(TEST_DB, version).apply {
                insertFunctions[version - 1](this)
                close()
            }

            // Open latest version of the database. Room validates the schema once all migrations execute
            Room.databaseBuilder(
                InstrumentationRegistry.getInstrumentation().targetContext,
                Database::class.java,
                TEST_DB
            ).addMigrations(migration[version - 1]).build().apply {
                runTest {
                    val dbUsers = this@apply.getUserDao().getAll().firstOrNull()
                    assert(dbUsers?.size == users.size)
                    { "Database after migration contains ${dbUsers?.size} users instead of ${users.size} - $dbUsers" }

                    dbUsers?.forEachIndexed { index, dbUser ->
                        assert(compareFunctions.last()(users[index], dbUser))
                        { "compareFunctions.last() - ${users[index]} != $dbUser" }
                    }
                }
                openHelper.writableDatabase.close()
            }

            version++
        }
    }
}
