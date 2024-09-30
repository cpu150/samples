package com.example.ui.userdetails

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.domain.Logger
import com.example.domain.state.LocalRequestState
import com.example.domain.user.DeleteLocalUserUseCase
import com.example.domain.user.LoadLocalUsersUseCase
import com.example.domain.user.SaveUserUseCase
import com.example.ui.nav.UserDetailsScreen
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.serialization.json.Json
import javax.inject.Inject

@HiltViewModel
class UserDetailsViewModelImp @Inject constructor(
    private val saveUserUseCase: SaveUserUseCase,
    private val loadLocalUsersUseCase: LoadLocalUsersUseCase,
    private val deleteLocalUserUseCase: DeleteLocalUserUseCase,
    private val logger: Logger?,
    json: Json,
    savedStateHandle: SavedStateHandle,
) : UserDetailsViewModel, ViewModel() {

    private val userDetailsScreen = UserDetailsScreen.from(savedStateHandle, json)

    private val _state = MutableStateFlow(UserDetailsScreenState(user = userDetailsScreen.user))
    override val state: StateFlow<UserDetailsScreenState> = _state

    init {
        viewModelScope.launch { collectLocalUsers() }
    }

    private suspend fun collectLocalUsers() {
        loadLocalUsersUseCase.all().collect { result ->
            handleLocalUserError(
                result,
                onSuccess = {
                    when (result) {
                        is LocalRequestState.Create -> result.data
                        is LocalRequestState.Read -> result.data
                        is LocalRequestState.Update -> result.data
                        is LocalRequestState.Delete -> result.data
                        else -> null
                    }?.also { users ->
                        _state.update { it.copy(isUserSaved = users.contains(it.user)) }
                    }
                },
                onError = { errorMsg -> _state.update { it.copy(localUsersError = errorMsg) } }
            )
        }
    }

    override fun saveUser() {
        viewModelScope.launch {
            handleLocalUserError(saveUserUseCase.save(_state.value.user)) { errorMsg ->
                _state.update { it.copy(saveUsersError = errorMsg) }
            }
        }
    }

    override fun deleteUser() {
        viewModelScope.launch {
            handleLocalUserError(deleteLocalUserUseCase.delete(_state.value.user)) { errorMsg ->
                _state.update { it.copy(deleteUsersError = errorMsg) }
            }
        }
    }

    private fun handleLocalUserError(
        result: LocalRequestState<Any>,
        onSuccess: (() -> Unit)? = null,
        onError: (errorMsg: String?) -> Unit,
    ) {
        "Unknown error".let { defaultErrorMsg ->
            when (result) {
                is LocalRequestState.ErrorCreate -> result.e?.message ?: defaultErrorMsg
                is LocalRequestState.ErrorRead -> result.e?.message ?: defaultErrorMsg
                is LocalRequestState.ErrorUpdate -> result.e?.message ?: defaultErrorMsg
                is LocalRequestState.ErrorDelete -> result.e?.message ?: defaultErrorMsg
                else -> null
            }?.also { errorMsg ->
                logger?.e(errorMsg)
                onError(errorMsg)
            } ?: onSuccess?.invoke()
        }
    }
}
