package com.example.going.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.going.model.EventData
import com.google.android.gms.maps.model.LatLng
import com.google.firebase.Firebase
import com.google.firebase.Timestamp
import com.google.firebase.auth.auth
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
    private val auth = Firebase.auth

    private val _eventId = MutableStateFlow<String?>(null)
    val eventId = MutableStateFlow<String?>(null)

    private val _eventDetails = MutableStateFlow<EventData?>(null)
    val eventDetails: StateFlow<EventData?> = _eventDetails

    private val _isUserInterested = MutableStateFlow(false)
    val isUserInterested: StateFlow<Boolean> = _isUserInterested

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

    private fun checkUserInterest(eventId: String) {
        val userId = auth.currentUser?.uid ?: return
        viewModelScope.launch {
            try {
                val query = firestore.collection("eventInterests")
                    .whereEqualTo("userId", userId)
                    .whereEqualTo("eventId", eventId)
                    .limit(1)
                    .get()
                    .await()
                // If the query is not empty, the user is interested
                _isUserInterested.value = !query.isEmpty
            } catch (e: Exception) {
                // Handle error
            }
        }
    }

    fun toggleInterest() {
        val userId = auth.currentUser?.uid ?: return
        viewModelScope.launch {
            try {
                val interestDocRef = firestore.collection("eventInterests")
                    .whereEqualTo("userId", userId)
                    .whereEqualTo("eventId", _eventId.value)
                    .limit(1)

                val snapshot = interestDocRef.get().await()

                if (snapshot.isEmpty) {
                    // User is NOT interested, so ADD their interest
                    val newInterest = hashMapOf(
                        "userId" to userId,
                        "eventId" to _eventId.value,
                        "timestamp" to Timestamp.now()
                    )
                    firestore.collection("eventInterests").add(newInterest).await()
                    _isUserInterested.value = true
                } else {
                    // User IS interested, so REMOVE their interest
                    val docId = snapshot.documents.first().id
                    firestore.collection("eventInterests").document(docId).delete().await()
                    _isUserInterested.value = false
                }
            } catch (e: Exception) {
                // TODO: Handle error, maybe show a snackbar
            }
        }
    }


    fun setEventId(id: String) {
        _eventId.value = id
        _eventDetails.value = null
        getEventById()
    }
}