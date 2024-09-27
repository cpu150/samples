package com.example.ui.main

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.domain.Logger
import com.example.domain.model.User
import com.example.domain.state.LocalRequestState
import com.example.domain.state.RemoteRequestState.Empty
import com.example.domain.state.RemoteRequestState.Error
import com.example.domain.state.RemoteRequestState.Success
import com.example.domain.user.GetRandomUsersUseCase
import com.example.domain.user.LoadLocalUsersUseCase
import com.example.domain.user.SaveUserUseCase
import com.example.ui.ScreenState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MainViewModelImpl @Inject constructor(
    private val getRandomUsersUseCase: GetRandomUsersUseCase,
    private val saveUserUseCase: SaveUserUseCase,
    private val loadLocalUsersUseCase: LoadLocalUsersUseCase,
    private val logger: Logger?,
) : ViewModel(), MainViewModel {

    private val _state = MutableStateFlow(MainState())
    override val state: StateFlow<MainState> = _state

    init {
        _state.update { it.copy(screenState = ScreenState.Loading(0f)) }
        viewModelScope.launch {
            launch { collectLocalUsers() }

            suspendedFetchRandomUsers(10)

            _state.update { it.copy(screenState = ScreenState.Loaded) }
        }
    }

    private suspend fun collectLocalUsers() {
        loadLocalUsersUseCase.all().collect { result ->
            handleLocalUserError(result) { errorMsg ->
                _state.update { it.copy(localUsersError = errorMsg) }
            }.takeIf { isAnError -> isAnError.not() }
                ?.also { _ ->
                    when (result) {
                        is LocalRequestState.Create -> result.data
                        is LocalRequestState.Read -> result.data
                        is LocalRequestState.Update -> result.data
                        is LocalRequestState.Delete -> result.data
                        else -> null
                    }?.also { users ->
                        _state.update { it.copy(localUsers = users) }
                    }
                }
        }
    }

    private fun getErrorMsg(result: Error) = if (result.ex?.message != null) {
        result.ex.message
    } else {
        result.msg
    } ?: "Error code: ${result.reason.name}"

    override fun fetchRandomUsers(nbUsers: Int) {
        viewModelScope.launch { suspendedFetchRandomUsers(nbUsers) }
    }

    private suspend fun suspendedFetchRandomUsers(nbUsers: Int) {
        var randomUsersError: String? = null
        var remoteRandomUsers = _state.value.remoteRandomUsers

        when (val result = getRandomUsersUseCase.fetch(nbUsers)) {
            Empty -> remoteRandomUsers = emptyList()
            is Success -> remoteRandomUsers = result.data
            is Error -> randomUsersError = getErrorMsg(result)
                .also { logger?.e("Error while fetching users", result.ex) }
        }

        _state.update {
            it.copy(
                randomUsersError = randomUsersError,
                remoteRandomUsers = remoteRandomUsers
            )
        }
    }

    override fun saveUser(user: User) {
        viewModelScope.launch {
            handleLocalUserError(saveUserUseCase.save(user)) { errorMsg ->
                _state.update { it.copy(saveUsersError = errorMsg) }
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
