package com.example.ghostleg.model

data class Line(
    val startX: Float,
    val startY: Float,
    val endX: Float,
    val endY: Float,
    val color: Int = android.graphics.Color.BLACK,
    val stroke: Float = 10f
)

