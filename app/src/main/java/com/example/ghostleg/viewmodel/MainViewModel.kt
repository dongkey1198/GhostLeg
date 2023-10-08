package com.example.ghostleg.viewmodel

import androidx.lifecycle.ViewModel
import com.example.ghostleg.model.Line
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

class MainViewModel: ViewModel() {

    private val _verticalLinesFlow = MutableStateFlow<List<Line>>(emptyList())
    val verticalLinesFlow get() = _verticalLinesFlow.asStateFlow()

    fun generateVerticalLines(playerNumber: Int, width: Float, height: Float) {
        val sectionWidth = width / playerNumber
        var sectionStart = 0f
        var sectionEnd = sectionWidth
        (0 until playerNumber).map {
            val xScale = (sectionEnd + sectionStart) / 2
            sectionStart += sectionWidth
            sectionEnd += sectionWidth
            Line(startX = xScale, startY = 0f, endX = xScale, endY = height)
        }.let { lines ->
            _verticalLinesFlow.update { lines }
        }
    }
}
