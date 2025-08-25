package com.example.going.view.MapScreen.util

import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Paint
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.core.graphics.createBitmap
import com.google.android.gms.maps.model.BitmapDescriptor
import com.google.android.gms.maps.model.BitmapDescriptorFactory

fun CustomMapMarkerIcon(category: String, iconColor: Color): BitmapDescriptor {

    val iconMap = hashMapOf(
        "party" to "🎉",
        "concert" to "🎸",
        "sports" to "🏀",
        "rave" to "🎤",
        "food" to "🍔",
        "conference" to "💼",
        "other" to "📍"
    )

    val diameter = 96
    // 2. Create the main bitmap for the blip
    val bitmap = Bitmap.createBitmap(diameter, diameter, Bitmap.Config.ARGB_8888)
    val canvas = Canvas(bitmap)

    // 3. Draw the purple background circle
    val backgroundPaint = Paint().apply {
        this.color = iconColor.toArgb()
        isAntiAlias = true
    }
    canvas.drawCircle(diameter / 2f, diameter / 2f, diameter / 2f, backgroundPaint)

    // 4. Set up the paint for drawing the emoji text
    val textPaint = Paint().apply {
        this.color = Color.White.toArgb()
        this.textSize = diameter * 0.6f // Make emoji large
        this.textAlign = Paint.Align.CENTER
        this.isAntiAlias = true
    }

    // 5. Calculate the correct Y position to center the emoji vertically
    val textY = (diameter / 2f) - ((textPaint.descent() + textPaint.ascent()) / 2)

    // 6. Draw the emoji onto the canvas
    canvas.drawText(iconMap[category] ?: "", diameter / 2f, textY, textPaint)

    return BitmapDescriptorFactory.fromBitmap(bitmap)
}