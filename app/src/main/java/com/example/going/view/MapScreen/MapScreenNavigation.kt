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
import androidx.compose.material3.SnackbarHostState

@Composable
fun MapScreenNavigation(
    mapViewModel: MapViewModel,
    snackbarHostState: SnackbarHostState,
) {
    MapScreenNavHost(
        mapViewModel = mapViewModel,
        snackbarHostState = snackbarHostState,
        innerPadding = PaddingValues()
    )
}

@OptIn(ExperimentalSharedTransitionApi::class)
@Composable
fun MapScreenNavHost(
    mapViewModel: MapViewModel,
    snackbarHostState: SnackbarHostState,
    innerPadding: PaddingValues
) {
    val navController = rememberNavController()
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
                    snackBarHostState=snackbarHostState,
                    eventDetailsViewModel=eventDetailsViewModel,
                    sharedTransitionScope = this@SharedTransitionLayout,
                    animatedVisibilityScope = this
                )
            }
            composable(MapScreen.EventDetails.route) {
                EventDetailsScreen(
                    navController=navController,
                    snackBarHostState=snackbarHostState,
                    eventDetailsViewModel=eventDetailsViewModel,
                )
            }
            composable(
                route = MapScreen.Search.route,
                enterTransition = {
                    slideInVertically(initialOffsetY = { it })
                },
                popExitTransition = {
                    slideOutVertically(targetOffsetY = { it })
                }
            ) {
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