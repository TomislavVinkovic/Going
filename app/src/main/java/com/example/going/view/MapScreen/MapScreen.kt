package com.example.going.view.MapScreen

import android.Manifest
import android.content.pm.PackageManager
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibilityScope
import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.SharedTransitionScope
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.MapProperties
import com.google.maps.android.compose.Marker
import com.google.maps.android.compose.rememberMarkerState
import com.example.going.R
import com.example.going.model.EventData
import com.example.going.util.MapScreen
import com.example.going.view.MapScreen.util.CustomMapMarkerIcon
import com.example.going.view.MapScreen.util.EventModalBottomSheet
import com.example.going.view.common.SearchBarUI
import com.example.going.viewmodel.EventDetailsViewModel
import com.example.going.viewmodel.MapEvent
import com.google.android.gms.maps.model.MapStyleOptions
@OptIn(ExperimentalSharedTransitionApi::class, ExperimentalMaterial3Api::class)
@Composable
fun MapScreen(
    navController: NavController,
    mapViewModel: MapViewModel = viewModel(),
    eventDetailsViewModel: EventDetailsViewModel = viewModel(),
    sharedTransitionScope: SharedTransitionScope,
    animatedVisibilityScope: AnimatedVisibilityScope
) {
    val cameraPositionState by mapViewModel.cameraPositionState.collectAsStateWithLifecycle()
    val savedStateHandle = navController.currentBackStackEntry?.savedStateHandle
    val selectedLat = savedStateHandle?.get<Double>("selected_event_lat")
    val selectedLng = savedStateHandle?.get<Double>("selected_event_lng")

    val events by mapViewModel.events.collectAsState()
    val sheetState = rememberModalBottomSheetState()
    val userLocation by mapViewModel.userLocation.collectAsState()
    val isFirstLoad by mapViewModel.isFirstLoad.collectAsState()
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

    LaunchedEffect(userLocation) {
        userLocation?.let {
            if(isFirstLoad) {
                mapViewModel.moveToLocation(it.latitude, it.longitude)
                mapViewModel.setFirstLoad(false)
            }

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

    LaunchedEffect(Unit) {
        mapViewModel.mapEvents.collect { event ->
            when (event) {
                is MapEvent.AnimateToLocation -> {
                    cameraPositionState.animate(
                        CameraUpdateFactory.newLatLngZoom(event.latLng, 15f),
                        1000
                    )
                }
            }
        }
    }

    LaunchedEffect(selectedLat, selectedLng) {
        if(selectedLat != null && selectedLng != null) {
            mapViewModel.moveToLocation(selectedLat, selectedLng)
            // Clear saved state data
            savedStateHandle.remove<Double>("selected_event_lat")
            savedStateHandle.remove<Double>("selected_event_lng")
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {
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
                EventModalBottomSheet(
                    selectedEvent,
                    onButtonClick={
                        eventDetailsViewModel.setEventId(selectedEvent!!.id)
                        navController.navigate(MapScreen.EventDetails.route)
                    }
                )
            }
        }

        SearchBarUI(
            searchQuery = "",
            onQueryChanged = {},
            onSearchClicked = {
                navController.navigate(MapScreen.Search.route)
            },
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.TopCenter)
                .padding(16.dp),
            sharedTransitionScope,
            animatedVisibilityScope,
            enabled = false
        )
    }
}