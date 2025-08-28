package com.example.going.view.common

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Place
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.FabPosition
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.SuggestionChip
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.navigation.NavController
import com.example.going.viewmodel.EventDetailsViewModel
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.going.R
import com.google.firebase.Timestamp
import java.text.SimpleDateFormat
import coil.compose.AsyncImage
import com.example.going.model.ProfileUserData
import com.example.going.view.MapScreen.util.formatTimestamp
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun EventDetailsScreen(
    navController: NavController,
    eventDetailsViewModel: EventDetailsViewModel = viewModel()
) {
    val context = LocalContext.current
    val event by eventDetailsViewModel.eventDetails.collectAsState()
    val interestedUsers by eventDetailsViewModel.interestedUsers.collectAsState()
    val isUserInterested by eventDetailsViewModel.isUserInterested.collectAsState()

    val scope = rememberCoroutineScope()
    val snackbarHostState = remember { SnackbarHostState() }

    val eventFetchState by eventDetailsViewModel.eventFetchState.collectAsState()
    val toggleInterestState by eventDetailsViewModel.toggleInterestState.collectAsState()
    val checkInterestState by eventDetailsViewModel.checkInterestState.collectAsState()
    val userListFetchState by eventDetailsViewModel.userListFetchState.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(event?.name ?: stringResource(R.string.event_details_screen_event_name)) },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(
                            Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = stringResource(R.string.event_details_screen_back_button)
                        )
                    }
                }
            )
        },
        floatingActionButton = {
            // --- MODIFIED: The button is now dynamic ---
            ExtendedFloatingActionButton(
                onClick = {
                    eventDetailsViewModel.toggleInterest(context)
                    navController.previousBackStackEntry
                        ?.savedStateHandle
                        ?.set("list_should_refresh", true)
                    scope.launch {
                        snackbarHostState.showSnackbar(
                            if (isUserInterested) context.getString(R.string.event_details_screen_interested)
                            else context.getString(R.string.event_details_screen_uninterested),
                            duration = SnackbarDuration.Short
                        )
                    }
                },
                // Change the icon based on the interest state
                icon = {
                    if(!toggleInterestState.isLoading) {
                        Icon(
                            if (isUserInterested) Icons.Filled.Close else Icons.Filled.Star,
                            contentDescription = null
                        )
                    }
                },
                // Change the text based on the interest state
                text = {
                    if(toggleInterestState.isLoading) {
                        CircularProgressIndicator(
                            color = if(isUserInterested) MaterialTheme.colorScheme.primary
                                else MaterialTheme.colorScheme.secondary
                        )
                    }
                    else {
                        Text(
                            if (isUserInterested) stringResource(R.string.event_details_cancel_interest)
                            else stringResource(R.string.event_details_screen_im_interested_button)
                        )
                    }
                },
                // Change the color based on the interest state
                containerColor = if (isUserInterested) MaterialTheme.colorScheme.primary
                else MaterialTheme.colorScheme.primaryContainer,
                contentColor = if (isUserInterested) MaterialTheme.colorScheme.onPrimary
                else MaterialTheme.colorScheme.onPrimaryContainer,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            )
        },
        floatingActionButtonPosition = FabPosition.Center
    ) { innerPadding ->
        if (eventFetchState.isLoading || event == null) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
                    .padding(horizontal = 16.dp)
            ) {
                item {
                    Spacer(modifier = Modifier.height(16.dp))
                    AsyncImage(
                        model = event?.image,
                        contentDescription = event?.name ?: stringResource(R.string.event_details_screen_event_name),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(200.dp)
                            .clip(RoundedCornerShape(12.dp)),
                        contentScale = ContentScale.Crop,
                        // Optional: Show a placeholder while the image is loading
                        placeholder = painterResource(id = R.drawable.ic_launcher_background),
                        // Optional: Show an error image if the URL fails to load
                        error = painterResource(id = R.drawable.ic_launcher_background)
                    )
                    Spacer(modifier = Modifier.height(24.dp))
                }

                item {
                    EventInfoRow(
                        icon = Icons.Filled.Place,
                        text = event!!.locationName ?: stringResource(R.string.event_details_screen_unknown_location)
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    EventInfoRow(
                        icon = Icons.Filled.Schedule,
                        text = "${stringResource(R.string.map_screen_event_start)}: ${formatTimestamp(event!!.startTime)}"
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    EventInfoRow(
                        icon = Icons.Filled.Schedule,
                        text = "${stringResource(R.string.map_screen_event_end)}: ${formatTimestamp(event!!.endTime)}"
                    )
                }

                item {
                    Spacer(modifier = Modifier.height(24.dp))
                    Text(stringResource(R.string.event_details_screen_description), style = MaterialTheme.typography.titleLarge)
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = event!!.description ?: stringResource(R.string.event_details_screen_no_description),
                        style = MaterialTheme.typography.bodyMedium
                    )
                }

                item {
                    Spacer(modifier = Modifier.height(24.dp))
                    Text(stringResource(R.string.event_details_screen_tags), style = MaterialTheme.typography.titleLarge)
                    Spacer(modifier = Modifier.height(8.dp))
                    FlowRow(
                        horizontalArrangement = Arrangement.spacedBy(8.dp), // Space between chips on the same line
                        verticalArrangement = Arrangement.spacedBy(8.dp)   // Space between rows of chips
                    ) {
                        event!!.tags?.forEach { tag ->
                            SuggestionChip(onClick = {}, label = { Text("#${tag}") })
                        }
                    }
                    Spacer(modifier = Modifier.height(24.dp))
                }
                item {
                    Text(stringResource(R.string.event_details_interested_users), style = MaterialTheme.typography.titleLarge)
                    Spacer(modifier = Modifier.height(8.dp))
                }
                if (userListFetchState.isLoading) {
                    item {
                        Box(modifier = Modifier.fillMaxWidth().padding(vertical = 16.dp), contentAlignment = Alignment.Center) {
                            CircularProgressIndicator()
                        }
                    }
                } else {
                    if (interestedUsers.isNotEmpty()) {
                        items(interestedUsers) { user ->
                            InterestedUserListItem(user)
                        }
                    } else {
                        item {
                            Box(modifier = Modifier.fillMaxWidth().padding(vertical = 16.dp), contentAlignment = Alignment.Center) {
                                Text(stringResource(R.string.event_details_screen_no_interested_users))
                            }
                        }
                    }
                }
                item {
                    Spacer(modifier = Modifier.height(100.dp))
                }
            }
        }
    }
}

