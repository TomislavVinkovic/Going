package com.example.going.view.MyEventsScreen

import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.going.view.common.SearchBarUI
import com.example.going.viewmodel.MyEventsViewModel

@OptIn(ExperimentalSharedTransitionApi::class)
@Composable
fun MyEventsScreen(
    navController: NavController,
    myEventsViewModel: MyEventsViewModel = viewModel()
) {

    val myEvents = myEventsViewModel.searchResults.collectAsState()
    val searchState = myEventsViewModel.searchState.collectAsState()

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
    }
}