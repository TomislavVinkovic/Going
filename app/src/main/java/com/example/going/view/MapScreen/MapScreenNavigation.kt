package com.example.going.view.MapScreen

import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.SharedTransitionLayout
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.going.util.MapScreen
import com.example.going.view.MapScreen.MapScreen
import com.example.going.view.common.EventDetailsScreen
import com.example.going.viewmodel.EventDetailsViewModel
import com.example.going.viewmodel.MapViewModel
import com.example.going.viewmodel.SearchViewModel
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically

@Composable
fun MapScreenNavigation() {
    MapScreenNavHost(
        innerPadding = PaddingValues()
    )
}

@OptIn(ExperimentalSharedTransitionApi::class)
@Composable
fun MapScreenNavHost(
    innerPadding: PaddingValues
) {
    val navController = rememberNavController()
    val mapViewModel: MapViewModel = viewModel()
    val eventDetailsViewModel: EventDetailsViewModel = viewModel()
    val searchViewModel: SearchViewModel = viewModel()

    SharedTransitionLayout {
        NavHost(
            navController = navController,
            startDestination = MapScreen.Map.route,
            modifier = Modifier.padding(innerPadding)
        ) {
            composable(MapScreen.Map.route) {
                MapScreen(
                    navController=navController,
                    mapViewModel=mapViewModel,
                    eventDetailsViewModel=eventDetailsViewModel,
                    sharedTransitionScope = this@SharedTransitionLayout,
                    animatedVisibilityScope = this
                )
            }
            composable(MapScreen.EventDetails.route) {
                EventDetailsScreen(
                    navController=navController,
                    eventDetailsViewModel,
                )
            }
            composable(MapScreen.Search.route) {
                SearchScreen(
                    navController=navController,
                    searchViewModel=searchViewModel,
                    sharedTransitionScope = this@SharedTransitionLayout,
                    animatedVisibilityScope = this,
                )
            }
        }
    }
}