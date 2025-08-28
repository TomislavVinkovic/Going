package com.example.going.view.MyEventsScreen

import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.going.util.MyEventsScreen
import com.example.going.view.MyEventsScreen.MyEventsScreen
import com.example.going.view.common.EventDetailsScreen
import com.example.going.viewmodel.EventDetailsViewModel
import com.example.going.viewmodel.MyEventsViewModel

@OptIn(ExperimentalSharedTransitionApi::class)
@Composable
fun MyEventsScreenNavigation() {
    MyEventsScreenNavHost(innerPadding = PaddingValues())
}

@Composable
fun MyEventsScreenNavHost(innerPadding: PaddingValues) {
    val navController = rememberNavController()
    val eventDetailsViewModel: EventDetailsViewModel = viewModel()
    val myEventsViewModel: MyEventsViewModel = viewModel()

    NavHost(
        navController = navController,
        startDestination = MyEventsScreen.MyEvents.route,
        modifier = Modifier.padding(innerPadding)
    ) {
        composable(MyEventsScreen.MyEvents.route) {
            MyEventsScreen(
                navController=navController,
                eventDetailsViewModel = eventDetailsViewModel,
                myEventsViewModel=myEventsViewModel,
            )
        }
        composable(
            MyEventsScreen.EventDetails.route,
            enterTransition = {
                slideInVertically(initialOffsetY = { it })
            },
            popExitTransition = {
                slideOutVertically(targetOffsetY = { it })
            }
        ) {
            EventDetailsScreen(
                navController=navController,
                eventDetailsViewModel=eventDetailsViewModel,

            )
        }
    }

}