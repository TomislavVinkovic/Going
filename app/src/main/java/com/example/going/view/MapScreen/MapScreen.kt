package com.example.going.view.MapScreen

import android.Manifest
import android.app.ProgressDialog.show
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Paint
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.going.viewmodel.MapViewModel
import androidx.compose.runtime.setValue
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.platform.LocalContext
import androidx.core.content.ContextCompat
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.MapProperties
import com.google.maps.android.compose.Marker
import com.google.maps.android.compose.rememberCameraPositionState
import com.google.maps.android.compose.rememberMarkerState
import com.example.going.R
import com.example.going.view.MapScreen.util.CustomMapMarkerIcon
import com.example.going.view.MapScreen.util.EventModalBottomSheet
import com.example.going.viewmodel.EventData
import com.google.android.gms.maps.model.MapStyleOptions

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MapScreen(
    navController: NavController,
    mapViewModel: MapViewModel = viewModel()
) {
    val events by mapViewModel.events.collectAsState()
    val sheetState = rememberModalBottomSheetState()
    val userLocation by mapViewModel.userLocation.collectAsState()
    val context = LocalContext.current

    val isDarkTheme = isSystemInDarkTheme()
    val primaryColor = MaterialTheme.colorScheme.primary

    var selectedEvent by remember {mutableStateOf<EventData?>(null)}

    var hasLocationPermission by remember {
        mutableStateOf(
            ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
                    || ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        )
    }

    val locationPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission(),
        onResult = { isGranted ->
            hasLocationPermission = isGranted
            if (isGranted) {
                // Permission Granted: fetch the location
                mapViewModel.fetchUserLocation(context)
            } else {
                // TODO: Handle error
            }
        }
    )

    val cameraPositionState = rememberCameraPositionState()
    LaunchedEffect(userLocation) {
        userLocation?.let {
            cameraPositionState.animate(
                update = CameraUpdateFactory.newLatLngZoom(it, 14f),
                durationMs = 1000
            )
        }
        }

    val mapStyleOptions = remember(isDarkTheme) {
        if(isDarkTheme) {
            MapStyleOptions.loadRawResourceStyle(context, R.raw.map_style_dark)
        } else {
            MapStyleOptions.loadRawResourceStyle(context, R.raw.map_style_light)
        }
    }

    LaunchedEffect(Unit) {
        when(PackageManager.PERMISSION_GRANTED) {
            ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) -> {
                // Permission is already granted, update user location
                mapViewModel.fetchUserLocation(context)
            }
            ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) -> {
                mapViewModel.fetchUserLocation(context)
            }
            else -> {
                locationPermissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION)
            }
        }
    }

    GoogleMap(
        modifier = Modifier.fillMaxSize(),
        cameraPositionState = cameraPositionState,
        properties = MapProperties(
            isMyLocationEnabled = hasLocationPermission,
            mapStyleOptions = mapStyleOptions
        )
    ) {

        events.forEach { event ->
            val customMapMarkerIcon = remember(event.category!!, primaryColor) {
                // Pass the theme color to the function
                CustomMapMarkerIcon(event.category!!, primaryColor)
            }
            Marker(
                state = rememberMarkerState(position = event.position),
                onClick = {
                    selectedEvent = event
                    true
                },
                icon = customMapMarkerIcon,
                anchor = Offset(0.5f, 0.5f)
            )
        }
    }

    if(selectedEvent != null) {
        ModalBottomSheet(
            onDismissRequest = {
                selectedEvent = null
            },
            sheetState=sheetState
        ) {
            EventModalBottomSheet(selectedEvent)
        }
    }
    
}