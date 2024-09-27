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
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.domain.Logger
import com.example.domain.model.User
import com.example.domain.model.UserGender
import com.example.domain.model.UserTitle
import com.example.ui.ScreenState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import java.net.URL
import java.time.LocalDateTime
import kotlin.math.roundToInt

@Composable
fun MainScreen(logger: Logger? = null) {
    val vm = hiltViewModel<MainViewModelImpl>()
    val stateFlow by vm.state.collectAsStateWithLifecycle()

    when (val screenState = stateFlow.screenState) {
        is ScreenState.Error -> ErrorScreen(screenState = screenState)
        ScreenState.Loaded -> MainScreen(stateFlow)
        ScreenState.Initializing, is ScreenState.Loading -> LoadingScreen(screenState)
    }

    logger?.d("UI updated with $stateFlow")
}

@Composable
fun LoadingScreen(screenState: ScreenState) {
    val progress = if (screenState is ScreenState.Loading) {
        screenState.progress * 100f
    } else {
        0f
    }
    Text(text = "SCREEN LOADING ${progress.roundToInt()}%")
}

@Composable
fun ErrorScreen(screenState: ScreenState.Error) {
    Text(text = "SCREEN ERROR (${screenState.message})")
}

@Composable
fun MainScreen(state: MainState, vm: MainViewModel = hiltViewModel<MainViewModelImpl>()) {
    Column(
        modifier = Modifier.padding(16.dp)
    ) {
        with(state) {
            Text(
                text = "${remoteRandomUsers.count()} user(s) fetched",
                modifier = Modifier
                    .padding(bottom = 8.dp)
            )

            remoteRandomUsers.forEach { user -> UserRow(user = user) }

            if (randomUsersError != null) {
                Text(text = "Error while fetching: $randomUsersError")
            }
            if (saveUsersError != null) {
                Text(text = "Error while saving: $saveUsersError")
            }

            Text(
                modifier = Modifier.padding(
                    top = 16.dp,
                    bottom = if (localUsers.isNotEmpty()) 8.dp else 0.dp
                ),
                text = "${localUsers.count()} user(s) saved"
            )
            localUsers.forEach { user -> UserRow(user = user) }

            Button(
                modifier = Modifier
                    .padding(top = 16.dp)
                    .fillMaxWidth()
                    .wrapContentHeight(),
                shape = RoundedCornerShape(4.dp),
                onClick = {
                    remoteRandomUsers.firstOrNull()?.let { user -> vm.saveUser(user = user) }
                },
            ) {
                Text(
                    text = "Save first user in the offline list",
                    textAlign = TextAlign.Center,
                    fontSize = 16.sp,
                    modifier = Modifier
                        .padding(vertical = 8.dp),
                )
            }
        }
    }
}

@Composable
fun UserRow(user: User) = with(user) { Text(text = "${title.entityValue} $firstName $lastName") }

private fun getMainViewModel(
    state: MainState = MainState(),
): MainViewModel {
    return object : MainViewModel {
        override val state: StateFlow<MainState> = MutableStateFlow(state)

        override fun fetchRandomUsers(nbUsers: Int) {
            // no-op
        }

        override fun saveUser(user: User) {
            // no-op
        }
    }
}

@Preview(showBackground = true)
@Composable
fun PreviewMainScreenEmpty() {
    MainScreen(MainState(), getMainViewModel())
}

@Preview(showBackground = true)
@Composable
fun PreviewMainScreenSuccess() {
    MainScreen(
        MainState(
            remoteRandomUsers = listOf(
                User(
                    UserTitle.MISS,
                    "Jane",
                    "Doe",
                    UserGender.FEMALE,
                    "user@example.com",
                    LocalDateTime.now(),
                    27,
                    URL("https://example.com/large.png"),
                    URL("https://example.com/medium.png"),
                    URL("https://example.com/small.png"),
                ),
                User(
                    UserTitle.MR,
                    "John",
                    "Doe",
                    UserGender.fromEntity(null),
                    null,
                    null,
                    null,
                    null,
                    null,
                    null,
                ),
                User(
                    UserTitle.MS,
                    "John",
                    "Doe",
                    UserGender.MALE,
                    "user@example.com",
                    LocalDateTime.now(),
                    32,
                    URL("https://example.com/large.png"),
                    URL("https://example.com/medium.png"),
                    URL("https://example.com/small.png"),
                ),
            )
        ), getMainViewModel()
    )
}

@Preview(showBackground = true)
@Composable
fun PreviewMainScreenFetchError() {
    MainScreen(MainState(randomUsersError = "No Internet connection"), getMainViewModel())
}

@Preview(showBackground = true)
@Composable
fun PreviewMainScreenSaveError() {
    MainScreen(MainState(saveUsersError = "Out of memory"), getMainViewModel())
}

@Preview(showBackground = true)
@Composable
fun PreviewMainScreenFetchAndSaveErrors() {
    MainScreen(
        MainState(
            saveUsersError = "Out of memory",
            randomUsersError = "No Internet connection"
        ), getMainViewModel()
    )
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
