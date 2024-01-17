package com.example.domain.user

import com.example.data.api.randomuser.UserTestUtility.DEFAULT_TITLE
import com.example.data.api.randomuser.UserTestUtility.getDomainUser
import com.example.domain.model.UserTitle
import com.example.domain.state.RemoteRequestState
import io.mockk.every
import io.mockk.impl.annotations.MockK
import io.mockk.junit4.MockKRule
import io.mockk.mockk
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.test.TestCoroutineScheduler
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Rule
import org.junit.Test

class GetRandomUsersUseCaseTest {

    @get:Rule
    val mockkRule = MockKRule(this)

    private val scheduler = TestCoroutineScheduler()

    @MockK
    private val repository = mockk<UserRepository>()

    private lateinit var useCase: GetRandomUsersUseCase

    private val userTitle = if (DEFAULT_TITLE != UserTitle.MISS) UserTitle.MISS else UserTitle.MRS
    private val firstName = "UserRepositoryTest"
    private val lastName = "UserRepositoryTest"
    private val user = getDomainUser(
        title = userTitle,
        firstName = firstName,
        lastName = lastName,
    )

    @Before
    fun setUp() {
        every { runBlocking { repository.fetchRemoteRandomUsers(any()) } } returns
                RemoteRequestState.Success(listOf(user))

        useCase = GetRandomUsersUseCaseImp(repository)
    }

    // TESTS

    @Test
    fun `WHEN fetching VALID remote users THEN received DOMAIN models`() = runTest(scheduler) {
        val result = useCase.fetch()

        assert(result is RemoteRequestState.Success) { "fetch FAILED" }
        val resultUser = (result as RemoteRequestState.Success).data.first()
        assert(resultUser == user) { "fetch FAILED $resultUser != $user" }
    }
}
