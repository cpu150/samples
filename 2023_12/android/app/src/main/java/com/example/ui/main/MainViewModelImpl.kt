package com.example.ui.main

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.domain.Logger
import com.example.domain.model.User
import com.example.domain.state.LocalRequestState
import com.example.domain.state.RemoteRequestState.Empty
import com.example.domain.state.RemoteRequestState.Error
import com.example.domain.state.RemoteRequestState.Success
import com.example.domain.state.ScreenState
import com.example.domain.user.GetRandomUsersUseCase
import com.example.domain.user.LoadLocalUsersUseCase
import com.example.domain.user.SaveUserUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject
import kotlin.time.Duration.Companion.seconds

@HiltViewModel
class MainViewModelImpl @Inject constructor(
    private val getRandomUsersUseCase: GetRandomUsersUseCase,
    private val saveUserUseCase: SaveUserUseCase,
    private val loadLocalUsersUseCase: LoadLocalUsersUseCase,
    private val logger: Logger?,
) : ViewModel(), MainViewModel {

    private var mainState = MutableMainState()

    private val _state = MutableStateFlow<ScreenState<MainState>>(ScreenState.Initializing)

    override val state: StateFlow<ScreenState<MainState>>
        get() = _state

    init {
        simulateLoading {
            fetchRandomUsers(10)
            viewModelScope.launch { collectLocalUsers() }
        }
    }

    private fun simulateLoading(completion: () -> Unit) {
        var progress = 0f

        viewModelScope.launch {
            do {
                _state.value = ScreenState.Loading(progress)
                delay(.8.seconds)
                progress += 1 / 4f
            } while (progress <= 1f)

            completion()
        }
    }

    private fun updateState() {
        _state.value = ScreenState.View(mainState)
    }

    private suspend fun collectLocalUsers() {
        loadLocalUsersUseCase.all().collect { result ->
            val isAnError = handleLocalUserError(result) { errorMsg ->
                mainState.localUsersError = errorMsg
            }
            if (!isAnError) {
                when (result) {
                    is LocalRequestState.Create -> result.data
                    is LocalRequestState.Read -> result.data
                    is LocalRequestState.Update -> result.data
                    is LocalRequestState.Delete -> result.data
                    else -> null
                }?.also {
                    mainState.localUsers = it
                }
            }
            updateState()
        }
    }

    private fun getErrorMsg(result: Error) = if (result.ex?.message != null) {
        result.ex.message
    } else {
        result.msg
    } ?: "Error code: ${result.reason.name}"

    override fun fetchRandomUsers(nbUsers: Int) {
        viewModelScope.launch {
            mainState.randomUsersError = null

            when (val result = getRandomUsersUseCase.fetch(nbUsers)) {
                Empty -> mainState.remoteRandomUsers = emptyList()
                is Success -> mainState.remoteRandomUsers = result.data
                is Error -> mainState.randomUsersError = getErrorMsg(result)
                    .also { logger?.e("Error while fetching users", result.ex) }
            }
            updateState()
        }
    }

    override fun saveUser(user: User) {
        viewModelScope.launch {
            handleLocalUserError(saveUserUseCase.save(user)) { errorMsg ->
                mainState.saveUsersError = errorMsg
                updateState()
            }
        }
    }

    private fun handleLocalUserError(
        result: LocalRequestState<Any>,
        handleError: (errorMsg: String?) -> Unit,
    ) = "Unknown error".let { defaultErrorMsg ->
        when (result) {
            is LocalRequestState.ErrorCreate -> result.e?.message ?: defaultErrorMsg
            is LocalRequestState.ErrorRead -> result.e?.message ?: defaultErrorMsg
            is LocalRequestState.ErrorUpdate -> result.e?.message ?: defaultErrorMsg
            is LocalRequestState.ErrorDelete -> result.e?.message ?: defaultErrorMsg
            is LocalRequestState.Create,
            is LocalRequestState.Read,
            is LocalRequestState.Update,
            is LocalRequestState.Delete -> null
        }.also { errorMsg -> handleError(errorMsg) }
    } != null
}
