package com.example.data.storage.user

object UserTable {
    const val tableName = "user"
    const val columnTitle = "title"
    const val columnFirstName = "firstName"
    const val columnLastName = "lastName"
    val primaryKeys = listOf(columnTitle, columnFirstName, columnLastName)
}
