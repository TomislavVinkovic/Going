package com.example.going.viewmodel

import android.annotation.SuppressLint
import android.content.Context
import android.util.Log
import androidx.lifecycle.ViewModel
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.model.LatLng
import com.google.firebase.Firebase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import com.google.firebase.Timestamp
import com.google.firebase.firestore.ListenerRegistration
import com.google.firebase.firestore.firestore

data class EventData(
    val id: String,
    val name: String?,
    val category: String?,
    val locationName: String?,
    val position: LatLng, // Converted from GeoPoint
    val description: String?,
    val startTime: Timestamp?,
    val endTime: Timestamp?,
    val tags: List<String>?
)

class MapViewModel : ViewModel() {

    private val firestore = Firebase.firestore
    private var eventsListener: ListenerRegistration? = null

    private val _events = MutableStateFlow<List<EventData>>(emptyList())
    val events: StateFlow<List<EventData>> = _events

    private val _userLocation = MutableStateFlow<LatLng?>(null)
    val userLocation: StateFlow<LatLng?> = _userLocation

    init {
        // Load fake event data
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
                    Log.d("EVENTS", "Successfully fetched ${snapshot.size()} documents.")
                    val eventList = snapshot.documents.mapNotNull { doc ->
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
                                tags = doc.get("tags") as? List<String>
                            )
                        }
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