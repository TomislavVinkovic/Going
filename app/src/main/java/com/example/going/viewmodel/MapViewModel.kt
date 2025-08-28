package com.example.going.viewmodel

import android.annotation.SuppressLint
import android.content.Context
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.going.model.EventData
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.firebase.Firebase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import com.google.firebase.firestore.ListenerRegistration
import com.google.firebase.firestore.firestore
import com.google.maps.android.compose.CameraPositionState
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch

sealed class MapEvent {
    data class AnimateToLocation(val latLng: LatLng) : MapEvent()
}

class MapViewModel : ViewModel() {

    private val firestore = Firebase.firestore
    private var eventsListener: ListenerRegistration? = null

    private val _isFirstLoad = MutableStateFlow<Boolean>(true)
    val isFirstLoad: StateFlow<Boolean> = _isFirstLoad
    fun setFirstLoad(value: Boolean) {
        _isFirstLoad.value = value
    }

    private val _mapEvents = MutableSharedFlow<MapEvent>()
    val mapEvents = _mapEvents.asSharedFlow()

    private val _events = MutableStateFlow<List<EventData>>(emptyList())
    val events: StateFlow<List<EventData>> = _events

    private val _userLocation = MutableStateFlow<LatLng?>(null)
    val userLocation: StateFlow<LatLng?> = _userLocation

    private val _cameraPositionState = MutableStateFlow(CameraPositionState())
    val cameraPositionState: StateFlow<CameraPositionState> = _cameraPositionState

    // Function to move camera based on search result
    fun moveToLocation(latitude: Double, longitude: Double) {
        viewModelScope.launch {
            _mapEvents.emit(MapEvent.AnimateToLocation(LatLng(latitude, longitude)))
        }
    }

    init {
        attachEventsListener()
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

    private fun attachEventsListener() {
        eventsListener?.remove()
        eventsListener = firestore.collection("events")
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    // TODO: Handle error
                    return@addSnapshotListener
                }

                if (snapshot != null) {
                    var eventList = snapshot.documents.mapNotNull { doc ->
                        val geoPoint = doc.getGeoPoint("location_coords")
                        geoPoint?.let {
                            EventData(
                                id = doc.id,
                                name = doc.getString("name"),
                                category = doc.getString("category"),
                                locationName = doc.getString("location_name"),
                                position = LatLng(it.latitude, it.longitude),
                                // --- ADDED MISSING FIELDS ---
                                description = doc.getString("description"),
                                startTime = doc.getTimestamp("start_time"),
                                endTime = doc.getTimestamp("end_time"),
                                tags = doc.get("tags") as? List<String>,
                                image = doc.getString("image")
                            )
                        }
                    }
                    eventList = eventList.filter { event ->
                        event.startTime!! >= com.google.firebase.Timestamp.now()
                    }

                    _events.value = eventList
                }
            }
    }

    override fun onCleared() {
        super.onCleared()
        // Detach the listener to prevent memory leaks
        eventsListener?.remove()
    }
}