package com.example.data.storage.user.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.TypeConverters
import com.example.data.storage.TimestampConverter
import com.example.data.storage.user.UserTable.columnAge
import com.example.data.storage.user.UserTable.columnBirthDate
import com.example.data.storage.user.UserTable.columnEmail
import com.example.data.storage.user.UserTable.columnFirstName
import com.example.data.storage.user.UserTable.columnGender
import com.example.data.storage.user.UserTable.columnGenderDefaultValue
import com.example.data.storage.user.UserTable.columnLastName
import com.example.data.storage.user.UserTable.columnPicLargeUrl
import com.example.data.storage.user.UserTable.columnPicMediumUrl
import com.example.data.storage.user.UserTable.columnPicSmallUrl
import com.example.data.storage.user.UserTable.columnTitle
import com.example.data.storage.user.UserTable.tableName
import java.time.LocalDateTime

@Entity(
    primaryKeys = [columnTitle, columnFirstName, columnLastName],
    tableName = tableName,
)
@TypeConverters(TimestampConverter::class)
data class UserEntity(
    @ColumnInfo(name = columnTitle) val title: String,
    @ColumnInfo(name = columnFirstName) val firstName: String,
    @ColumnInfo(name = columnLastName) val lastName: String,
    @ColumnInfo(name = columnGender, defaultValue = columnGenderDefaultValue) val gender: String,
    @ColumnInfo(name = columnEmail) val email: String?,
    @ColumnInfo(name = columnBirthDate) val birthDate: LocalDateTime?,
    @ColumnInfo(name = columnAge) val age: Int?,
    @ColumnInfo(name = columnPicLargeUrl) val picLargeUrl: String?,
    @ColumnInfo(name = columnPicMediumUrl) val picMediumUrl: String?,
    @ColumnInfo(name = columnPicSmallUrl) val picSmallUrl: String?,
)