@Composable
private fun EventInfoRow(icon: ImageVector, text: String) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Icon(
            imageVector = icon,
            contentDescription = null, // Ikona je dekorativna
            tint = MaterialTheme.colorScheme.primary
        )
        Spacer(modifier = Modifier.width(16.dp))
        Text(text = text, style = MaterialTheme.typography.bodyLarge)
    }
}

@Composable
private fun InterestedUserListItem(user: ProfileUserData) {
    ListItem(
        headlineContent = {Text("${user.firstname} ${user.lastname}")},
        supportingContent = {Text(user.username ?: "")},
        leadingContent = {
            AsyncImage(
                model = user.avatarUrl,
                placeholder = painterResource(id = R.drawable.ic_launcher_background),
                error = painterResource(id = R.drawable.ic_launcher_background),
                contentDescription = null,
                modifier = Modifier
                    .size(48.dp)
                    .clip(RoundedCornerShape(50)),
                contentScale = ContentScale.Crop
            )
        }
    )
}

@Composable
private fun formatTimestamp(timestamp: Timestamp?): String {
    if (timestamp == null) return "N/A"
    val deviceLocale = LocalConfiguration.current.locales[0]
    val sdf = SimpleDateFormat("EEE, dd. MMM, HH:mm", deviceLocale)
    return sdf.format(timestamp.toDate())
}