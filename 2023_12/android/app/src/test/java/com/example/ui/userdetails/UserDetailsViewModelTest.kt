package com.example.ui.userdetails

import androidx.lifecycle.SavedStateHandle
import com.example.MainCoroutineRule
import com.example.data.api.randomuser.UserTestUtility.DEFAULT_TITLE
import com.example.data.api.randomuser.UserTestUtility.getDomainUser
import com.example.domain.model.User
import com.example.domain.model.UserTitle
import com.example.domain.state.LocalRequestState
import com.example.domain.user.DeleteLocalUserUseCase
import com.example.domain.user.LoadLocalUsersUseCase
import com.example.domain.user.SaveUserUseCase
import com.example.ui.nav.NavArgParser
import com.example.ui.nav.UserDetailsScreen
import io.mockk.MockKAnnotations
import io.mockk.coEvery
import io.mockk.every
import io.mockk.impl.annotations.MockK
import io.mockk.unmockkAll
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.test.TestScope
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import kotlinx.serialization.json.Json
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test

@Suppress("TestFunctionName")
class UserDetailsViewModelTest {

    @ExperimentalCoroutinesApi
    @get:Rule
    val mainCoroutineRule = MainCoroutineRule()

    @MockK
    lateinit var saveUserUseCase: SaveUserUseCase

    @MockK
    lateinit var loadLocalUsersUseCase: LoadLocalUsersUseCase

    @MockK
    lateinit var deleteLocalUsersUseCase: DeleteLocalUserUseCase

    @MockK
    lateinit var json: Json

    @MockK
    lateinit var navArgParser: NavArgParser<UserDetailsScreen>

    @MockK
    lateinit var savedStateHandle: SavedStateHandle

    private val user = getDomainUser(
        title = if (DEFAULT_TITLE != UserTitle.MISS) UserTitle.MISS else UserTitle.MRS,
        firstName = "UserListViewModelTest",
        lastName = "UserListViewModelTest",
    )

    private val localUsersFlow =
        MutableStateFlow<LocalRequestState<List<User>>>(LocalRequestState.Read(listOf(user)))

    private fun getNewUserDetailsViewModel() = UserDetailsViewModelImp(
        saveUserUseCase = saveUserUseCase,
        loadLocalUsersUseCase = loadLocalUsersUseCase,
        deleteLocalUserUseCase = deleteLocalUsersUseCase,
        logger = null,
        json = json,
        savedStateHandle = savedStateHandle,
        navArgParser = navArgParser,
    )

    @ExperimentalCoroutinesApi
    private suspend fun getInitialisedViewModel(
        testScope: TestScope,
    ) = getNewUserDetailsViewModel().also { viewModel ->
        // Wait until initialisation is done
        testScope.advanceUntilIdle()

        val state = viewModel.state.first()
        assert(state.isUserSaved == true) { "isUserSaved != true ${state.isUserSaved}" }
        assert(state.saveUsersError == null) { "saveUsersError != null ${state.saveUsersError}" }
        assert(state.localUsersError == null) { "localUsersError != null ${state.localUsersError}" }
        assert(state.deleteUsersError == null) { "deleteUsersError != null ${state.deleteUsersError}" }
        assert(state.user == user) { "user != $user ${state.user}" }
    }

    @Before
    fun setUp() {
        MockKAnnotations.init(this)

        every { navArgParser.from(any(), any()) } returns UserDetailsScreen(user)
        coEvery { loadLocalUsersUseCase.all() } returns localUsersFlow
        coEvery { saveUserUseCase.save(user) } returns LocalRequestState.Update(user)
        coEvery { deleteLocalUsersUseCase.delete(user) } returns LocalRequestState.Delete(user)
    }

    @After
    fun tearDown() {
        unmockkAll()
    }

    // TESTS

