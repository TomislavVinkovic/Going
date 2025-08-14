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
import com.example.going.view.GreetingScreen
import com.example.going.view.LoginScreen
import com.example.going.view.MainAppScreen
import com.example.going.view.RegisterScreen
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
                    Screen.Greeting.route
                }

                AppNavigation(startDestination = startDestination)
            }
        }
    }
}

@Composable
fun AppNavigation(startDestination: String) {
    val navController = rememberNavController()

    NavHost(navController = navController, startDestination = startDestination) {
        // Autentikacijski dio grafa
        composable(route = Screen.Greeting.route) {
            GreetingScreen(navController = navController)
        }
        composable(route = Screen.Login.route) {
            LoginScreen(navController = navController)
        }
        composable(route = Screen.Register.route) {
            RegisterScreen(navController = navController)
        }

        // Glavni dio aplikacije nakon prijave
        composable(route = Screen.MainApp.route) {
            MainAppScreen() // Ovaj ekran će imati svoj vlastiti Bottom Bar i navigaciju
        }
    }
}
