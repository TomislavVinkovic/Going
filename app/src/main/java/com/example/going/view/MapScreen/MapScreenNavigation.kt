package com.example.going.view.MapScreen

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.NavHostController
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.going.util.MapScreen
import com.example.going.util.ProfileScreen
import com.example.going.view.MapScreen.MapScreen
import com.example.going.view.ProfileScreen.ProfileScreen
import com.example.going.view.common.EventDetailsScreen
import com.example.going.viewmodel.AuthViewModel
import com.example.going.viewmodel.EventDetailsViewModel
import com.example.going.viewmodel.MapViewModel
import com.google.android.gms.maps.MapView

@Composable
fun MapScreenNavigation() {
    MapScreenNavHost(
        innerPadding = PaddingValues()
    )
}

@Composable
fun MapScreenNavHost(
    innerPadding: PaddingValues
) {
    val navController = rememberNavController()
    val mapViewModel: MapViewModel = viewModel()
    val eventDetailsViewModel: EventDetailsViewModel = viewModel()

    NavHost(
        navController = navController,
        startDestination = MapScreen.Map.route,
        modifier = Modifier.padding(innerPadding)
    ) {
        composable(MapScreen.Map.route) {
            MapScreen(
                navController=navController,
                mapViewModel=mapViewModel,
                eventDetailsViewModel=eventDetailsViewModel
            )
        }
        composable(MapScreen.EventDetails.route) {
            EventDetailsScreen(
                navController=navController,
                eventDetailsViewModel
            )
        }
    }
}