package com.example.going.view.common

import androidx.compose.foundation.Image
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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Place
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.FabPosition
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SuggestionChip
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.navigation.NavController
import com.example.going.viewmodel.EventDetailsViewModel
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.going.R
import com.google.firebase.Timestamp
import java.text.SimpleDateFormat
import coil.compose.AsyncImage

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun EventDetailsScreen(
    navController: NavController,
    eventDetailsViewModel: EventDetailsViewModel = viewModel()
) {
    val event by eventDetailsViewModel.eventDetails.collectAsState()

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
            ExtendedFloatingActionButton(
                onClick = { /* TODO: Implement "I'm interested" logic */ },
                icon = { Icon(Icons.Filled.Star, contentDescription = null) },
                text = { Text(stringResource(R.string.event_details_screen_im_interested_button)) }
            )
        },
        floatingActionButtonPosition = FabPosition.Center
    ) { innerPadding ->
        if (event == null) {
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
                    Text("Opis", style = MaterialTheme.typography.titleLarge)
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
                }
                item {
                    Spacer(modifier = Modifier.height(24.dp))
                    Text(stringResource(R.string.event_details_interested_users), style = MaterialTheme.typography.titleLarge)
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
private fun formatTimestamp(timestamp: Timestamp?): String {
    if (timestamp == null) return "N/A"
    val deviceLocale = LocalConfiguration.current.locales[0]
    val sdf = SimpleDateFormat("EEE, dd. MMM, HH:mm", deviceLocale)
    return sdf.format(timestamp.toDate())
}