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
import com.example.domain.state.ScreenState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import java.net.URL
import java.time.LocalDateTime
import kotlin.math.roundToInt

@Composable
fun MainScreen(logger: Logger? = null) {
    val vm = hiltViewModel<MainViewModelImpl>()
    val stateFlow by vm.state.collectAsStateWithLifecycle()
    // TODO: Check why when removing the log 'stateFlow' does not refresh anymore
    logger?.d("UI updated: $stateFlow")
    when (val screenState = stateFlow) {
        is ScreenState.Error -> ErrorScreen(screenState = screenState)
        ScreenState.Initializing -> InitializingScreen()
        is ScreenState.Loading -> LoadingScreen(screenState)
        is ScreenState.View -> MainScreen(screenState.state, vm)
    }
}

@Composable
fun InitializingScreen() {
    Text(text = "SCREEN INITIALIZING")
}

@Composable
fun LoadingScreen(screenState: ScreenState.Loading) {
    val progress = screenState.progress * 100f
    Text(text = "SCREEN LOADING ${progress.roundToInt()}%")
}

@Composable
fun ErrorScreen(screenState: ScreenState.Error) {
    Text(text = "SCREEN ERROR (${screenState.message})")
}

@Composable
fun MainScreen(state: MainState, vm: MainViewModel) {
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
                    text = "Save first user in the remote list",
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

private fun getMainViewModel(mainState: MainState = MutableMainState()) = object : MainViewModel {
    override val state: StateFlow<ScreenState<MainState>> =
        MutableStateFlow(ScreenState.View(mainState))

    override fun fetchRandomUsers(nbUsers: Int) {}
    override fun saveUser(user: User) {}
}

@Preview(showBackground = true)
@Composable
fun PreviewMainScreenEmpty() {
    MainScreen(MutableMainState(), getMainViewModel())
}

@Preview(showBackground = true)
@Composable
fun PreviewMainScreenSuccess() {
    val mainState = MutableMainState(
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
    )
    MainScreen(mainState, getMainViewModel(mainState))
}

@Preview(showBackground = true)
@Composable
fun PreviewMainScreenFetchError() {
    val mainState = MutableMainState(randomUsersError = "No Internet connection")
    MainScreen(mainState, getMainViewModel(mainState))
}

@Preview(showBackground = true)
@Composable
fun PreviewMainScreenSaveError() {
    val mainState = MutableMainState(saveUsersError = "Out of memory")
    MainScreen(mainState, getMainViewModel(mainState))
}

@Preview(showBackground = true)
@Composable
fun PreviewMainScreenFetchAndSaveErrors() {
    val mainState = MutableMainState(
        saveUsersError = "Out of memory",
        randomUsersError = "No Internet connection"
    )
    MainScreen(mainState, getMainViewModel(mainState))
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
