package com.example.ghostleg.viewmodel

import androidx.lifecycle.ViewModel
import com.example.ghostleg.model.Line
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

class MainViewModel : ViewModel() {

    private val _verticalLinesFlow = MutableStateFlow<List<Line>>(emptyList())
    val verticalLinesFlow get() = _verticalLinesFlow.asStateFlow()

    private val _horizontalLinesFlow = MutableStateFlow<List<Line>>(emptyList())
    val horizontalLinesFlow get() = _horizontalLinesFlow.asStateFlow()

    private var playerNumber = 6

    fun generateVerticalLines(width: Float, height: Float) {
        val sectionWidth = width / playerNumber
        var sectionStart = 0f
        var sectionEnd = sectionWidth
        (0 until playerNumber).map {
            val xScale = (sectionEnd + sectionStart) / 2
            sectionStart += sectionWidth
            sectionEnd += sectionWidth
            Line(startX = xScale, startY = 0f, endX = xScale, endY = height)
        }.let { verticalLines ->
            _verticalLinesFlow.update { verticalLines }
        }
    }

    fun generateHorizontalLines() {
        if (_verticalLinesFlow.value.isEmpty()) return
        val horizontalLinesMatrix = getHorizontalLineMatrix()
        (0 until _verticalLinesFlow.value.size - 1).map { index ->
            val currentLine = _verticalLinesFlow.value[index]
            val nextLine = _verticalLinesFlow.value[index + 1]
            generateHorizontalLines(
                currentLine.startX,
                nextLine.startX,
                currentLine.startY,
                currentLine.endY,
                horizontalLinesMatrix[index]
            )
        }.flatten().let { horizontalLines ->
            _horizontalLinesFlow.update { horizontalLines }
        }
    }

    private fun generateHorizontalLines(
        startX: Float,
        endX: Float,
        minY: Float,
        maxY: Float,
        horizontalLinesMatrix: BooleanArray
    ): List<Line> {
        val lines = mutableListOf<Line>()
        val sectionWidth = maxY / HORIZONTAL_LINE_SECTIONS
        horizontalLinesMatrix.forEachIndexed { index, value ->
            if (value) {
                val yScale = when (index) {
                    0 -> (sectionWidth + minY) / 2
                    else -> (sectionWidth * (index + 1) + (sectionWidth * index)) / 2
                }
                val line = Line(startX = startX, startY = yScale, endX = endX, endY = yScale)
                lines.add(line)
            }
        }
        return lines
    }

    private fun getHorizontalLineMatrix(): Array<BooleanArray> {
        val horizontalLineMatrix = Array(playerNumber) { BooleanArray(HORIZONTAL_LINE_SECTIONS) }
        (0 until playerNumber).forEach { index ->
            val availableIndices = (0 until HORIZONTAL_LINE_SECTIONS).toMutableList()
            if (index > 0) {
                horizontalLineMatrix[index - 1].forEachIndexed { j, value ->
                    if (value) availableIndices.remove(j)
                }
            }
            val remainingTrueCount = when {
                availableIndices.size > playerNumber -> (MINIMUM_COUNT..playerNumber).random()
                else -> (MINIMUM_COUNT..availableIndices.size).random()
            }
            availableIndices.shuffled()
                .take(remainingTrueCount)
                .forEach { randomIndex ->
                    horizontalLineMatrix[index][randomIndex] = true
                }
        }
        return horizontalLineMatrix
    }

    companion object {
        private const val HORIZONTAL_LINE_SECTIONS = 10
        private const val MINIMUM_COUNT = 2
    }
}
