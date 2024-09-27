package com.example.data.api.randomuser

import com.example.common.toDateTime
import com.example.common.toURL
import com.example.data.api.randomuser.model.DobDTO
import com.example.data.api.randomuser.model.GetRandomUsersDTO
import com.example.data.api.randomuser.model.InfoRandomUserDTO
import com.example.data.api.randomuser.model.RandomUserDTO
import com.example.data.api.randomuser.model.RandomUserNameDTO
import com.example.data.api.randomuser.model.RandomUserPictureDTO
import com.example.data.storage.user.model.UserEntity
import com.example.domain.model.User
import com.example.domain.model.UserGender
import com.example.domain.model.UserTitle
import com.example.domain.state.RemoteRequestState
import java.net.URL
import java.time.LocalDateTime

object UserTestUtility {

    val DEFAULT_TITLE = UserTitle.UNKNOWN
    const val DEFAULT_TITLE_STR = "title"
    const val DEFAULT_FIRST_NAME = "First Name"
    const val DEFAULT_LAST_NAME = "Last Name"
    const val DEFAULT_EMAIL = "user@example.com"
    const val DEFAULT_IMG_LARGE = "https://example.com/large"
    const val DEFAULT_IMG_MEDIUM = "https://example.com/medium"
    const val DEFAULT_IMG_THUMB = "https://example.com/thumbnail"
    val DEFAULT_GENDER = UserGender.UNKNOWN
    const val DEFAULT_GENDER_STR = "gender"
    const val DEFAULT_DOB_STR = "1963-10-25T13:56:32.813Z"
    val DEFAULT_DOB_DATE = DEFAULT_DOB_STR.toDateTime(DobDTO.BIRTH_DATE_FORMAT)
    const val DEFAULT_AGE = 30

    fun <T> getMapperResult(
        result: RemoteRequestState<T>,
        error: (result: RemoteRequestState<T>) -> Any
    ) = when (result) {
        is RemoteRequestState.Success -> result.data
        else -> {
            assert(false) { error(result) }
            null
        }
    }

    fun getDomainUser(
        title: UserTitle = DEFAULT_TITLE,
        firstName: String = DEFAULT_FIRST_NAME,
        lastName: String = DEFAULT_LAST_NAME,
        gender: UserGender = DEFAULT_GENDER,
        email: String? = DEFAULT_EMAIL,
        birthDate: LocalDateTime? = DEFAULT_DOB_DATE,
        age: Int? = DEFAULT_AGE,
        picLargeUrl: URL? = DEFAULT_IMG_LARGE.toURL(),
        picMediumUrl: URL? = DEFAULT_IMG_MEDIUM.toURL(),
        picSmallUrl: URL? = DEFAULT_IMG_THUMB.toURL(),
    ) = User(
        title = title,
        firstName = firstName,
        lastName = lastName,
        gender = gender,
        email = email,
        birthDate = birthDate,
        age = age,
        picLargeUrl = picLargeUrl,
        picMediumUrl = picMediumUrl,
        picSmallUrl = picSmallUrl,
    )

    fun getRandomUser(user: RandomUserDTO = getUserDTO()) = getRandomUsers(listOf(user))

    fun getRandomUsers(
        users: List<RandomUserDTO> = listOf(getUserDTO())
    ) = GetRandomUsersDTO(results = users, info = InfoRandomUserDTO(results = users.size))

    fun getUserDTO(
        title: String? = DEFAULT_TITLE_STR,
        firstName: String? = DEFAULT_FIRST_NAME,
        lastName: String? = DEFAULT_LAST_NAME,
        email: String = DEFAULT_EMAIL,
        large: String = DEFAULT_IMG_LARGE,
        medium: String = DEFAULT_IMG_MEDIUM,
        thumbnail: String = DEFAULT_IMG_THUMB,
        gender: String = DEFAULT_GENDER_STR,
        date: String = DEFAULT_DOB_STR,
        age: Int = DEFAULT_AGE,
    ) = RandomUserDTO(
        name = RandomUserNameDTO(
            title = title,
            firstName = firstName,
            lastName = lastName,
        ),
        email = email,
        picture = RandomUserPictureDTO(
            large = large,
            medium = medium,
            thumbnail = thumbnail,
        ),
        gender = gender,
        dob = DobDTO(
            date = date,
            age = age,
        ),
    )

    fun checkUser(
        user: User?,
        titleArg: UserTitle = DEFAULT_TITLE,
        firstNameArg: String = DEFAULT_FIRST_NAME,
        lastNameArg: String = DEFAULT_LAST_NAME,
        emailArg: String? = DEFAULT_EMAIL,
        largeArg: URL? = DEFAULT_IMG_LARGE.toURL(),
        mediumArg: URL? = DEFAULT_IMG_MEDIUM.toURL(),
        thumbnailArg: URL? = DEFAULT_IMG_THUMB.toURL(),
        genderArg: UserGender = DEFAULT_GENDER,
        dateArg: LocalDateTime? = DEFAULT_DOB_DATE,
        ageArg: Int? = DEFAULT_AGE,
    ) = user?.let {
        with(it) {
            assert(title == titleArg) { "$user invalid title: $title" }
            assert(firstName == firstNameArg) { "$user invalid firstName: $firstName" }
            assert(lastName == lastNameArg) { "$user invalid lastName: $lastName" }
            assert(email == emailArg) { "$user invalid email: $email" }
            assert(picLargeUrl == largeArg) { "$user invalid picLargeUrl: $picLargeUrl" }
            assert(picMediumUrl == mediumArg) { "$user invalid picMediumUrl: $picMediumUrl" }
            assert(picSmallUrl == thumbnailArg) { "$user invalid picSmallUrl: $picSmallUrl" }
            assert(gender == genderArg) { "$user invalid gender: $gender" }
            assert(birthDate == dateArg) { "$user invalid birthDate: $birthDate" }
            assert(age == ageArg) { "$user invalid age: $age" }
        }
    } ?: assert(false) { "user == null" }

    fun getRoomUser(
        title: String = DEFAULT_TITLE_STR,
        firstName: String = DEFAULT_FIRST_NAME,
        lastName: String = DEFAULT_LAST_NAME,
        gender: String = DEFAULT_GENDER_STR,
        email: String? = DEFAULT_EMAIL,
        birthDate: LocalDateTime? = DEFAULT_DOB_DATE,
        age: Int? = DEFAULT_AGE,
        picLargeUrl: String? = DEFAULT_IMG_LARGE,
        picMediumUrl: String? = DEFAULT_IMG_MEDIUM,
        picSmallUrl: String? = DEFAULT_IMG_THUMB,
    ) = UserEntity(
        title = title,
        firstName = firstName,
        lastName = lastName,
        gender = gender,
        email = email,
        birthDate = birthDate,
        age = age,
        picLargeUrl = picLargeUrl,
        picMediumUrl = picMediumUrl,
        picSmallUrl = picSmallUrl,
    )

    fun getRoomUsers() = listOf(getRoomUser())
}
