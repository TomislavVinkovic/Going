package com.example.going.view.MapScreen.util

import android.R.attr.singleLine
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.example.going.R

@OptIn(ExperimentalSharedTransitionApi::class)
@Composable
fun SearchBarUI(
    searchQuery: String,
    onQueryChanged: (String) -> Unit,
    onSearchClicked: () -> Unit,
    sharedTransitionScope: SharedTransitionScope,
    animatedVisibilityScope: AnimatedVisibilityScope,
    modifier: Modifier = Modifier,
    enabled: Boolean,
    focusRequester: FocusRequester? = null
) {

    with(sharedTransitionScope) {
        Box(
            modifier = modifier
                .sharedElement(
                    state = rememberSharedContentState(key = "search-bar"),
                    animatedVisibilityScope = animatedVisibilityScope
                )
        ) {
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