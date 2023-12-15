package com.example.domain.model

import java.net.URL
import java.time.LocalDateTime

data class User(
    val title: UserTitle,
    val firstName: String,
    val lastName: String,
    val gender: UserGender,
    val email: String?,
    val birthDate: LocalDateTime?,
    val age: Int?,
    val picLargeUrl: URL?,
    val picMediumUrl: URL?,
    val picSmallUrl: URL?,
) {

    override fun toString() = """
    |User {
    | title = "$title"
    | firstName = "$firstName"
    | lastName = "$lastName"
    |}
    """.trimMargin()
}
