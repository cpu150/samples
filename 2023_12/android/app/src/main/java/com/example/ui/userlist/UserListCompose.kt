package com.example.ui.userlist

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.example.common.dpToPx
import com.example.common.toUri
import com.example.domain.Logger
import com.example.domain.model.User
import com.example.domain.model.UserGender
import com.example.domain.model.UserTitle
import com.example.example2023.R
import com.example.ui.ScreenState
import com.example.ui.theme.Ex2023Theme
import java.net.URL
import java.time.LocalDateTime

@Composable
fun UserListRootScreen(
    modifier: Modifier = Modifier,
    onUserClick: (user: User) -> Unit = {},
    userListViewModel: UserListViewModel = hiltViewModel<UserListViewModelImpl>(),
    logger: Logger? = null,
) {
    val stateFlow by userListViewModel.state.collectAsStateWithLifecycle()

    when (val screenState = stateFlow.screenState) {
        is ScreenState.Error -> ErrorScreen(screenState = screenState)
        ScreenState.Loaded -> UserList(stateFlow.remoteRandomUsers, modifier, onUserClick)
        ScreenState.Initializing, is ScreenState.Loading -> LoadingScreen(modifier)
    }

    logger?.d("UI updated with $stateFlow")
}

@Composable
fun LoadingScreen(modifier: Modifier = Modifier) {
    Box(
        modifier = modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        CircularProgressIndicator()
    }
}

@Composable
fun ErrorScreen(modifier: Modifier = Modifier, screenState: ScreenState.Error) {
    Box(
        modifier = modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Text(text = "SCREEN ERROR (${screenState.message})")
    }
}

@Composable
fun UserList(
    users: List<User>,
    modifier: Modifier = Modifier,
    onUserClick: (user: User) -> Unit = {}
) {
    LazyColumn(modifier = modifier) {
        items(items = users, key = { user -> user.firstName + user.lastName }) { user ->
            UserItem(user, onUserClick)
        }
    }
}

@Composable
fun UserItem(user: User, onUserClick: (user: User) -> Unit = {}) {
    val placeholderPic = painterResource(R.mipmap.placeholder)

    with(user) {
        ElevatedCard(
            elevation = CardDefaults.cardElevation(defaultElevation = 6.dp),
            colors = CardDefaults.cardColors(containerColor = Color(0xFFCCDDDD)),
            modifier = Modifier
                .fillMaxWidth()
                .height(80.dp)
                .padding(vertical = 4.dp, horizontal = 20.dp)
                .clickable(onClick = { onUserClick(user) }),
        ) {
            Row {
                AsyncImage(
                    model = ImageRequest.Builder(LocalContext.current)
                        .data(picMediumUrl?.toUri())
                        .crossfade(true)
                        .size(80.dpToPx(LocalContext.current))
                        .build(),
                    contentDescription = "Profile Picture",
                    placeholder = placeholderPic,
                    error = placeholderPic,
                    modifier = Modifier
                        .fillMaxHeight()
                        .width(80.dp),
                )
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 4.dp),
                ) {
                    Text(
                        "${title.getString(LocalContext.current)} $firstName $lastName".trim(),
                        fontSize = 12.sp
                    )
                    Text("email: $email", fontSize = 12.sp)
                }
            }
        }
    }
}

private fun dummyUser(
    title: UserTitle = UserTitle.MR,
    firstName: String = "First",
    lastName: String = "Last",
    gender: UserGender = UserGender.UNKNOWN,
    email: String = "dummy@example.com",
    birthDate: LocalDateTime = LocalDateTime.MIN,
    age: Int = -1,
    picLarge: URL? = URL("https://example.com/picLarge.png"),
    picMedium: URL? = URL("https://example.com/picMedium.png"),
    picSmall: URL? = URL("https://example.com/picSmall.png"),
) = User(title, firstName, lastName, gender, email, birthDate, age, picLarge, picMedium, picSmall)

@Preview(showBackground = true)
@Composable
fun ScreenLoadingPreview() {
    Ex2023Theme {
        LoadingScreen()
    }
}

@Preview(showBackground = true)
@Composable
fun UserItemPreview() {
    Ex2023Theme {
        UserItem(dummyUser())
    }
}

@Preview(showBackground = true)
@Composable
fun UserListPreview() {
    val users = listOf(
        dummyUser(),
        dummyUser(
            UserTitle.MISS,
            "Jane",
            "Doe",
            email = "jane.doe@example.com",
            picLarge = null,
            picMedium = null,
            picSmall = null
        ),
        dummyUser(
            UserTitle.UNKNOWN,
            "John",
            "Doe",
            email = "john.doe@example.com",
            picLarge = URL("https://example.com/johnLarge.png"),
            picMedium = URL("https://example.com/johnMedium.png"),
            picSmall = URL("https://example.com/johnSmall.png"),
        ),
    )
    Ex2023Theme {
        UserList(users)
    }
}
