package com.example.domain.user

import com.example.data.api.randomuser.UserTestUtility.DEFAULT_TITLE
import com.example.data.api.randomuser.UserTestUtility.getDomainUser
import com.example.domain.model.User
import com.example.domain.model.UserTitle
import com.example.domain.state.LocalRequestState
import io.mockk.every
import io.mockk.impl.annotations.MockK
import io.mockk.junit4.MockKRule
import io.mockk.mockk
import io.mockk.unmockkAll
import kotlinx.coroutines.cancel
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.test.TestCoroutineScheduler
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test

class SaveUserUseCaseTest {

    @get:Rule
    val mockkRule = MockKRule(this)

    private val scheduler = TestCoroutineScheduler()

    @MockK
    private val repository = mockk<UserRepository>()

    private lateinit var useCase: SaveUserUseCase

    private val userTitle = if (DEFAULT_TITLE != UserTitle.MISS) UserTitle.MISS else UserTitle.MRS
    private val firstName = "UserRepositoryTest"
    private val lastName = "UserRepositoryTest"
    private val savedUser = getDomainUser(
        title = userTitle,
        firstName = firstName,
        lastName = lastName,
    )

    @Before
    fun setUp() {
        every { runBlocking { repository.saveLocalUser(savedUser) } } returns
                LocalRequestState.Update(savedUser)

        every { runBlocking { repository.saveLocalUser(nrefEq(savedUser)) } } answers
                { LocalRequestState.Create(args[0] as User) }

        useCase = SaveUserUseCaseImp(repository)
    }

    @After
    fun tearDown() {
        scheduler.cancel()
        unmockkAll()
    }

    // TESTS

    @Test
    fun `WHEN saving VALID non existing user THEN CREATE`() = runTest(scheduler) {
        val newUser = getDomainUser()
        val result = useCase.save(newUser)

        assert(result is LocalRequestState.Create) { "save CREATE FAILED" }
        val resultUser = (result as LocalRequestState.Create).data
        assert(resultUser == newUser) { "save CREATE $resultUser != $newUser" }
    }

    @Test
    fun `WHEN saving VALID existing user THEN UPDATE`() = runTest(scheduler) {
        val result = useCase.save(savedUser)

        assert(result is LocalRequestState.Update) { "save UPDATE FAILED" }
        val resultUser = (result as LocalRequestState.Update).data
        assert(resultUser == savedUser) { "save UPDATE $resultUser != $savedUser" }
    }
}
