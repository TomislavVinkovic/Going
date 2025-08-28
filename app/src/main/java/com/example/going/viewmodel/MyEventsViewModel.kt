package com.example.going.viewmodel

import android.util.Log.e
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.going.model.EventData
import com.example.going.model.util.DataFetchState
import com.google.android.gms.maps.model.LatLng
import com.google.firebase.Firebase
import com.google.firebase.auth.auth
import com.google.firebase.firestore.FieldPath
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.firestore
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import java.util.Locale

class MyEventsViewModel: ViewModel() {
    private val firebase = Firebase
    private val firestore = Firebase.firestore
    private val auth = Firebase.auth

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery

    private val _selectedCategory = MutableStateFlow<String?>(null)
    val selectedCategory: StateFlow<String?> = _selectedCategory

    private val _searchResults = MutableStateFlow<List<EventData>>(emptyList())
    val searchResults: StateFlow<List<EventData>> = _searchResults

    private val _searchState = MutableStateFlow<DataFetchState>(DataFetchState())
    var searchState: StateFlow<DataFetchState> = _searchState

    private val _userInterestsState = MutableStateFlow<DataFetchState>(DataFetchState())
    var userInterestsState: StateFlow<DataFetchState> = _userInterestsState

    fun onQueryChanged(query: String) {
        _searchQuery.value = query
    }
    fun onCategorySelected(category: String) {
        if(_selectedCategory.value == category) {
            _selectedCategory.value = null
        }
        else {
            _selectedCategory.value = category
        }
    }

    init {
        viewModelScope.launch {
            combine(searchQuery, selectedCategory) { query, category ->
                Pair(query, category)
            }.debounce(300)
                .collect { (query, category) ->
                    searchEvents(query, category)
                }
        }
    }

    private fun searchEvents(
        query: String,
        category: String?
    ) {
        try {
            _searchState.value = DataFetchState(isLoading = true)

            viewModelScope.launch {
                val userInterests = getUserInterests()
                if(userInterests.isEmpty()) {
                    _searchResults.value = emptyList()
                    _searchState.value = DataFetchState(isSuccess = "Data fetched successfully")
                    return@launch
                }
                var eventsQuery: Query = firestore.collection("events")

                if (category != null) {
                    eventsQuery = eventsQuery.whereEqualTo("category", category)
                }

                val snapshot = eventsQuery
                    .whereIn(FieldPath.documentId(), userInterests)
                    .get()
                    .await()

                val eventList = snapshot.documents.mapNotNull { doc ->
                    val geoPoint = doc.getGeoPoint("location_coords")
                    geoPoint?.let {
                        EventData(
                            id = doc.id,
                            name = doc.getString("name"),
                            category = doc.getString("category"),
                            locationName = doc.getString("location_name"),
                            position = LatLng(it.latitude, it.longitude),
                            description = doc.getString("description"),
                            startTime = doc.getTimestamp("start_time"),
                            endTime = doc.getTimestamp("end_time"),
                            tags = doc.get("tags") as? List<String>,
                            image = doc.getString("image")
                        )
                    }
                }

                var filteredResults = if(query.isNotBlank()) {
                    val lowerCaseQuery = query.lowercase(Locale.getDefault())
                    eventList.filter { event ->
                        val nameMatch = event.name?.lowercase(Locale.getDefault())
                            ?.contains(lowerCaseQuery) == true
                        val locationMatch = event.locationName?.lowercase(Locale.getDefault())
                            ?.contains(lowerCaseQuery) == true
                        // Return true if either the name or location matches
                        nameMatch || locationMatch
                    }
                }
                else eventList

                filteredResults = filteredResults.filter { event ->
                    event.startTime!! >= com.google.firebase.Timestamp.now()
                }

                _searchResults.value = filteredResults
                _searchState.value = DataFetchState(isSuccess = "Data fetched successfully")
            }
        } catch(e: Exception) {
            _searchResults.value = emptyList<EventData>()
            _searchState.value = DataFetchState(isError = e.message)
        }
    }

    private suspend fun getUserInterests(): List<String> {
        return try {
            _userInterestsState.value = DataFetchState(isLoading = true)
            val query = firestore.collection("eventInterests")
                .whereEqualTo("userId", auth.currentUser!!.uid)
                .get()
                .await()

            val interests = query.documents.mapNotNull { doc ->
                doc.getString("eventId")
            }
            _userInterestsState.value = DataFetchState(isSuccess = "Data fetched successfully")
            interests
        } catch(e: Exception) {
            _userInterestsState.value = DataFetchState(isError = e.message)
            emptyList()
        }
    }

    fun refreshEvents() {
        searchEvents(searchQuery.value, selectedCategory.value)
    }
}