package com.example.ui.userdetails

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.example.common.dpToPx
import com.example.common.toUri
import com.example.example2023.R

@Composable
fun UserDetailsScreenRoot(
    modifier: Modifier = Modifier,
    userDetailsViewModel: UserDetailsViewModel = hiltViewModel<UserDetailsViewModelImp>(),
) {
    val placeholderPic = painterResource(R.mipmap.placeholder)
    val state by userDetailsViewModel.state.collectAsStateWithLifecycle()

    Column(
        Modifier
            .fillMaxHeight()
            .padding(horizontal = 24.dp),
        verticalArrangement = Arrangement.SpaceBetween,
    ) {
        Column(
            modifier = modifier
                .verticalScroll(rememberScrollState())
                .padding(top = 24.dp)
                .weight(1f, false)
        ) {
            with(state.user) {
                Row(
                    horizontalArrangement = Arrangement.Center,
                    modifier = Modifier
                        .wrapContentHeight()
                        .fillMaxWidth()
                        .padding(bottom = 32.dp)
                ) {
                    AsyncImage(
                        model = ImageRequest.Builder(LocalContext.current)
                            .data(picLargeUrl?.toUri())
                            .crossfade(true)
                            .size(240.dpToPx(LocalContext.current))
                            .build(),
                        contentDescription = "Profile Picture",
                        placeholder = placeholderPic,
                        error = placeholderPic,
                        contentScale = ContentScale.FillHeight,
                        modifier = Modifier
                            .clip(CircleShape)
                            .border(2.dp, Color.Gray, CircleShape)
                            .width(240.dp)
                            .height(240.dp),
                    )
                }
                Text(
                    modifier = Modifier.fillMaxWidth(),
                    textAlign = TextAlign.Center,
                    text = "${title.getString(LocalContext.current)}  $firstName $lastName".trim(),
                    fontSize = 24.sp,
                )
                var cpt = remember { 0 }
                while (cpt++ < 30) {
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(text = "$email")
                }
            }
        }
        Button(
            onClick = {
                when (state.isUserSaved) {
                    true -> userDetailsViewModel.deleteUser()
                    false -> userDetailsViewModel.saveUser()
                    null -> Unit
                }
            },
            modifier = Modifier
                .padding(bottom = 24.dp)
                .fillMaxWidth()
        ) {
            when (state.isUserSaved) {
                true -> Text("Delete")
                false -> Text("Save")
                null -> CircularProgressIndicator()
            }
        }
    }
}
