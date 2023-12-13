package com.example.ui.main

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.domain.Logger
import com.example.domain.model.User
import com.example.domain.randomuser.GetRandomUsersUseCase
import com.example.domain.randomuser.SaveUserUseCase
import com.example.domain.state.LocalRequestState
import com.example.domain.state.RemoteRequestState
import com.example.domain.state.ScreenState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MainViewModelImpl @Inject constructor(
    private val getRandomUsersUseCase: GetRandomUsersUseCase,
    private val saveUserUseCase: SaveUserUseCase,
    private val logger: Logger,
) : ViewModel(), MainViewModel {

    private var mainState = MutableMainState()

    private val _state = MutableStateFlow<ScreenState<MainState>>(ScreenState.Initializing)

    override val state: StateFlow<ScreenState<MainState>>
        get() = _state

    init {
        viewModelScope.launch {
            _state.value = when (val result = getRandomUsersUseCase.fetch(10)) {
                RemoteRequestState.Empty ->
                    ScreenState.View(mainState.apply { users = emptyList() })

                is RemoteRequestState.Success ->
                    ScreenState.View(mainState.apply { users = result.data })

                is RemoteRequestState.Error -> ScreenState.Error(message = getErrorMsg(result))
                    .also { logger.e("Error while fetching users at the init", result.ex) }
            }
        }
    }

    private fun getErrorMsg(result: RemoteRequestState.Error) = if (result.ex?.message != null) {
        result.ex.message
    } else {
        result.msg
    } ?: "Error code: ${result.reason.name}"

    override suspend fun fetchRandomUsers(nbUsers: Int) {
        _state.value = when (val result = getRandomUsersUseCase.fetch(nbUsers)) {
            RemoteRequestState.Empty -> mainState.apply { users = emptyList() }
            is RemoteRequestState.Success -> mainState.apply { users = result.data }
            is RemoteRequestState.Error -> mainState.apply { userFetchError = getErrorMsg(result) }
                .also { logger.e("Error while fetching users", result.ex) }
        }.let { ScreenState.View(it) }
    }

    override suspend fun saveUser(user: User) {
        val defaultErrorMsg = "Unknown error"
        when (val result = saveUserUseCase.save(user)) {
            is LocalRequestState.Create,
            is LocalRequestState.Read,
            is LocalRequestState.Update,
            is LocalRequestState.Delete -> null

            is LocalRequestState.ErrorCreate -> result.e?.message ?: defaultErrorMsg
            is LocalRequestState.ErrorRead -> result.e?.message ?: defaultErrorMsg
            is LocalRequestState.ErrorUpdate -> result.e?.message ?: defaultErrorMsg
            is LocalRequestState.ErrorDelete -> result.e?.message ?: defaultErrorMsg
        }?.let { _state.value = ScreenState.View(mainState.apply { userSaveError = it }) }
    }
}
