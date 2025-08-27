package com.example.going

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.navigation.compose.rememberNavController
import com.example.going.ui.theme.GoingTheme
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.going.util.Screen
import com.example.going.view.Auth.AuthScreenNavigation
import com.example.going.view.MainAppScreen
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
                val isLoggedIn by authViewModel.isUserLoggedIn.collectAsState()

                if (isLoggedIn) {
                    // If logged in, the ONLY thing that exists is the main app.
                    val mainNavController = rememberNavController()
                    MainAppScreen(authViewModel, mainNavController = mainNavController)
                } else {
                    // If not logged in, the ONLY thing that exists is the auth flow.
                    val authNavController = rememberNavController()
                    AuthScreenNavigation(authViewModel, mainNavController = authNavController)
                }
            }
        }
    }
}