    // DELETE -----------------------------
    @ExperimentalCoroutinesApi
    @Test
    fun GIVEN_viewModel_deleting_WHEN_failed_THEN_viewModel_get_an_error() = runTest {
        val viewModel = getInitialisedViewModel(this)

        var state = viewModel.state.first()
        assert(state.deleteUsersError == null) { "deleteUsersError != null ${state.deleteUsersError}" }

        // Return error when deleting
        coEvery {  deleteLocalUsersUseCase.delete(user) } returns LocalRequestState.ErrorDelete(user)

        // Deleting
        viewModel.deleteUser()
        advanceUntilIdle()

        state = viewModel.state.first()
        assert(state.deleteUsersError != null) { "deleteUsersError == null" }
    }

    @ExperimentalCoroutinesApi
    @Test
    fun GIVEN_viewModel_deleting_WHEN_success_THEN_viewModel_get_no_error() = runTest {
        val viewModel = getInitialisedViewModel(this)

        // Get into an error State before the test
        coEvery { deleteLocalUsersUseCase.delete(user) } returns LocalRequestState.ErrorDelete(user)
        viewModel.deleteUser()
        advanceUntilIdle()
        var state = viewModel.state.first()
        assert(state.deleteUsersError != null) { "deleteUsersError == null" }

        // Return success when deleting
        coEvery { deleteLocalUsersUseCase.delete(user) } returns LocalRequestState.Delete(user)

        // Deleting
        viewModel.deleteUser()
        advanceUntilIdle()

        state = viewModel.state.first()
        assert(state.deleteUsersError == null) { "deleteUsersError != null ${state.deleteUsersError}" }
    }

    @ExperimentalCoroutinesApi
    @Test
    fun GIVEN_user_getting_deleted_WHEN_repo_succeed_THEN_viewModel_get_updated() = runTest {
        val viewModel = getInitialisedViewModel(this)

        var state = viewModel.state.first()
        assert(state.isUserSaved == true) { "isUserSaved != true" }
        assert(state.deleteUsersError == null) { "deleteUsersError != null ${state.deleteUsersError}" }

        // Repo emits: Delete user
        localUsersFlow.update { LocalRequestState.Delete(listOf()) }
        advanceUntilIdle()

        state = viewModel.state.first()
        assert(state.isUserSaved == false) { "isUserSaved != false" }
        assert(state.deleteUsersError == null) { "deleteUsersError != null ${state.deleteUsersError}" }
    }

    @ExperimentalCoroutinesApi
    @Test
    fun GIVEN_user_getting_deleted_WHEN_repo_error_THEN_viewModel_get_an_error() = runTest {
        val viewModel = getInitialisedViewModel(this)

        var state = viewModel.state.first()
        assert(state.isUserSaved == true) { "isUserSaved != true" }
        assert(state.localUsersError == null) { "localUsersError != null ${state.localUsersError}" }

        // Repo emits: Error deleting user
        localUsersFlow.update { LocalRequestState.ErrorDelete(listOf(user)) }
        advanceUntilIdle()

        state = viewModel.state.first()
        assert(state.isUserSaved == true) { "isUserSaved != true" }
        assert(state.localUsersError != null) { "localUsersError == null" }
    }
    // DELETE -----------------------------
    // SAVE -------------------------------
    @ExperimentalCoroutinesApi
    @Test
    fun GIVEN_viewModel_saving_WHEN_failed_THEN_viewModel_get_an_error() = runTest {
        val viewModel = getInitialisedViewModel(this)

        var state = viewModel.state.first()
        assert(state.saveUsersError == null) { "saveUsersError != null ${state.saveUsersError}" }

        // Return error when saving
        coEvery {  saveUserUseCase.save(user) } returns LocalRequestState.ErrorCreate(user)

        // Saving
        viewModel.saveUser()
        advanceUntilIdle()

        state = viewModel.state.first()
        assert(state.saveUsersError != null) { "saveUsersError == null" }
    }

