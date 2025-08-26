package com.example.going.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.going.model.EventData
import com.google.firebase.Firebase
import com.google.firebase.functions.FirebaseFunctions
import com.google.firebase.functions.functions
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.launch

class SearchViewModel: ViewModel() {
    private val firebase = Firebase
    private val functions: FirebaseFunctions = firebase.functions("europe-west1")

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery

    private val _selectedCategory = MutableStateFlow<String?>(null)
    val selectedCategory: StateFlow<String?> = _selectedCategory

    private val _searchResults = MutableStateFlow<List<EventData>>(emptyList())
    val searchResults: StateFlow<List<EventData>> = _searchResults

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    init {
        viewModelScope.launch {
            combine(searchQuery, selectedCategory) { query, category ->
                Pair(query, category)
            }.debounce(300)
                .collect { (query, category) ->

                }
        }
    }

    fun onQueryChanged(query: String) {
        _searchQuery.value = query
    }
    fun onCategorySelected(category: String) {
        _selectedCategory.value = category
    }

    private fun searchEvents(
        query: String,
        category: String?
    ) {
        if (query.isBlank() && category == null) {
            _searchResults.value = emptyList()
            return
        }
        _isLoading.value = true
        val data = hashMapOf(
            "query" to query,
            "category" to category
        )

        functions
            .getHttpsCallable("searchEvents")
            .call(data)
            .addOnSuccessListener { result ->
                _searchResults.value = result.data as List<EventData>
                _isLoading.value = true
            }
            .addOnFailureListener {
                // TODO: Handle error
                _isLoading.value = false
            }
    }
}