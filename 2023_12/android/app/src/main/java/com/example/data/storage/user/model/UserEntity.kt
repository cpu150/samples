package com.example.data.storage.user.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import com.example.data.storage.user.UserTable.columnFirstName
import com.example.data.storage.user.UserTable.columnLastName
import com.example.data.storage.user.UserTable.columnTitle
import com.example.data.storage.user.UserTable.tableName

@Entity(
    primaryKeys = [columnTitle, columnFirstName, columnLastName],
    tableName = tableName,
)
data class UserEntity(
    @ColumnInfo(name = columnTitle) val title: String,
    @ColumnInfo(name = columnFirstName) val firstName: String,
    @ColumnInfo(name = columnLastName) val lastName: String,
)
