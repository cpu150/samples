package com.example.data.api.randomuser

import com.example.data.api.randomuser.UserTestUtility.checkUser
import com.example.data.api.randomuser.UserTestUtility.getMapperResult
import com.example.data.api.randomuser.UserTestUtility.getRandomUser
import com.example.data.api.randomuser.UserTestUtility.getRandomUsers
import com.example.data.api.randomuser.UserTestUtility.getUserDTO
import com.example.data.api.randomuser.model.DobDTO
import com.example.domain.model.UserGender
import com.example.domain.model.UserTitle
import com.example.domain.state.RemoteRequestState
import org.junit.Before
import org.junit.Test
import java.net.URL
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

class ApiUserMapperTest {

    private lateinit var apiMapper: ApiUserMapper

    @Before
    fun setUp() {
        apiMapper = RandomUserMapperImp()
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
