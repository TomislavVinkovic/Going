package com.example.going.view.MapScreen

import androidx.compose.animation.AnimatedVisibilityScope
import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.SharedTransitionScope
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.example.going.util.CategoryIcons.CategoryIcons
import com.example.going.view.MapScreen.util.SearchBarUI
import com.example.going.viewmodel.SearchViewModel
import androidx.compose.ui.Alignment
import androidx.compose.ui.focus.FocusRequester


@OptIn(ExperimentalSharedTransitionApi::class, ExperimentalLayoutApi::class)
@Composable
fun SearchScreen(
    navController: NavHostController,
    searchViewModel: SearchViewModel,
    sharedTransitionScope: SharedTransitionScope,
    animatedVisibilityScope: AnimatedVisibilityScope
) {

    val focusRequester = remember { FocusRequester() }

    val searchQuery by searchViewModel.searchQuery.collectAsState()
    val selectedCategory by searchViewModel.selectedCategory.collectAsState()
    val searchResults by searchViewModel.searchResults.collectAsState()
    val searchState by searchViewModel.searchState.collectAsState()

    LaunchedEffect(Unit) {
        focusRequester.requestFocus()
    }

    Column(
        modifier = Modifier.fillMaxSize()
            .padding(16.dp)
    ) {
        SearchBarUI(
            searchQuery = searchQuery,
            onQueryChanged = { searchViewModel.onQueryChanged(it) },
            onSearchClicked = { },
            sharedTransitionScope,
            animatedVisibilityScope,
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.CenterHorizontally),
            enabled = true,
            focusRequester = focusRequester,
        )

        Spacer(Modifier.height(16.dp))

        FlowRow(
            horizontalArrangement = Arrangement.spacedBy(4.dp), // Space between chips on the same line
            verticalArrangement = Arrangement.spacedBy(4.dp),
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 8.dp)
        ) {
            CategoryIcons.keys.forEach { category ->
                FilterChip(
                    selected = category == selectedCategory,
                    onClick = { searchViewModel.onCategorySelected(category) },
                    label = { Text(text = category) },
                    leadingIcon = {Text(CategoryIcons[category] ?: "")},
                )
            }
        }

        Spacer(Modifier.height(16.dp))

        if(searchState.isLoading) {
            CircularProgressIndicator()
        }
        else {
            LazyColumn {
                items(searchResults) { event ->
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