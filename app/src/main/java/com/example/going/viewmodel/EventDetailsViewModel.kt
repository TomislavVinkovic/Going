package com.example.going.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.going.model.EventData
import com.google.android.gms.maps.model.LatLng
import com.google.firebase.Firebase
import com.google.firebase.firestore.firestore
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

data class UIState(
    val isLoading: Boolean = false,
    val isError: String? = null,
    val isSuccess: String? = null
)

class EventDetailsViewModel: ViewModel() {

    private val firestore = Firebase.firestore

    private val _eventId = MutableStateFlow<String?>(null)
    val eventId = MutableStateFlow<String?>(null)

    private val _eventDetails = MutableStateFlow<EventData?>(null)
    val eventDetails: StateFlow<EventData?> = _eventDetails

    private val _uiState = MutableStateFlow<UIState>(UIState())
    val uiState: StateFlow<UIState> = _uiState

    private fun getEventById() {
        viewModelScope.launch {
            try {
                val document = firestore
                    .collection("events")
                    .document(_eventId.value!!)
                    .get()
                    .await()

                if (document.exists()) {
                    // Map the document to your EventData class
                    val geoPoint = document.getGeoPoint("location_coords")
                    _eventDetails.value = EventData(
                        id = document.id,
                        name = document.getString("name"),
                        category = document.getString("category"),
                        locationName = document.getString("location_name"),
                        position = LatLng(geoPoint?.latitude ?: 0.0, geoPoint?.longitude ?: 0.0),
                        description = document.getString("description"),
                        startTime = document.getTimestamp("start_time"),
                        endTime = document.getTimestamp("end_time"),
                        tags = document.get("tags") as? List<String>,
                        image = document.getString("image")
                    )
                    _uiState.value = UIState(isSuccess = "Event loaded")
                } else {
                    _uiState.value = UIState(isError = "Event not found.")
                }
            } catch (e: Exception) {
                _uiState.value = UIState(isError = "Failed to load event: ${e.message}")
            }
        }
    }

    fun setEventId(id: String) {
        _eventId.value = id
        _eventDetails.value = null
        getEventById()
    }
}