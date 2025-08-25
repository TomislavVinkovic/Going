package com.example.going.view.MapScreen.util

import android.content.res.Configuration
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Place
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.example.going.R
import com.example.going.viewmodel.EventData
import java.security.Timestamp
import java.text.SimpleDateFormat

fun formatTimestamp(timestamp: com.google.firebase.Timestamp?, deviceConfig: Configuration): String {
    if (timestamp == null) return "N/A"

    // Get the primary locale from the current device configuration
    // val deviceLocale = LocalConfiguration.current.locales[0]

    // Use the device's locale to format the date and time
    val sdf = SimpleDateFormat("EEE, dd MMM, HH:mm", deviceConfig.locales[0])

    return sdf.format(timestamp.toDate())
}

@Composable
fun EventModalBottomSheet(
    selectedEvent: EventData?
) {

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp, vertical = 16.dp)
    ) {
        // Title
        Text(
            text = selectedEvent!!.name ?: "Event",
            style = MaterialTheme.typography.headlineSmall
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Location Row
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(
                imageVector = Icons.Filled.Place,
                contentDescription = "Location Icon",
                tint = MaterialTheme.colorScheme.primary
            )
            Spacer(modifier = Modifier.width(16.dp))
            Text(
                text = selectedEvent!!.locationName!!,
                style = MaterialTheme.typography.bodyMedium
            )
        }

        Spacer(modifier = Modifier.height(8.dp))

        // Start Time Row
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(
                imageVector = Icons.Filled.Schedule,
                contentDescription = "Start Time Icon",
                tint = MaterialTheme.colorScheme.primary
            )
            Spacer(modifier = Modifier.width(16.dp))
            Text(
                text = "${stringResource(R.string.map_screen_event_start)}: ${formatTimestamp(selectedEvent!!.startTime, LocalConfiguration.current)}",
                style = MaterialTheme.typography.bodyMedium
            )
        }

        Spacer(modifier = Modifier.height(8.dp))

        // End Time Row
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(
                imageVector = Icons.Filled.Schedule,
                contentDescription = "End Time Icon",
                tint = MaterialTheme.colorScheme.primary
            )
            Spacer(modifier = Modifier.width(16.dp))
            Text(
                text = "${stringResource(R.string.map_screen_event_end)}: ${formatTimestamp(selectedEvent!!.endTime, LocalConfiguration.current)}",
                style = MaterialTheme.typography.bodyMedium
            )
        }

        Spacer(modifier = Modifier.height(24.dp))

        // "More Info" Button
        Button(
            onClick = {
                // TODO: Navigate to the full details screen
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(stringResource(R.string.map_screen_more_info))
        }
    }
}