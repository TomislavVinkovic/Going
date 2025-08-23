package com.example.going.view.Auth

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.going.util.AuthScreen
import com.example.going.view.AppBottomNavigation
import com.example.going.viewmodel.AuthViewModel


@Composable
fun AuthScreenNavigation(
    authViewModel: AuthViewModel,
    mainNavController: NavHostController
) {
    val navController = rememberNavController()
    val snackbarHostState = remember { SnackbarHostState() }

    Scaffold(snackbarHost = { SnackbarHost(hostState = snackbarHostState) })
    { innerPadding ->
        AuthScreenNavHost(
            navController = navController,
            innerPadding = PaddingValues(),
            snackbarHostState,
            authViewModel,
            mainNavController
        )
    }
}

@Composable
fun AuthScreenNavHost(
    navController: NavHostController,
    innerPadding: PaddingValues,
    snackbarHostState: SnackbarHostState,
    authViewModel: AuthViewModel,
    mainNavController: NavHostController,
) {
    NavHost(
        navController = navController,
        startDestination = AuthScreen.Greeting.route,
        modifier = Modifier.padding(innerPadding)
    ) {

        // Notifications screen route
        composable(AuthScreen.Greeting.route) {
            GreetingScreen(
                navController = navController,
                mainNavController,
                authViewModel
            )
        }
        composable(AuthScreen.Login.route) {
            LoginScreen(
                navController = navController,
                mainNavController,
                authViewModel,
                snackbarHostState = snackbarHostState
            )
        }
        composable(AuthScreen.Register.route) {
            RegisterScreen(
                navController = navController,
                mainNavController,
                authViewModel,
                snackbarHostState = snackbarHostState
            )
        }
    }
}