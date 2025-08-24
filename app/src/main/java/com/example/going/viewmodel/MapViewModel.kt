package com.example.going.viewmodel

import android.annotation.SuppressLint
import android.content.Context
import androidx.lifecycle.ViewModel
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.model.LatLng
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

// A simple data class to represent an event's location
data class EventLocation(
    val id: String,
    val position: LatLng
)

class MapViewModel : ViewModel() {
    private val _eventLocations = MutableStateFlow<List<EventLocation>>(emptyList())
    val eventLocations: StateFlow<List<EventLocation>> = _eventLocations

    private val _userLocation = MutableStateFlow<LatLng?>(null)
    val userLocation: StateFlow<LatLng?> = _userLocation

    init {
        // Load fake event data
        loadFakeEvents()
    }

    @SuppressLint("MissingPermission")
    fun fetchUserLocation(context: Context) {
        val fusedLocationClient = LocationServices.getFusedLocationProviderClient(context)
        fusedLocationClient.lastLocation
            .addOnSuccessListener { location ->
                location?.let {
                    _userLocation.value = LatLng(it.latitude, it.longitude)
                }
            }
            .addOnFailureListener {
                // TODO: Handle failure
            }
    }

    private fun loadFakeEvents() {
        // Fake events around Beli Manastir, Croatia
        _eventLocations.value = listOf(
            EventLocation("event1", LatLng(45.7755, 18.6010)), // Center
            EventLocation("event2", LatLng(45.7780, 18.6055)), // North-East
            EventLocation("event3", LatLng(45.7720, 18.5980))  // South-West
        )
    }
}