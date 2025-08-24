package com.example.going.view.MapScreen.util

import android.graphics.Canvas
import android.graphics.Paint
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.core.graphics.createBitmap
import com.google.android.gms.maps.model.BitmapDescriptor
import com.google.android.gms.maps.model.BitmapDescriptorFactory

fun CustomMapMarkerIcon(): BitmapDescriptor {
    val color = Color(0xFF800080) // Purple
    val descriptor = createBitmap(48, 48)
    val canvas = Canvas(descriptor)
    val paint = Paint().apply {
        this.color = color.toArgb()
        isAntiAlias = true
    }
    canvas.drawCircle(24f, 24f, 24f, paint)
    return BitmapDescriptorFactory.fromBitmap(descriptor)
}