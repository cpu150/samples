package com.example.ui.main

import android.content.res.Configuration
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle

@Composable
fun MainScreen() {
    val stateFlow = hiltViewModel<MainViewModelImpl>().state
    MainScreen(stateFlow.collectAsStateWithLifecycle().value)
}

@Composable
fun MainScreen(state: MainState) {
    when (state) {
        is MainState.Error -> Text("Empty")
        MainState.Loading -> Text("Loading")
        is MainState.Success<*> -> Text("Success")
    }
}

@Preview(showBackground = true)
@Composable
fun PreviewMainScreenSuccess() {
    MainScreen(MainState.Success("Success"))
}

@Preview(showBackground = true)
@Composable
fun PreviewMainScreenError() {
    MainScreen(MainState.Error())
}

@Preview(
    showBackground = true,
    showSystemUi = true,
    uiMode = Configuration.UI_MODE_NIGHT_YES
)
@Composable
fun PreviewMainScreenLoading() {
    MainScreen(MainState.Loading)
}
