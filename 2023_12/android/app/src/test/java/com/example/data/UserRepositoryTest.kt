package com.example.data

import com.example.data.api.randomuser.ApiUserMapper
import com.example.data.api.randomuser.RandomUserService
import com.example.data.api.randomuser.UserTestUtility.DEFAULT_TITLE
import com.example.data.api.randomuser.UserTestUtility.getDomainUser
import com.example.data.api.randomuser.UserTestUtility.getRandomUsers
import com.example.data.api.randomuser.UserTestUtility.getRoomUser
import com.example.data.storage.user.UserDAO
import com.example.domain.model.UserTitle
import com.example.domain.state.LocalRequestState
import com.example.domain.state.RemoteRequestState
import com.example.domain.user.UserRepository
import io.mockk.every
import io.mockk.impl.annotations.MockK
import io.mockk.junit4.MockKRule
import io.mockk.mockk
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.TestCoroutineScheduler
import kotlinx.coroutines.test.runTest
import kotlinx.serialization.json.Json
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import retrofit2.Response

class UserRepositoryTest {

    @get:Rule
    val mockkRule = MockKRule(this)

    @MockK
    private val userDAO = mockk<UserDAO>()

    @MockK
    private val randomUserService = mockk<RandomUserService>()

    @MockK
    private val apiUserMapper = mockk<ApiUserMapper>()

    @MockK
    private val json = mockk<Json>()

    private lateinit var repository: UserRepository

    private val scheduler = TestCoroutineScheduler()
    private val dispatcher = StandardTestDispatcher(scheduler, "UserRepositoryTest")

    private val userTitle = if (DEFAULT_TITLE != UserTitle.MISS) UserTitle.MISS else UserTitle.MRS
    private val firstName = "UserRepositoryTest"
    private val lastName = "UserRepositoryTest"
    private val userSaved = getDomainUser(
        title = userTitle,
        firstName = firstName,
        lastName = lastName,
    )
    private val userEntitySaved = getRoomUser(
        title = userTitle.entityValue,
        firstName = firstName,
        lastName = lastName,
    )

    @Before
    fun setUp() {
        val apiUsers = getRandomUsers()

        every { runBlocking { randomUserService.getRandomUsers(any()) } } returns
                Response.success(apiUsers)

        every { apiUserMapper.map(apiUsers, any()) } returns
                RemoteRequestState.Success(listOf(getDomainUser()))

        every { userDAO.add(any()) } returns 1
        every { userDAO.addAll(any()) } returns listOf(1)

        every { with(userSaved) { userDAO.get(title.entityValue, firstName, lastName) } } returns
                userEntitySaved
        every { userDAO.getAll() } returns flowOf(listOf(userEntitySaved))

        every {
            with(userSaved) {
                userDAO.get(nrefEq(title.entityValue), nrefEq(firstName), nrefEq(lastName))
            }
        } returns null

        repository = UserRepositoryImp(
            randomUserService = randomUserService,
            userDAO = userDAO,
            randomUserMapper = apiUserMapper,
            json = json,
            logger = null,
            ioDispatcher = dispatcher,
        )
    }

    // TESTS

    @Test
    fun `WHEN fetching VALID remote users THEN received DOMAIN models`() = runTest(scheduler) {
        val result = repository.fetchRemoteRandomUsers(10)

        assert(result is RemoteRequestState.Success) { "fetchRemoteRandomUsers FAILED" }
    }

    @Test
    fun `GIVEN user not saved WHEN saving THEN created`() = runTest(scheduler) {
        val user = getDomainUser()
        val result = repository.saveLocalUser(user)

        assert(result is LocalRequestState.Create) { "saveLocalUser Create FAILED: $result" }
        val resultUser = (result as LocalRequestState.Create).data
        assert(resultUser == user) { "saveLocalUser Create FAILED: $resultUser != $user" }
    }

    @Test
    fun `GIVEN user saved WHEN saving THEN updated`() = runTest(scheduler) {
        val result = repository.saveLocalUser(userSaved)

        assert(result is LocalRequestState.Update) { "saveLocalUser Update FAILED: $result" }
        val resultUser = (result as LocalRequestState.Update).data
        assert(resultUser == userSaved) { "saveLocalUser Update FAILED: $resultUser != $userSaved" }
    }

    @Test
    fun `GIVEN user saved WHEN loading THEN get`() = runTest(scheduler) {
        val result = repository.getLocalUsers().firstOrNull()

        assert(result is LocalRequestState.Read) { "getLocalUsers Read FAILED: $result" }
        val resultUser = (result as LocalRequestState.Read).data.first()
        assert(resultUser == userSaved) { "getLocalUsers Rea FAILED: $resultUser != $userSaved" }
    }
}
