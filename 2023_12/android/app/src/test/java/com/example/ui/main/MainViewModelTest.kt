package com.example.ui.main

import com.example.data.api.randomuser.UserTestUtility.DEFAULT_TITLE
import com.example.data.api.randomuser.UserTestUtility.getDomainUser
import com.example.domain.model.User
import com.example.domain.model.UserTitle
import com.example.domain.state.LocalRequestState
import com.example.domain.state.RemoteRequestState
import com.example.ui.ScreenState
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
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.TestCoroutineScheduler
import kotlinx.coroutines.test.TestScope
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

    @OptIn(ExperimentalCoroutinesApi::class)
    private suspend fun getInitialisedViewModel(
        testScope: TestScope,
    ) = getNewMainViewModel().also { viewModel ->
        // Wait until initialisation is done
        testScope.advanceUntilIdle()

        val state = viewModel.state.firstOrNull()
        assert(state is ScreenState.View) { "View != $state" }

        val stateUser = (state as ScreenState.View).state.remoteRandomUsers.firstOrNull()
        assert(stateUser == user) { "$stateUser != $user" }
    }

    private val user = getDomainUser(
        title = if (DEFAULT_TITLE != UserTitle.MISS) UserTitle.MISS else UserTitle.MRS,
        firstName = "MainViewModelTest",
        lastName = "MainViewModelTest",
    )

    private val localUsersFlow =
        MutableStateFlow<LocalRequestState<List<User>>>(LocalRequestState.Read(listOf(user)))

    @OptIn(ExperimentalCoroutinesApi::class)
    @Before
    fun setUp() {
        Dispatchers.setMain(dispatcher)

        every { runBlocking { loadLocalUsersUseCase.all() } } returns localUsersFlow

        every { runBlocking { getRandomUsersUseCase.fetch(any()) } } returns
                RemoteRequestState.Success(listOf(user))

        every { runBlocking { saveUserUseCase.save(user) } } returns LocalRequestState.Update(user)
            .also { runBlocking { localUsersFlow.emit(LocalRequestState.Read(listOf(user))) } }

        every { runBlocking { saveUserUseCase.save(nrefEq(user)) } } answers {
            val newUser = args[0] as User
            LocalRequestState.Create(newUser).also {
                runBlocking { localUsersFlow.emit(LocalRequestState.Read(listOf(newUser))) }
            }
        }
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
    fun `GIVEN MainViewModel WHEN initialising THEN Initialising, Loading and View states`() =
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
            val stateUser = (state as ScreenState.View).state.remoteRandomUsers.firstOrNull()
            assert(stateUser == user) { "$stateUser != $user" }
        }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun `GIVEN MainViewModel WHEN fetching users THEN get users if Success or get Error`() =
        runTest(scheduler) {
            val viewModel = getInitialisedViewModel(this)

            val state = viewModel.state.firstOrNull() as ScreenState.View
            val user2 = getDomainUser()
            val errorStr = "Error MainViewModelTest"

            // Error
            every { runBlocking { getRandomUsersUseCase.fetch(any()) } } returns
                    RemoteRequestState.Error(
                        reason = RemoteRequestState.ReasonCode.BODY_NULL,
                        msg = errorStr,
                    )
            viewModel.fetchRandomUsers(10)
            advanceUntilIdle()

            var stateUser = state.state.remoteRandomUsers.firstOrNull()
            var stateErrorMsg = state.state.randomUsersError
            assert(stateUser == user) { "$stateUser != $user" }
            assert(stateErrorMsg == errorStr) { "$stateErrorMsg != $errorStr" }

            // Success
            every { runBlocking { getRandomUsersUseCase.fetch(any()) } } returns
                    RemoteRequestState.Success(listOf(user2))
            viewModel.fetchRandomUsers(10)
            advanceUntilIdle()

            stateUser = state.state.remoteRandomUsers.firstOrNull()
            stateErrorMsg = state.state.randomUsersError
            assert(stateUser == user2) { "$stateUser != $user2" }
            assert(stateErrorMsg == null) { "$stateErrorMsg != null" }
        }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun `GIVEN MainViewModel WHEN saving user THEN get Success or Error accordingly`() =
        runTest(scheduler) {
            val viewModel = getInitialisedViewModel(this)

            val state = viewModel.state.firstOrNull() as ScreenState.View
            val user2 = getDomainUser()
            val errorStr = "Error MainViewModelTest"
            every { runBlocking { getRandomUsersUseCase.fetch(any()) } } returns
                    RemoteRequestState.Success(listOf(user2))

            viewModel.saveUser(user2)
            advanceUntilIdle()

            var stateUser = state.state.localUsers.firstOrNull()
            var stateError = state.state.saveUsersError
            assert(stateUser == user2) { "$stateUser != $user2" }
            assert(stateError == null) { "$stateError != null" }

            every { runBlocking { saveUserUseCase.save(user) } } returns
                    LocalRequestState.ErrorUpdate(user, Exception(errorStr))
            viewModel.saveUser(user)
            advanceUntilIdle()

            stateUser = state.state.localUsers.firstOrNull()
            stateError = state.state.saveUsersError
            // Users list must not change
            assert(stateUser == user2) { "$stateUser != $user2" }
            assert(stateError == errorStr) { "$stateError != null" }
        }
}
