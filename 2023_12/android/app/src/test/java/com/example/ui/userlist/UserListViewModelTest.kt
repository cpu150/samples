package com.example.ui.userlist

import com.example.MainCoroutineRule
import com.example.data.api.randomuser.UserTestUtility.DEFAULT_TITLE
import com.example.data.api.randomuser.UserTestUtility.getDomainUser
import com.example.domain.model.UserTitle
import com.example.domain.state.RemoteRequestState
import com.example.domain.user.GetRandomUsersUseCase
import com.example.ui.ScreenState
import io.mockk.MockKAnnotations
import io.mockk.coEvery
import io.mockk.impl.annotations.MockK
import io.mockk.unmockkAll
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.test.advanceTimeBy
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.yield
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test

@Suppress("TestFunctionName")
class UserListViewModelTest {

    @ExperimentalCoroutinesApi
    @get:Rule
    val mainCoroutineRule = MainCoroutineRule()

    @MockK
    lateinit var randomUsersUseCase: GetRandomUsersUseCase

    @ExperimentalCoroutinesApi
    private fun getNewUserListViewModel() = UserListViewModelImpl(
        getRandomUsersUseCase = randomUsersUseCase,
        logger = null,
    )

    private val user = getDomainUser(
        title = if (DEFAULT_TITLE != UserTitle.MISS) UserTitle.MISS else UserTitle.MRS,
        firstName = "UserListViewModelTest",
        lastName = "UserListViewModelTest",
    )

    @ExperimentalCoroutinesApi
    @Before
    fun setUp() {
        MockKAnnotations.init(this)

        coEvery { randomUsersUseCase.fetch(any()) } returns RemoteRequestState.Success(listOf(user))
    }

    @After
    fun tearDown() {
        unmockkAll()
    }

    // TESTS

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun GIVEN_MainViewModel_WHEN_initialising_THEN_Initialising_Loading_and_Loaded() = runTest {
        // Called by `init` while loading. Call `advanceTimeBy()` to be able to test the `state`
        // while loading
        coEvery { randomUsersUseCase.fetch(any()) } answers {
            runBlocking { mainCoroutineRule.advanceTimeBy(1L) }
            RemoteRequestState.Success(listOf(user))
        }

        val viewModel = getNewUserListViewModel()
        val state = viewModel.state
        var screenState = state.value.screenState
        assert(screenState == ScreenState.Initializing) { "Initializing != $screenState" }

        yield()

        screenState = state.value.screenState
        assert(screenState == ScreenState.Loading()) { "Loading != $screenState" }

        advanceTimeBy(1L)
        yield()

        screenState = state.first().screenState
        assert(screenState == ScreenState.Loaded) { "Loaded != $screenState" }
        val stateUser = state.value.remoteRandomUsers.first()
        assert(stateUser == user) { "$stateUser != $user" }
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun GIVEN_MainViewModel_WHEN_fetching_users_THEN_get_users_if_Success_or_get_Error() = runTest {
        val viewModel = getNewUserListViewModel()
        val user2 = getDomainUser()
        val errorStr = "Error MainViewModelTest"

        // Error while fetching users
        coEvery { randomUsersUseCase.fetch(any()) } returns RemoteRequestState.Error(
            reason = RemoteRequestState.ReasonCode.BODY_NULL,
            msg = errorStr,
        )
        viewModel.fetchUsers(10)
        advanceUntilIdle()

        var state = viewModel.state.first()
        val emptyUserList = state.remoteRandomUsers.isEmpty()
        var stateErrorMsg = state.randomUsersError
        assert(emptyUserList) { "$emptyUserList list should be empty when returns an error" }
        assert(stateErrorMsg == errorStr) { "$stateErrorMsg != $errorStr" }

        // Success while fetching users
        coEvery { randomUsersUseCase.fetch(any()) } returns RemoteRequestState.Success(listOf(user2))
        viewModel.fetchUsers(10)
        advanceUntilIdle()

        state = viewModel.state.first()
        val stateUser = state.remoteRandomUsers.first()
        stateErrorMsg = state.randomUsersError
        assert(stateUser == user2) { "$stateUser != $user2" }
        assert(stateErrorMsg == null) { "$stateErrorMsg != null" }
    }
}
