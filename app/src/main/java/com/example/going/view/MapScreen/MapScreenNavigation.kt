package com.example.going.view.MapScreen

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.going.util.MapScreen
import com.example.going.view.common.EventDetailsScreen
import com.example.going.viewmodel.EventDetailsViewModel
import com.example.going.viewmodel.MapViewModel
import com.example.going.viewmodel.SearchViewModel

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
    val searchViewModel: SearchViewModel = viewModel()

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
        composable(MapScreen.Search.route) {
            SearchScreen(
                navController=navController,
                searchViewModel=searchViewModel
            )
        }
    }
}