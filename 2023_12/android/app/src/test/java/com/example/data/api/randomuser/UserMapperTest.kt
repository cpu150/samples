package com.example.data.api.randomuser

import com.example.common.toDateTime
import com.example.common.toURL
import com.example.data.api.randomuser.model.DobDTO
import com.example.data.api.randomuser.model.GetRandomUsersDTO
import com.example.data.api.randomuser.model.InfoRandomUserDTO
import com.example.data.api.randomuser.model.RandomUserDTO
import com.example.data.api.randomuser.model.RandomUserNameDTO
import com.example.data.api.randomuser.model.RandomUserPictureDTO
import com.example.data.storage.user.RoomUserMapperImp
import com.example.data.storage.user.StorageUserMapper
import com.example.domain.model.User
import com.example.domain.model.UserGender
import com.example.domain.model.UserTitle
import com.example.domain.state.RemoteRequestState
import org.junit.Before
import org.junit.Test
import java.net.URL
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

class UserMappersTest {

    private lateinit var apiMapper: ApiUserMapper
    private lateinit var storageMapper: StorageUserMapper

    @Before
    fun setUp() {
        apiMapper = RandomUserMapperImp()
        storageMapper = RoomUserMapperImp()
    }

    companion object {
        const val DEFAULT_TITLE_STR = "title"
        const val DEFAULT_FIRST_NAME = "First Name"
        const val DEFAULT_LAST_NAME = "Last Name"
        const val DEFAULT_EMAIL = "user@example.com"
        const val DEFAULT_IMG_LARGE = "https://example.com/large"
        const val DEFAULT_IMG_MEDIUM = "https://example.com/medium"
        const val DEFAULT_IMG_THUMB = "https://example.com/thumbnail"
        const val DEFAULT_GENDER_STR = "gender"
        const val DEFAULT_DOB_STR = "1963-10-25T13:56:32.813Z"
        val DEFAULT_DOB_DATE = DEFAULT_DOB_STR.toDateTime(DobDTO.birthDateFormat)
        const val DEFAULT_AGE = 30

        private fun getRandomUser(user: RandomUserDTO = getUserDTO()) =
            getRandomUsers(listOf(user))

        private fun getRandomUsers(
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
            titleArg: UserTitle = UserTitle.UNKNOWN,
            firstNameArg: String = DEFAULT_FIRST_NAME,
            lastNameArg: String = DEFAULT_LAST_NAME,
            emailArg: String? = DEFAULT_EMAIL,
            largeArg: URL? = DEFAULT_IMG_LARGE.toURL(),
            mediumArg: URL? = DEFAULT_IMG_MEDIUM.toURL(),
            thumbnailArg: URL? = DEFAULT_IMG_THUMB.toURL(),
            genderArg: UserGender = UserGender.UNKNOWN,
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
    }

    private fun <T> getMapperResult(
        result: RemoteRequestState<T>,
        error: (result: RemoteRequestState<T>) -> Any
    ) = when (result) {
        is RemoteRequestState.Success -> result.data
        else -> {
            assert(false) { error(result) }
            null
        }
    }

    // TESTS

    @Test
    fun `GIVEN a valid User API model WHEN mapping to DOMAIN model THEN valid`() {
        val userDTO = getRandomUser()
        val user = getMapperResult(apiMapper.map(userDTO)) { "$it: $userDTO" }?.firstOrNull()

        checkUser(user)
    }

    @Test
    fun `GIVEN User without required fields WHEN parsing to DOMAIN model THEN failed`() {
        val noTitle = apiMapper.map(getRandomUser(getUserDTO(title = null)))
        assert(noTitle !is RemoteRequestState.Error) { "User should not be valid if no title" }

        val noFirstName = apiMapper.map(getRandomUser(getUserDTO(firstName = null)))
        assert(noFirstName !is RemoteRequestState.Error) { "User should not be valid if no first name" }

        val noLastName = apiMapper.map(getRandomUser(getUserDTO(lastName = null)))
        assert(noLastName !is RemoteRequestState.Error) { "User should not be valid if no last name" }
    }

    @Test
    fun `GIVEN User title from the API WHEN parsing to DOMAIN model THEN valid`() {
        val titles = UserTitle.entries.map { it.entityValue to it }
        val usersDTO = titles.map { getUserDTO(title = it.first) }
        val users = getMapperResult(apiMapper.map(getRandomUsers(usersDTO))) { "$it: $usersDTO" }

        users?.forEachIndexed { index, user ->
            checkUser(user, titleArg = titles[index].second)
        }
    }

    @Test
    fun `GIVEN User first name from the API WHEN parsing to DOMAIN model THEN valid`() {
        val firstName = "John"

        val userDTO = getUserDTO(firstName = firstName)
        val users = getMapperResult(apiMapper.map(getRandomUser(userDTO))) { "$it: $userDTO" }

        checkUser(users?.firstOrNull(), firstNameArg = firstName)
    }

    @Test
    fun `GIVEN User last name from the API WHEN parsing to DOMAIN model THEN valid`() {
        val lastName = "Doe"

        val userDTO = getUserDTO(firstName = lastName)
        val users = getMapperResult(apiMapper.map(getRandomUser(userDTO))) { "$it: $userDTO" }

        checkUser(users?.firstOrNull(), firstNameArg = lastName)
    }

    @Test
    fun `GIVEN User email from the API WHEN parsing to DOMAIN model THEN valid`() {
        val email = "john.doe@example.com"

        val userDTO = getUserDTO(email = email)
        val users = getMapperResult(apiMapper.map(getRandomUser(userDTO))) { "$it: $userDTO" }

        checkUser(users?.firstOrNull(), emailArg = email)
    }

    @Test
    fun `GIVEN User large pic from the API WHEN parsing to DOMAIN model THEN valid`() {
        val large = "https://randomuser.me/api/portraits/men/75.jpg"

        val userDTO = getUserDTO(large = large)
        val users = getMapperResult(apiMapper.map(getRandomUser(userDTO))) { "$it: $userDTO" }

        checkUser(users?.firstOrNull(), largeArg = URL(large))
    }

    @Test
    fun `GIVEN User medium pic from the API WHEN parsing to DOMAIN model THEN valid`() {
        val medium = "https://randomuser.me/api/portraits/med/men/75.jpg"

        val userDTO = getUserDTO(medium = medium)
        val users = getMapperResult(apiMapper.map(getRandomUser(userDTO))) { "$it: $userDTO" }

        checkUser(users?.firstOrNull(), mediumArg = URL(medium))
    }

    @Test
    fun `GIVEN User thumbnail pic from the API WHEN parsing to DOMAIN model THEN valid`() {
        val thumbnail = "https://randomuser.me/api/portraits/thumb/men/75.jpg"

        val userDTO = getUserDTO(thumbnail = thumbnail)
        val users = getMapperResult(apiMapper.map(getRandomUser(userDTO))) { "$it: $userDTO" }

        checkUser(users?.firstOrNull(), thumbnailArg = URL(thumbnail))
    }

    @Test
    fun `GIVEN User gender from the API WHEN parsing to DOMAIN model THEN valid`() {
        val genders = UserGender.entries.map { it.entityValue to it }
        val usersDTO = genders.map { getUserDTO(gender = it.first) }
        val users = getMapperResult(apiMapper.map(getRandomUsers(usersDTO))) { "$it: $usersDTO" }

        users?.forEachIndexed { index, user ->
            checkUser(user, genderArg = genders[index].second)
        }
    }

    @Test
    fun `GIVEN User dob from the API WHEN parsing to DOMAIN model THEN valid`() {
        val dob = "1982-10-31T19:30:32.813Z"
        val date = LocalDateTime.parse(dob, DateTimeFormatter.ofPattern(DobDTO.birthDateFormat))
        val userDTO = getUserDTO(date = dob)
        val users = getMapperResult(apiMapper.map(getRandomUser(userDTO))) { "$it: $userDTO" }

        checkUser(users?.firstOrNull(), dateArg = date)
    }

    @Test
    fun `GIVEN User age from the API WHEN parsing to DOMAIN model THEN valid`() {
        val age = 41

        val userDTO = getUserDTO(age = age)
        val users = getMapperResult(apiMapper.map(getRandomUser(userDTO))) { "$it: $userDTO" }

        checkUser(users?.firstOrNull(), ageArg = age)
    }
}
