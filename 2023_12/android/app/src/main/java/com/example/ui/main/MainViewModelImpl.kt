package com.example.ui.main

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import javax.inject.Inject

@HiltViewModel
class MainViewModelImpl @Inject constructor() : ViewModel(), MainViewModel {

    private val _state = MutableStateFlow(MainState.Loading)

    override val state: StateFlow<MainState>
        get() = _state
}
