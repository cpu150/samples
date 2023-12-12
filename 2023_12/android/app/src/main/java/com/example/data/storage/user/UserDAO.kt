package com.example.data.storage.user

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.data.storage.user.UserTable.tableName
import com.example.data.storage.user.model.UserEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface UserDAO {
    @Query(
        """
        SELECT *
        FROM $tableName
        """
    )
    fun getAllUsers(): Flow<List<UserEntity>?>

    @Query(
        """
        SELECT *
        FROM $tableName
        WHERE title=:title AND firstName=:firstName AND lastName=:lastName
        LIMIT 1
        """
    )
    fun getUser(title: String, firstName: String, lastName: String): UserEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun addUser(user: UserEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun addUsers(users: List<UserEntity>): List<Long>

    @Delete
    fun deleteUser(user: UserEntity): Int

    @Delete
    fun deleteUsers(users: List<UserEntity>): Int
}
