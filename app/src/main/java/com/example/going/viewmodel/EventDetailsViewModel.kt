package com.example.going.viewmodel

import android.content.Context
import android.util.Log.e
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.going.R
import com.example.going.model.EventData
import com.example.going.model.ProfileUserData
import com.google.android.gms.maps.model.LatLng
import com.google.firebase.Firebase
import com.google.firebase.Timestamp
import com.google.firebase.auth.auth
import com.google.firebase.firestore.FieldPath
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
    val eventId: StateFlow<String?> = _eventId

    private val _eventDetails = MutableStateFlow<EventData?>(null)
    val eventDetails: StateFlow<EventData?> = _eventDetails

    private val _interestedUsers = MutableStateFlow<List<ProfileUserData>>(emptyList())
    val interestedUsers: StateFlow<List<ProfileUserData>> = _interestedUsers

    private val _isUserInterested = MutableStateFlow(false)
    val isUserInterested: StateFlow<Boolean> = _isUserInterested

    private val _eventFetchState = MutableStateFlow<UIState>(UIState())
    val eventFetchState: StateFlow<UIState> = _eventFetchState

    private val _toggleInterestState = MutableStateFlow<UIState>(UIState())
    val toggleInterestState: StateFlow<UIState> = _toggleInterestState

    private val _checkInterestState = MutableStateFlow<UIState>(UIState())
    val checkInterestState: StateFlow<UIState> = _checkInterestState

    private val _userListFetchState = MutableStateFlow<UIState>(UIState())
    val userListFetchState: StateFlow<UIState> = _userListFetchState

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
                    _eventFetchState.value = UIState(isSuccess = "Event loaded")
                } else {
                    _eventFetchState.value = UIState(isError = "Event not found.")
                }
            } catch (e: Exception) {
                _eventFetchState.value = UIState(isError = "Failed to load event: ${e.message}")
            }
        }
    }

    private fun getUsersInterestedInEvent() {
        viewModelScope.launch {
            try {
                _userListFetchState.value = UIState(isLoading = true)
                val query = firestore.collection("eventInterests")
                    .whereEqualTo("eventId", eventId.value)
                    .whereNotEqualTo("userId", auth.currentUser?.uid)
                    .get()
                    .await()

                val interestedUsers = query.documents.mapNotNull {user ->
                    user.getString("userId")
                }

                // whereIn query for the interestedUsers array
                val usersQuery = firestore.collection("users")
                    .whereIn(FieldPath.documentId(), interestedUsers)
                    .whereEqualTo("isPubliclyInterested", true)
                    .get()
                    .await()

                _interestedUsers.value = usersQuery.mapNotNull { user ->
                    ProfileUserData(
                        uid = user.id,
                        firstname = user.getString("firstname"),
                        lastname = user.getString("lastname"),
                        username = user.getString("username"),
                        avatarUrl = user.getString("avatarUrl"),
                        isPubliclyInterested = user.getBoolean("isPubliclyInterested") ?: true
                    )
                }

                _userListFetchState.value = UIState(isSuccess = "Data fetch successful")

            } catch(e: Exception) {
                _interestedUsers.value = emptyList()
                _userListFetchState.value = UIState(isError = e.message)
            }
        }
    }

    fun checkUserInterest() {
        val userId = auth.currentUser?.uid ?: return
        viewModelScope.launch {
            _checkInterestState.value = UIState(isLoading = true)
            try {
                val query = firestore.collection("eventInterests")
                    .whereEqualTo("userId", userId)
                    .whereEqualTo("eventId", eventId.value)
                    .limit(1)
                    .get()
                    .await()
                // If the query is not empty, the user is interested
                _isUserInterested.value = !query.isEmpty

                _checkInterestState.value = UIState(isSuccess = "Data fetch successful")
            } catch (e: Exception) {
                _isUserInterested.value = false
                _checkInterestState.value = UIState(isError = e.message)
            }
        }
    }

    fun toggleInterest(context: Context) {
        val userId = auth.currentUser?.uid ?: return
        viewModelScope.launch {
            try {
                _toggleInterestState.value = UIState(isLoading=true)
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

                    val successMessage = context.getString(R.string.data_update_success)
                    _toggleInterestState.value = UIState(isSuccess = successMessage)
                } else {
                    // User IS interested, so REMOVE their interest
                    val docId = snapshot.documents.first().id
                    firestore.collection("eventInterests").document(docId).delete().await()
                    _isUserInterested.value = false

                    _toggleInterestState.value = UIState(isSuccess = "Operation successful")

                }
            } catch (e: Exception) {
                _toggleInterestState.value = UIState(isError = e.message)
            }
        }
    }


    fun setEventId(id: String) {
        _eventId.value = id
        _eventDetails.value = null
        getEventById()
        getUsersInterestedInEvent()
        checkUserInterest()
    }
}