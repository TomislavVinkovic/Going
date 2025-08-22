package com.example.going

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.runtime.Composable
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.navigation.compose.rememberNavController
import com.example.going.ui.theme.GoingTheme
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.going.util.Screen
import com.example.going.view.Auth.AuthScreenNavigation
import com.example.going.view.Auth.GreetingScreen
import com.example.going.view.Auth.LoginScreen
import com.example.going.view.MainAppScreen
import com.example.going.view.Auth.RegisterScreen
import com.example.going.viewmodel.AuthViewModel

class MainActivity : ComponentActivity() {
    private val authViewModel: AuthViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        installSplashScreen().apply {
            // Zadrži splash screen vidljivim dok ne odlučimo kamo idemo.
            setKeepOnScreenCondition {
                authViewModel.isLoading.value
            }
        }

        setContent {
            GoingTheme {
                val startDestination = if(authViewModel.isUserLoggedIn.value) {
                    Screen.MainApp.route
                } else {
                    Screen.Auth.route
                }

                AppNavigation(
                    startDestination = startDestination,
                    authViewModel
                )
            }
        }
    }
}

@Composable
fun AppNavigation(startDestination: String, authViewModel: AuthViewModel) {
    val navController = rememberNavController()

    NavHost(navController = navController, startDestination = startDestination) {
        composable(route = Screen.Auth.route) {
            AuthScreenNavigation(navController, authViewModel)
        }
        composable(route = Screen.MainApp.route) {
            MainAppScreen(navController, authViewModel)
        }
    }
}
