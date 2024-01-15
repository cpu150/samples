package com.example.domain.model

import com.example.common.toURL
import com.example.data.storage.user.model.UserEntity
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
    | gender = "$gender"
    | email = "$email"
    | birthDate = "$birthDate"
    | age = "$age"
    | picLargeUrl = "$picLargeUrl"
    | picMediumUrl = "$picMediumUrl"
    | picSmallUrl = "$picSmallUrl"
    |}
    """.trimMargin()

    override fun equals(other: Any?) = when (other) {
        is User -> equals(other)
        is UserEntity -> equals(other)
        else -> super.equals(other)
    }

    private fun equals(user: User) = title == user.title &&
            firstName == user.firstName &&
            lastName == user.lastName &&
            gender == user.gender &&
            email == user.email &&
            birthDate == user.birthDate &&
            age == user.age &&
            picLargeUrl == user.picLargeUrl &&
            picMediumUrl == user.picMediumUrl &&
            picSmallUrl == user.picSmallUrl

    private fun equals(user: UserEntity) = title == UserTitle.fromEntity(user.title) &&
            firstName == user.firstName &&
            lastName == user.lastName &&
            gender == UserGender.fromEntity(user.gender) &&
            email == user.email &&
            birthDate == user.birthDate &&
            age == user.age &&
            picLargeUrl == user.picLargeUrl?.toURL() &&
            picMediumUrl == user.picMediumUrl?.toURL() &&
            picSmallUrl == user.picSmallUrl?.toURL()

    override fun hashCode(): Int {
        var result = title.hashCode()
        result = 31 * result + firstName.hashCode()
        result = 31 * result + lastName.hashCode()
        result = 31 * result + gender.hashCode()
        result = 31 * result + (email?.hashCode() ?: 0)
        result = 31 * result + (birthDate?.hashCode() ?: 0)
        result = 31 * result + (age ?: 0)
        result = 31 * result + (picLargeUrl?.hashCode() ?: 0)
        result = 31 * result + (picMediumUrl?.hashCode() ?: 0)
        result = 31 * result + (picSmallUrl?.hashCode() ?: 0)
        return result
    }
}
