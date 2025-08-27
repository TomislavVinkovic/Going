package com.example.going.view

import android.R.attr.onClick
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.navigation.NavController
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.going.util.MainScreen
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.NavHostController
import androidx.navigation.compose.composable
import com.example.going.view.FriendsScreen.FriendsScreen
import com.example.going.view.MapScreen.MapScreen
import com.example.going.view.MapScreen.MapScreenNavigation
import com.example.going.view.MyEventsScreen.MyEventsScreen
import com.example.going.view.NotificationsScreen.NotificationsScreen
import com.example.going.view.ProfileScreen.ProfileScreenNavigation
import com.example.going.viewmodel.AuthViewModel
import com.example.going.viewmodel.EventDetailsViewModel
import com.example.going.viewmodel.MapViewModel
import com.example.going.viewmodel.MyEventsViewModel
import com.example.going.viewmodel.SearchViewModel

private val screens = listOf(
    // MainScreen.Friends,
    MainScreen.MyEvents,
    MainScreen.Map,
    // MainScreen.Notifications,
    MainScreen.Profile
)

@Composable
fun MainAppScreen(
    authViewModel: AuthViewModel,
    mainNavController: NavHostController
) {
    val navController = rememberNavController()
    val snackbarHostState = remember { SnackbarHostState() }
    Scaffold(
       snackbarHost = { SnackbarHost(hostState = snackbarHostState) },
       bottomBar = {
            AppBottomNavigation(navController)
       }
    ) { innerPadding ->
        MainAppNavHost(
            navController,
            mainNavController,
            innerPadding,
            snackbarHostState,
            authViewModel
        )
    }
}

@Composable
fun AppMainScreenPlaceholder(text: String) {
    Box(
        modifier = Modifier.fillMaxSize()
            .padding(16.dp)
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(text)
        }
    }
}

@Composable
fun MainAppNavHost(
    navController: NavHostController,
    mainNavController: NavHostController,
    innerPadding: PaddingValues,
    snackbarHostState: SnackbarHostState,
    authViewModel: AuthViewModel
) {

    val mapViewModel: MapViewModel = viewModel()
    val myEventsViewModel: MyEventsViewModel = viewModel()

    NavHost(
        navController = navController,
        startDestination = MainScreen.Map.route,
        modifier = Modifier.padding(innerPadding)
    ) {


        // Friends screen route
        // composable(MainScreen.Friends.route) { FriendsScreen(navController = navController) }

        // Notifications screen route
        // composable(MainScreen.Notifications.route) { NotificationsScreen(navController = navController) }

        composable(MainScreen.MyEvents.route) {
            MyEventsScreen(
                navController = navController,
                myEventsViewModel = myEventsViewModel
            )
        }
        composable(MainScreen.Map.route) {
            MapScreenNavigation(
                mapViewModel = mapViewModel
            )
        }


        // Profile screen routes
        composable(MainScreen.Profile.route) {
            ProfileScreenNavigation(
                mainNavController = mainNavController,
                snackbarHostState = snackbarHostState,
                authViewModel = authViewModel
            )
        }
    }
}

@Composable
fun AppBottomNavigation(navController: NavController) {
    NavigationBar {
        val navBackStackEntry by navController.currentBackStackEntryAsState()
        val currentRoute = navBackStackEntry?.destination?.route

        screens.forEach { screen ->
            NavigationBarItem(
                icon = {Icon(screen.icon, contentDescription = stringResource(screen.titleResId))},
                selected = currentRoute == screen.route,
                label = {
                    Text(stringResource(screen.titleResId))
                },
                onClick = {
                    navController.navigate(screen.route) {
                        navController.graph.startDestinationId
                        launchSingleTop = true
                    }
                }
            )
        }
    }
}