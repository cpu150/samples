package com.example.ui.main

import android.content.res.Configuration
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.domain.model.User
import com.example.domain.model.UserTitle
import com.example.domain.state.ScreenState
import kotlinx.coroutines.launch

@Composable
fun MainScreen() {
    val stateFlow = hiltViewModel<MainViewModelImpl>().state

    when (val screenState = stateFlow.collectAsStateWithLifecycle().value) {
        is ScreenState.Error -> ErrorScreen(screenState = screenState)
        ScreenState.Initializing -> InitializingScreen()
        is ScreenState.Loading -> LoadingScreen(screenState)
        is ScreenState.View -> MainScreen(screenState.state)
    }
}

@Composable
fun InitializingScreen() {
    Text(text = "SCREEN INITIALIZING")
}

@Composable
fun LoadingScreen(screenState: ScreenState.Loading) {
    Text(text = "SCREEN LOADING (${screenState.progress * 100f}%)")
}

@Composable
fun ErrorScreen(screenState: ScreenState.Error) {
    Text(text = "SCREEN ERROR (${screenState.message})")
}

@Composable
fun MainScreen(state: MainState) {
    Column(
        modifier = Modifier.padding(16.dp)
    ) {
        Text(
            text = "${state.users.count()} user fetched",
            modifier = Modifier
                .padding(bottom = 8.dp)
        )
        state.users.forEach {
            Text(text = "${it.title.value} ${it.firstName} ${it.lastName}")
        }
        if (state.userFetchError != null) {
            Text(text = "Error while fetching: ${state.userFetchError}")
        }
        if (state.userSaveError != null) {
            Text(text = "Error while saving: ${state.userSaveError}")
        }

        val vm = hiltViewModel<MainViewModelImpl>()
        val scope = rememberCoroutineScope()
        Button(
            modifier = Modifier
                .padding(top = 16.dp)
                .fillMaxWidth()
                .wrapContentHeight(),
            shape = RoundedCornerShape(4.dp),
            onClick = {
                state.users.firstOrNull()?.let { user -> scope.launch { vm.saveUser(user = user) } }
            },
        ) {
            Text(
                text = "Save",
                textAlign = TextAlign.Center,
                fontSize = 16.sp,
                modifier = Modifier
                    .padding(vertical = 8.dp),
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun PreviewMainScreenEmpty() {
    MainScreen(MutableMainState())
}

@Preview(showBackground = true)
@Composable
fun PreviewMainScreenSuccess() {
    MainScreen(
        MutableMainState(
            users = listOf(
                User(
                    UserTitle.MISS,
                    "Jane",
                    "Doe",
                ),
                User(
                    UserTitle.MR,
                    "John",
                    "Doe",
                ),
                User(
                    UserTitle.MS,
                    "John",
                    "Doe",
                ),
            )
        )
    )
}

@Preview(showBackground = true)
@Composable
fun PreviewMainScreenFetchError() {
    MainScreen(MutableMainState(userFetchError = "No Internet connection"))
}

@Preview(showBackground = true)
@Composable
fun PreviewMainScreenSaveError() {
    MainScreen(MutableMainState(userSaveError = "Out of memory"))
}

@Preview(showBackground = true)
@Composable
fun PreviewMainScreenInitializing() {
    InitializingScreen()
}

@Preview(
    showBackground = true,
    showSystemUi = true,
)
@Composable
fun PreviewMainScreenError() {
    ErrorScreen(ScreenState.Error("Error while initializing"))
}

@Preview(
    showBackground = true,
    showSystemUi = true,
    uiMode = Configuration.UI_MODE_NIGHT_YES
)
@Composable
fun PreviewMainScreenLoading() {
    LoadingScreen(ScreenState.Loading(.37f))
}
