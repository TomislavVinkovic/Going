package com.example.going.util

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Event
import androidx.compose.material.icons.filled.Groups
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Map
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Person
import androidx.compose.ui.graphics.vector.ImageVector
import com.example.going.R

sealed class Screen(val route: String) {
    object MainApp : Screen("main_app_screen")
    object Auth: Screen("auth_screen")
}

sealed class AuthScreen(val route: String) {
    object Greeting : Screen("greeting_screen")
    object Login : Screen("login_screen")
    object Register : Screen("register_screen")

}

sealed class MainScreen(
    val route: String,
    val titleResId: Int,
    val icon: ImageVector
) {
    object Map: MainScreen(
        "main.map",
        R.string.screen_title_map,
        Icons.Filled.Map
    )
    object MyEvents: MainScreen(
        "main.my_events",
        R.string.screen_title_my_events,
        Icons.Filled.Event
    )
    object Friends: MainScreen(
        "main.friends",
        R.string.screen_title_friends,
        Icons.Filled.Groups
    )
    object Notifications: MainScreen(
        "main.notifications",
        R.string.screen_title_notifications,
        Icons.Filled.Notifications
    )
    object Profile: MainScreen(
        "main.profile",
        R.string.screen_title_profile,
        Icons.Filled.Person
    )

}

sealed class ProfileScreen(
    val route: String,
) {
    object Profile: ProfileScreen(
        "profile.profile",
    )
    object EditProfileInformation: ProfileScreen(
        "profile.edit_profile_info"
    )
}