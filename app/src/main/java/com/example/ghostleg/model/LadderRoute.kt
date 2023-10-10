package com.example.ghostleg.model

import android.graphics.Color
import kotlin.random.Random

data class LadderRoute(
    val pathScales: List<Pair<Float, Float>>,
    val lineColor: Int = getRandomColor(),
    val stroke: Float = 10f
) {

    companion object {

        private fun getRandomColor(): Int {
            val random = Random
            return Color.argb(
                255,
                random.nextInt(200) + 100,
                random.nextInt(200) + 100,
                random.nextInt(200) + 100
            )
        }
    }
}