    @ExperimentalCoroutinesApi
    @Test
    fun GIVEN_viewModel_saving_WHEN_success_THEN_viewModel_get_no_error() = runTest {
        val viewModel = getInitialisedViewModel(this)

        // Get into an error State before the test
        coEvery { saveUserUseCase.save(user) } returns LocalRequestState.ErrorCreate(user)
        viewModel.saveUser()
        advanceUntilIdle()
        var state = viewModel.state.first()
        assert(state.saveUsersError != null) { "saveUsersError == null" }

        // Return success when saving
        coEvery { saveUserUseCase.save(user) } returns LocalRequestState.Create(user)

        // Saving
        viewModel.saveUser()
        advanceUntilIdle()

        state = viewModel.state.first()
        assert(state.saveUsersError == null) { "saveUsersError != null ${state.saveUsersError}" }
    }

    @ExperimentalCoroutinesApi
    @Test
    fun GIVEN_user_getting_saved_WHEN_repo_succeed_THEN_viewModel_get_updated() = runTest {
        val viewModel = getInitialisedViewModel(this)

        var state = viewModel.state.first()
        assert(state.isUserSaved == true) { "isUserSaved != true" }
        assert(state.saveUsersError == null) { "saveUsersError != null ${state.saveUsersError}" }

        // Repo emits: Create user
        localUsersFlow.update { LocalRequestState.Create(listOf()) }
        advanceUntilIdle()

        state = viewModel.state.first()
        assert(state.isUserSaved == false) { "isUserSaved != false" }
        assert(state.saveUsersError == null) { "saveUsersError != null ${state.saveUsersError}" }
    }

    @ExperimentalCoroutinesApi
    @Test
    fun GIVEN_user_getting_saved_WHEN_repo_error_THEN_viewModel_get_an_error() = runTest {
        val viewModel = getInitialisedViewModel(this)

        var state = viewModel.state.first()
        assert(state.isUserSaved == true) { "isUserSaved != true" }
        assert(state.localUsersError == null) { "localUsersError != null ${state.localUsersError}" }

        // Repo emits: Error saving user
        localUsersFlow.update { LocalRequestState.ErrorCreate(listOf(user)) }
        advanceUntilIdle()

        state = viewModel.state.first()
        assert(state.isUserSaved == true) { "isUserSaved != true" }
        assert(state.localUsersError != null) { "localUsersError == null" }
    }
    // SAVE -----------------------------
    // READ -----------------------------
    @ExperimentalCoroutinesApi
    @Test
    fun GIVEN_user_getting_read_WHEN_repo_succeed_THEN_viewModel_get_updated() = runTest {
        // Get into an error State before the test
        localUsersFlow.update { LocalRequestState.ErrorRead() }
        val viewModel = getNewUserDetailsViewModel()
        advanceUntilIdle()
        var state = viewModel.state.first()
        assert(state.isUserSaved == null) { "isUserSaved != null" }
        assert(state.localUsersError != null) { "localUsersError == null" }

        // Repo emits: Read user
        localUsersFlow.update { LocalRequestState.Read(listOf(user)) }
        advanceUntilIdle()

        state = viewModel.state.first()
        assert(state.isUserSaved == true) { "isUserSaved != true" }
        assert(state.saveUsersError == null) { "saveUsersError != null ${state.saveUsersError}" }
    }

    @ExperimentalCoroutinesApi
    @Test
    fun GIVEN_user_getting_read_WHEN_repo_error_THEN_viewModel_get_an_error() = runTest {
        val viewModel = getInitialisedViewModel(this)

        var state = viewModel.state.first()
        assert(state.isUserSaved == true) { "isUserSaved != true" }
        assert(state.localUsersError == null) { "localUsersError != null ${state.localUsersError}" }

        // Repo emits: Error reading user
        localUsersFlow.update { LocalRequestState.ErrorRead() }
        advanceUntilIdle()

        state = viewModel.state.first()
        assert(state.isUserSaved == true) { "isUserSaved != true" }
        assert(state.localUsersError != null) { "localUsersError == null" }
    }
    // READ -----------------------------
}