package com.example.ui.main

import com.example.domain.state.ScreenState
import com.example.domain.user.GetRandomUsersUseCase
import com.example.domain.user.LoadLocalUsersUseCase
import com.example.domain.user.SaveUserUseCase
import io.mockk.impl.annotations.MockK
import io.mockk.junit4.MockKRule
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.TestCoroutineScheduler
import kotlinx.coroutines.test.setMain
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

    private lateinit var viewModel: MainViewModel

    @OptIn(ExperimentalCoroutinesApi::class)
    @Before
    fun setUp() {
        Dispatchers.setMain(dispatcher)

        viewModel = MainViewModelImpl(
            getRandomUsersUseCase = getRandomUsersUseCase,
            saveUserUseCase = saveUserUseCase,
            loadLocalUsersUseCase = loadLocalUsersUseCase,
            logger = null,
        )
    }

    // TESTS

    @Test
    fun `WHEN view model initialising THEN Initializing state`() = runBlocking {
        val state = viewModel.state.firstOrNull()
        assert(state is ScreenState.Initializing) { "Initializing != $state" }
    }
}
