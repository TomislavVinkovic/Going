package com.example.going.view.common

import androidx.compose.animation.AnimatedVisibilityScope
import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.SharedTransitionScope
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.example.going.R

@OptIn(ExperimentalSharedTransitionApi::class)
@Composable
fun SearchBarUI(
    searchQuery: String,
    onQueryChanged: (String) -> Unit,
    onSearchClicked: () -> Unit,
    modifier: Modifier = Modifier,
    sharedTransitionScope: SharedTransitionScope? = null,
    animatedVisibilityScope: AnimatedVisibilityScope? = null,
    enabled: Boolean = true,
    focusRequester: FocusRequester? = null
) {

    @Composable
    fun SearchBarTextField() {
        OutlinedTextField(
            value = searchQuery,
            onValueChange = onQueryChanged,
            modifier = if(focusRequester != null)
                Modifier
                    .fillMaxWidth()
                    .focusRequester(focusRequester)
            else Modifier.fillMaxWidth()
            ,
            placeholder = { Text("Search events, locations...") },
            leadingIcon = { Icon(Icons.Default.Search, contentDescription = stringResource(R.string.map_screen_search_icon_description)) },
            trailingIcon = {
                if (searchQuery.isNotEmpty()) {
                    IconButton(onClick = { onQueryChanged("") }) {
                        Icon(Icons.Default.Clear, contentDescription = stringResource(R.string.map_screen_clear_icon_description))
                    }
                }
            },
            shape = RoundedCornerShape(32.dp),
            colors = OutlinedTextFieldDefaults.colors(
                unfocusedContainerColor = MaterialTheme.colorScheme.surfaceBright,
                focusedContainerColor = MaterialTheme.colorScheme.surfaceBright,
                unfocusedBorderColor = MaterialTheme.colorScheme.surfaceBright,
                focusedBorderColor = MaterialTheme.colorScheme.surfaceBright,
                disabledContainerColor = MaterialTheme.colorScheme.surfaceBright,
                disabledBorderColor = MaterialTheme.colorScheme.surfaceBright,
                disabledTextColor = MaterialTheme.colorScheme.onSurface,
                focusedTextColor = MaterialTheme.colorScheme.onSurface,
                unfocusedTextColor = MaterialTheme.colorScheme.onSurface
            ),
            singleLine = true,
            enabled = enabled
        )
    }


    if(sharedTransitionScope != null && animatedVisibilityScope != null) {
        with(sharedTransitionScope) {
            Box(
                modifier = modifier
                    .sharedElement(
                        state = rememberSharedContentState(key = "search-bar"),
                        animatedVisibilityScope = animatedVisibilityScope
                    )
            ) {
                SearchBarTextField()
                if (!enabled) {
                    Box(
                        modifier = Modifier
                            .matchParentSize()
                            .clickable(onClick = onSearchClicked)
                    )
                }
            }
        }
    }
    else {
        Box() {
            SearchBarTextField()
            if (!enabled) {
                Box(
                    modifier = Modifier
                        .matchParentSize()
                        .clickable(onClick = onSearchClicked)
                )
            }
        }

    }
}