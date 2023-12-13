package com.example.ui.main

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
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
) : ViewModel(), MainViewModel {

    private var mainState = MainState()
        set(value) {
            field = value
            _state.value = ScreenState.View(value)
        }

    private val _state = MutableStateFlow<ScreenState<MainState>>(ScreenState.Initializing)

    override val state: StateFlow<ScreenState<MainState>>
        get() = _state

    init {
        viewModelScope.launch {
            when (val result = getRandomUsersUseCase.fetch(10)) {
                RemoteRequestState.Empty -> mainState = mainState.copy(users = emptyList())
                is RemoteRequestState.Success -> mainState = mainState.copy(users = result.data)
                is RemoteRequestState.Error -> ScreenState.Error(message = getErrorMsg(result))
            }
        }
    }

    private fun getErrorMsg(result: RemoteRequestState.Error) = if (result.ex?.message != null) {
        result.ex.message
    } else {
        result.msg
    } ?: "Error code: ${result.reason.name}"

    override suspend fun fetchRandomUsers(nbUsers: Int) {
        mainState = when (val result = getRandomUsersUseCase.fetch(nbUsers)) {
            RemoteRequestState.Empty -> emptyList<User>() to null
            is RemoteRequestState.Success -> result.data to null
            is RemoteRequestState.Error -> emptyList<User>() to getErrorMsg(result)
        }.let { (users, fetchError) ->
            mainState.copy(
                users = users,
                userFetchError = fetchError,
                userSaveError = null,
            )
        }
    }

    override suspend fun saveUser(user: User) {
        val defaultErrorMsg = "Unknown error"
        mainState = when (val result = saveUserUseCase.save(user)) {
            is LocalRequestState.Create,
            is LocalRequestState.Read,
            is LocalRequestState.Update,
            is LocalRequestState.Delete -> null

            is LocalRequestState.ErrorCreate -> result.e?.message ?: defaultErrorMsg
            is LocalRequestState.ErrorRead -> result.e?.message ?: defaultErrorMsg
            is LocalRequestState.ErrorUpdate -> result.e?.message ?: defaultErrorMsg
            is LocalRequestState.ErrorDelete -> result.e?.message ?: defaultErrorMsg
        }.let {
            mainState.copy(
                userFetchError = null,
                userSaveError = it,
            )
        }
    }
}
