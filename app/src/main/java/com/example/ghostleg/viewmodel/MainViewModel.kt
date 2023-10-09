package com.example.ghostleg.viewmodel

import androidx.lifecycle.ViewModel
import com.example.ghostleg.model.Line
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

class MainViewModel : ViewModel() {

    private val _playerLabelsFlow = MutableStateFlow<List<String>>(emptyList())
    val playerLabelsFlow get() = _playerLabelsFlow.asStateFlow()

    private val _gameResultLabelsFlow = MutableStateFlow<List<String>>(emptyList())
    val gameResultLabelsFlow get() = _gameResultLabelsFlow.asStateFlow()

    private val _verticalLinesFlow = MutableStateFlow<List<Line>>(emptyList())
    val verticalLinesFlow get() = _verticalLinesFlow.asStateFlow()

    private val _horizontalLinesFlow = MutableStateFlow<List<Line>>(emptyList())
    val horizontalLinesFlow get() = _horizontalLinesFlow.asStateFlow()

    private var _playerNumbers = 10

    fun initGame() {
        initGamePlayers()
        initGameResult()
    }

    fun initLadder(width: Float, height: Float) {
        initVerticalLines(width, height)
        initHorizontalLines()
    }

    fun resetGame() {
        initGameResult()
        initHorizontalLines()
    }

    private fun initGamePlayers() {
        (1 .. _playerNumbers).map { playerNumber ->
            "P$playerNumber"
        }.let { playerLabels ->
            _playerLabelsFlow.update { playerLabels }
        }
    }

    private fun initGameResult() {
        val resultIndex = (0 until _playerNumbers).random()
        (0 until _playerNumbers).map { index ->
            if (index == resultIndex) {
                "WIN"
            } else {
                "LOSE"
            }
        }.let { gameResults ->
            _gameResultLabelsFlow.update { gameResults }
        }
    }

    private fun initVerticalLines(width: Float, height: Float) {
        val sectionWidth = width / _playerNumbers
        var sectionStart = 0f
        var sectionEnd = sectionWidth
        (0 until _playerNumbers).map {
            val xScale = (sectionEnd + sectionStart) / 2
            sectionStart += sectionWidth
            sectionEnd += sectionWidth
            Line(startX = xScale, startY = 0f, endX = xScale, endY = height)
        }.let { verticalLines ->
            _verticalLinesFlow.update { verticalLines }
        }
    }

     private fun initHorizontalLines() {
        if (_verticalLinesFlow.value.isEmpty()) return
        val horizontalLinesMatrix = getHorizontalLineMatrix()
        (0 until _verticalLinesFlow.value.size - 1).map { index ->
            val currentLine = _verticalLinesFlow.value[index]
            val nextLine = _verticalLinesFlow.value[index + 1]
            initHorizontalLines(
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

    private fun initHorizontalLines(
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
        val horizontalLineMatrix = Array(_playerNumbers) { BooleanArray(HORIZONTAL_LINE_SECTIONS) }
        (0 until _playerNumbers).forEach { index ->
            val availableIndices = (0 until HORIZONTAL_LINE_SECTIONS).toMutableList()
            if (index > 0) {
                horizontalLineMatrix[index - 1].forEachIndexed { j, value ->
                    if (value) availableIndices.remove(j)
                }
            }
            val remainingTrueCount = when {
                availableIndices.size >= MAXIMUM_COUNT -> (MINIMUM_COUNT .. MAXIMUM_COUNT).random()
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
        private const val MAXIMUM_COUNT = 8
    }
}
