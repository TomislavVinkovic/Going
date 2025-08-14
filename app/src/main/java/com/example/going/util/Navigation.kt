package com.example.going.util

sealed class Screen(val route: String) {
    object Greeting : Screen("greeting_screen")
    object Login : Screen("login_screen")
    object Register : Screen("register_screen")
    object MainApp : Screen("main_app_screen")
}