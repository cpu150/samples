package com.example.ui.userlist

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.domain.Logger
import com.example.domain.state.RemoteRequestState.Empty
import com.example.domain.state.RemoteRequestState.Error
import com.example.domain.state.RemoteRequestState.Success
import com.example.domain.user.GetRandomUsersUseCase
import com.example.ui.ScreenState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class UserListViewModelImpl @Inject constructor(
    private val getRandomUsersUseCase: GetRandomUsersUseCase,
    private val logger: Logger?,
) : ViewModel(), UserListViewModel {

    private val _state = MutableStateFlow(UserListScreenState())
    override val state: StateFlow<UserListScreenState> = _state

    init {
        viewModelScope.launch {
            _state.update { it.copy(screenState = ScreenState.Loading()) }
            fetchRandomUsers(100)
            _state.update { it.copy(screenState = ScreenState.Loaded) }
        }
    }

    private fun getErrorMsg(result: Error) = if (result.ex?.message != null) {
        result.ex.message
    } else {
        result.msg
    } ?: "Error code: ${result.reason.name}"

    override fun fetchUsers(nbUsers: Int) {
        viewModelScope.launch { fetchRandomUsers(nbUsers) }
    }

    private suspend fun fetchRandomUsers(nbUsers: Int) {
        var randomUsersError: String? = null
        var remoteRandomUsers = _state.value.remoteRandomUsers

        when (val result = getRandomUsersUseCase.fetch(nbUsers)) {
            Empty -> remoteRandomUsers = emptyList()
            is Success -> remoteRandomUsers = result.data
            is Error -> randomUsersError = getErrorMsg(result)
                .also { logger?.e("Error while fetching users", result.ex) }
        }

        _state.update {
            it.copy(remoteRandomUsers = remoteRandomUsers, randomUsersError = randomUsersError)
        }
    }
}
