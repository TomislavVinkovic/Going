package com.example.going.model

import com.google.android.gms.maps.model.LatLng
import com.google.firebase.Timestamp

data class EventData(
    val id: String,
    val name: String?,
    val category: String?,
    val locationName: String?,
    val position: LatLng, // Converted from GeoPoint
    val description: String?,
    val startTime: Timestamp?,
    val endTime: Timestamp?,
    val tags: List<String>?,
    val image: String?
)