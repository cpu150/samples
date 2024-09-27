package com.example.domain.model

import android.os.Parcelable
import com.example.common.LocalDateTimeSerializer
import com.example.common.URLSerializer
import com.example.common.toUri
import com.example.data.storage.user.model.UserEntity
import kotlinx.parcelize.Parcelize
import kotlinx.serialization.Serializable
import java.net.URL
import java.time.LocalDateTime

@Serializable
@Parcelize
data class User(
    val title: UserTitle,
    val firstName: String,
    val lastName: String,
    val gender: UserGender,
    val email: String?,
    @Serializable(with = LocalDateTimeSerializer::class) val birthDate: LocalDateTime?,
    val age: Int?,
    @Serializable(with = URLSerializer::class) val picLargeUrl: URL?,
    @Serializable(with = URLSerializer::class) val picMediumUrl: URL?,
    @Serializable(with = URLSerializer::class) val picSmallUrl: URL?,
) : Parcelable {

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
            picLargeUrl?.toUri() == user.picLargeUrl?.toUri() &&
            picMediumUrl?.toUri() == user.picMediumUrl?.toUri() &&
            picSmallUrl?.toUri() == user.picSmallUrl?.toUri()

    private fun equals(user: UserEntity) = title == UserTitle.fromEntity(user.title) &&
            firstName == user.firstName &&
            lastName == user.lastName &&
            gender == UserGender.fromEntity(user.gender) &&
            email == user.email &&
            birthDate == user.birthDate &&
            age == user.age &&
            picLargeUrl?.toUri() == user.picLargeUrl?.toUri() &&
            picMediumUrl?.toUri() == user.picMediumUrl?.toUri() &&
            picSmallUrl?.toUri() == user.picSmallUrl?.toUri()

    override fun hashCode(): Int {
        var result = title.hashCode()
        result = 31 * result + firstName.hashCode()
        result = 31 * result + lastName.hashCode()
        result = 31 * result + gender.hashCode()
        result = 31 * result + (email?.hashCode() ?: 0)
        result = 31 * result + (birthDate?.hashCode() ?: 0)
        result = 31 * result + (age ?: 0)
        result = 31 * result + picLargeUrl?.toUri().hashCode()
        result = 31 * result + picMediumUrl?.toUri().hashCode()
        result = 31 * result + picSmallUrl?.toUri().hashCode()
        return result
    }
}
