package com.example.going.view.ProfileScreen

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.going.util.MainScreen
import com.example.going.util.ProfileScreen
import com.example.going.view.FriendsScreen.FriendsScreen
import com.example.going.view.MapScreen.MapScreen
import com.example.going.view.MyEventsScreen.MyEventsScreen
import com.example.going.view.NotificationsScreen.NotificationsScreen
import com.example.going.view.ProfileScreen.ProfileScreen
import com.example.going.viewmodel.ProfileViewModel

@Composable
fun ProfileScreenNavigation(navController: NavHostController, snackbarHostState: SnackbarHostState) {
    val navController = rememberNavController()
    ProfileScreenNavHost(
        navController = navController,
        innerPadding = PaddingValues(),
        snackbarHostState
    )
}

@Composable
fun ProfileScreenNavHost(
    navController: NavHostController,
    innerPadding: PaddingValues,
    snackbarHostState: SnackbarHostState
) {
    val profileViewModel: ProfileViewModel = viewModel()
    NavHost(
        navController = navController,
        startDestination = ProfileScreen.Profile.route,
        modifier = Modifier.padding(innerPadding)
    ) {

        // Notifications screen route
        composable(ProfileScreen.Profile.route) {
            ProfileScreen(
                navController = navController,
                profileViewModel = profileViewModel
            )
        }
        composable(ProfileScreen.EditProfileInformation.route) {
            EditProfileInformationScreen(
                navController = navController,
                profileViewModel = profileViewModel,
                snackbarHostState = snackbarHostState
            )
        }
    }
}