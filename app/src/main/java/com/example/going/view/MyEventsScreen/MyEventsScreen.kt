package com.example.going.view.MyEventsScreen

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.going.viewmodel.MyEventsViewModel

@Composable
fun MyEventsScreen(
    navController: NavController,
    myEventsViewModel: MyEventsViewModel = viewModel()
) {

    val myEvents = myEventsViewModel.searchResults.collectAsState()
    val searchState = myEventsViewModel.searchState.collectAsState()

    val searchQuery = myEventsViewModel.searchQuery.collectAsState()
    val selectedCategory = myEventsViewModel.selectedCategory.collectAsState()

    Column(
        modifier = Modifier.fillMaxSize()
            .padding(16.dp)
    ) {

    }
}