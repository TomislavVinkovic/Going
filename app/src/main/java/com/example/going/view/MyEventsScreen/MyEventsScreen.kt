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
import androidx.compose.material3.ListItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.going.view.common.CategorySelectorUI
import com.example.going.view.common.SearchBarUI
import com.example.going.viewmodel.MyEventsViewModel

@OptIn(ExperimentalSharedTransitionApi::class)
@Composable
fun MyEventsScreen(
    navController: NavController,
    myEventsViewModel: MyEventsViewModel = viewModel()
) {

    val myEvents by myEventsViewModel.searchResults.collectAsState()
    val searchState by myEventsViewModel.searchState.collectAsState()

    val searchQuery by myEventsViewModel.searchQuery.collectAsState()
    val selectedCategory by myEventsViewModel.selectedCategory.collectAsState()

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
                modifier = Modifier.fillMaxSize()
            ) {
                CircularProgressIndicator()
            }
        }
        else {
            LazyColumn {
                items(myEvents) { event ->
                    ListItem(
                        headlineContent = {Text(event.name ?: "")},
                        supportingContent = {Text(event.locationName ?: "")},
                        modifier = Modifier.clickable {
                            navController.previousBackStackEntry
                                ?.savedStateHandle
                                ?.set("selected_event_lat", event.position.latitude)
                            navController.previousBackStackEntry
                                ?.savedStateHandle
                                ?.set("selected_event_lng", event.position.longitude)
                            navController.popBackStack()
                        }
                    )
                }
            }
        }
    }
}