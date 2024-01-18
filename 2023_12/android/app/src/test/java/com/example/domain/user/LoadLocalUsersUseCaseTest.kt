package com.example.domain.user

import com.example.data.api.randomuser.UserTestUtility.DEFAULT_TITLE
import com.example.data.api.randomuser.UserTestUtility.getDomainUser
import com.example.domain.model.UserTitle
import com.example.domain.state.LocalRequestState
import io.mockk.every
import io.mockk.impl.annotations.MockK
import io.mockk.junit4.MockKRule
import io.mockk.mockk
import io.mockk.unmockkAll
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.test.TestCoroutineScheduler
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test

class LoadLocalUsersUseCaseTest {

    @get:Rule
    val mockkRule = MockKRule(this)

    private val scheduler = TestCoroutineScheduler()

    @MockK
    private val repository = mockk<UserRepository>()

    private lateinit var useCase: LoadLocalUsersUseCase

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
        every { runBlocking { repository.getLocalUsers(any()) } } returns
                flowOf(LocalRequestState.Read(listOf(user)))

        useCase = LoadLocalUsersUseCaseImp(repository, null)
    }

    @After
    fun tearDown() {
        scheduler.cancel()
        unmockkAll()
    }

    // TESTS

    @Test
    fun `WHEN loading VALID local users THEN get DOMAIN models`() = runTest(scheduler) {
        val result = useCase.all().firstOrNull()

        assert(result is LocalRequestState.Read) { "load all FAILED" }
        val resultUser = (result as LocalRequestState.Read).data.first()
        assert(resultUser == user) { "load all FAILED $resultUser != $user" }
    }
}
