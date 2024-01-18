package com.example.ui.main

import com.example.data.api.randomuser.UserTestUtility.getDomainUser
import com.example.domain.state.LocalRequestState
import com.example.domain.state.RemoteRequestState
import com.example.domain.state.ScreenState
import com.example.domain.user.GetRandomUsersUseCase
import com.example.domain.user.LoadLocalUsersUseCase
import com.example.domain.user.SaveUserUseCase
import io.mockk.every
import io.mockk.impl.annotations.MockK
import io.mockk.junit4.MockKRule
import io.mockk.mockk
import io.mockk.unmockkAll
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.TestCoroutineScheduler
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import kotlinx.coroutines.yield
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test

class MainViewModelTest {

    @get:Rule
    val mockkRule = MockKRule(this)

    private val scheduler = TestCoroutineScheduler()
    private val dispatcher = StandardTestDispatcher(scheduler, "MainViewModelTest")

    @MockK
    private val getRandomUsersUseCase = mockk<GetRandomUsersUseCase>()

    @MockK
    private val saveUserUseCase = mockk<SaveUserUseCase>()

    @MockK
    private val loadLocalUsersUseCase = mockk<LoadLocalUsersUseCase>()

    private fun getNewMainViewModel() = MainViewModelImpl(
        getRandomUsersUseCase = getRandomUsersUseCase,
        saveUserUseCase = saveUserUseCase,
        loadLocalUsersUseCase = loadLocalUsersUseCase,
        logger = null,
    )

    private val user = getDomainUser()

    @OptIn(ExperimentalCoroutinesApi::class)
    @Before
    fun setUp() {
        Dispatchers.setMain(dispatcher)

        every { runBlocking { loadLocalUsersUseCase.all() } } returns
                flowOf(LocalRequestState.Read(listOf(user)))

        every { runBlocking { getRandomUsersUseCase.fetch(any()) } } returns
                RemoteRequestState.Success(listOf(user))
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @After
    fun tearDown() {
        Dispatchers.resetMain()
        dispatcher.cancel()
        unmockkAll()
    }

    // TESTS

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun `GIVEN MainViewModel WHEN initialising THEN Initializing, Loading and View states`() =
        runTest(scheduler) {
            val viewModel = getNewMainViewModel()
            var state = viewModel.state.firstOrNull()
            assert(state is ScreenState.Initializing) { "Initializing != $state" }

            yield()

            state = viewModel.state.firstOrNull()
            assert(state is ScreenState.Loading) { "Loading != $state" }

            advanceUntilIdle()

            state = viewModel.state.firstOrNull()
            assert(state is ScreenState.View) { "View != $state" }
        }
}
