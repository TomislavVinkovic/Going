package com.example.going.view.MyEventsScreen

import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.going.R
import com.example.going.util.MyEventsScreen
import com.example.going.view.common.CategorySelectorUI
import com.example.going.view.common.SearchBarUI
import com.example.going.viewmodel.EventDetailsViewModel
import com.example.going.viewmodel.MyEventsViewModel

@OptIn(ExperimentalSharedTransitionApi::class)
@Composable
fun MyEventsScreen(
    navController: NavController,
    eventDetailsViewModel: EventDetailsViewModel = viewModel(),
    myEventsViewModel: MyEventsViewModel = viewModel()
) {

    val myEvents by myEventsViewModel.searchResults.collectAsState()
    val searchState by myEventsViewModel.searchState.collectAsState()

    val searchQuery by myEventsViewModel.searchQuery.collectAsState()
    val selectedCategory by myEventsViewModel.selectedCategory.collectAsState()

    val shouldRefresh = navController.currentBackStackEntry
        ?.savedStateHandle
        ?.get<Boolean>("list_should_refresh")

    LaunchedEffect(shouldRefresh) {
        if (shouldRefresh == true) {
            myEventsViewModel.refreshEvents()
            // Important: Clear the result so it doesn't re-trigger
            navController.currentBackStackEntry
                ?.savedStateHandle
                ?.remove<Boolean>("list_should_refresh")
        }
    }

    Column(
        modifier = Modifier.fillMaxSize()
            .padding(16.dp)
    ) {
        SearchBarUI(
            searchQuery = searchQuery,
            onQueryChanged = { myEventsViewModel.onQueryChanged(it) },
            onSearchClicked = { },
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.CenterHorizontally),
            enabled = true
        )

        Spacer(Modifier.height(16.dp))

        CategorySelectorUI(
            selectedCategory = selectedCategory,
            onClick = { myEventsViewModel.onCategorySelected(it) }
        )

        if(searchState.isLoading) {
            Box(
                modifier = Modifier
                    .fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }
        }
        else {
            if(myEvents.isNotEmpty()) {
                LazyColumn {
                    items(myEvents) { event ->
                        ListItem(
                            headlineContent = {Text(event.name ?: "")},
                            supportingContent = {Text(event.locationName ?: "")},
                            modifier = Modifier.clickable {
                                eventDetailsViewModel.setEventId(event.id)
                                navController.navigate(MyEventsScreen.EventDetails.route)
                            }
                        )
                        HorizontalDivider()
                    }
                }
            }
            else {
                Box(
                    modifier = Modifier
                        .fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        stringResource(R.string.myevents_no_events),
                        style = TextStyle(
                            textAlign = TextAlign.Center,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    )
                }
            }
        }
    }
}