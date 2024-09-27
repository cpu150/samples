package com.example.ui

import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.compose.rememberNavController
import com.example.domain.Logger
import com.example.ui.nav.Ex2023NavHost
import com.example.ui.nav.Ex2023Screens
import com.example.ui.nav.UserDetailsScreen
import com.example.ui.nav.UserListScreen
import com.example.ui.theme.Ex2023Theme
import com.example.ui.userlist.UserListRootScreen
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.serialization.json.Json
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    @Inject
    lateinit var logger: Logger

    @Inject
    lateinit var json: Json

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        enableEdgeToEdge()
        setContent {
            Ex2023Theme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->

                    val navController = rememberNavController()
                    Ex2023NavHost(
                        navController = navController,
                        startDestination = UserListScreen,
                        json = json,
                        screens = object : Ex2023Screens {
                            @Composable
                            override fun LoadUserListScreen() {
                                UserListRootScreen(
                                    modifier = Modifier.padding(innerPadding),
                                    onUserClick = { user ->
                                        navController.navigate(UserDetailsScreen(user))
                                    },
                                    logger = logger,
                                )
                            }

                            @Composable
                            override fun LoadUserDetailsScreen() {
                                TODO("Not yet implemented")
                            }
                        },
                    )
                }
            }
        }
    }
}
