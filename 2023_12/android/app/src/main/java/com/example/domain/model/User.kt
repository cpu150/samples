package com.example.domain.model

data class User(
    val title: UserTitle,
    val firstName: String,
    val lastName: String,
) {
    override fun toString() = """
    |User {
    | title = "$title"
    | firstName = "$firstName"
    | lastName = "$lastName"
    |}
    """.trimMargin()
}
