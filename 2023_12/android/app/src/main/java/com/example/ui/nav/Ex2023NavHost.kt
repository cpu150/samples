package com.example.ui.nav

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import kotlinx.serialization.json.Json

interface Ex2023Screens {
    @Composable
    fun LoadUserListScreen()

    @Composable
    fun LoadUserDetailsScreen()
}

@Composable
fun Ex2023NavHost(
    navController: NavHostController,
    startDestination: Any,
    json: Json = Json,
    screens: Ex2023Screens,
) {
    NavHost(
        navController = navController,
        startDestination = startDestination,
    ) {
        composable<UserListScreen> {
            screens.LoadUserListScreen()
        }
        composable<UserDetailsScreen>(typeMap = UserDetailsScreen.getTypeMap(json = json)) {
            // Example how to retrieve parameters:
            // val param = it.toRoute<UserDetailsScreen>()
            screens.LoadUserDetailsScreen()
        }
    }
}

