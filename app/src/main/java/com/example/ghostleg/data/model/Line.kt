package com.example.ghostleg.data.model

import android.graphics.Color


data class Line(
    val startX: Float,
    val startY: Float,
    val endX: Float,
    val endY: Float,
    val color: Int = Color.GRAY,
    val stroke: Float = 10f
)
