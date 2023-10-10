package com.example.ghostleg.viewmodel

import androidx.lifecycle.ViewModel
import com.example.ghostleg.model.Line
import com.example.ghostleg.model.LadderRoute
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

    private val _ladderRoutesFlow = MutableStateFlow<List<LadderRoute>>(emptyList())
    val ladderRoutesFlow get() = _ladderRoutesFlow.asStateFlow()

    private val _startButtonStateFlow = MutableStateFlow<Boolean>(true)
    val startButtonStateFlow get() = _startButtonStateFlow.asStateFlow()

    private val _resultBlindStateFlow = MutableStateFlow<Boolean>(true)
    val resultBlindStateFlow get() = _resultBlindStateFlow.asStateFlow()

    private val _ladderMatrix = mutableListOf<List<Pair<Float, Float>>>()
    private val _randomLineMatrix = mutableListOf<List<Int>>()
    private var _playerNumbers = 6

    fun initGame() {
        initGamePlayers()
        initGameResult()
    }

    fun initLadder(width: Float, height: Float) {
        initLadderMatrix(width, height)
        initRandomLineMatrix()
        initVerticalLines()
        initHorizontalLines()
    }

    fun resetGame() {
        initGameResult()
        initRandomLineMatrix()
        initHorizontalLines()
        updateLadderRoutes(emptyList())
        updateStartButtonState()
        updateResultBlindState()
    }

    fun startGame() {
        findRoutes()
    }

    fun updateStartButtonState() {
        _startButtonStateFlow.update { !_startButtonStateFlow.value }
    }

    fun updateResultBlindState() {
        _resultBlindStateFlow.update { !_resultBlindStateFlow.value }
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

    private fun initLadderMatrix(width: Float, height: Float) {
        val lastPosition = HORIZONTAL_LINE_SECTIONS + 2
        val sectionWidth = width / _playerNumbers
        val sectionHeight = height / HORIZONTAL_LINE_SECTIONS
        (0 until lastPosition).map { y ->
            (0 until _playerNumbers).map { x ->
                val xScale =  (sectionWidth * (x + 1) + sectionWidth * x) / 2
                val yScale = when {
                    // 출발 지점
                    y == 0 -> 0f
                    // 중간 지점
                    y < lastPosition - 1 -> (sectionHeight * y + sectionHeight * (y - 1)) / 2
                    // 도착 지점
                    else -> height
                }
                Pair(xScale, yScale)
            }
        }.let { ladderMatrix ->
            _ladderMatrix.clear()
            _ladderMatrix.addAll(ladderMatrix)
        }
    }

    private fun initRandomLineMatrix() {
        val randomIndices = getRandomIndices()
        val horizontalLineMatrix = Array(HORIZONTAL_LINE_SECTIONS + 2) { IntArray(_playerNumbers) }
        (0 until _playerNumbers - 1).forEach { x ->
            randomIndices[x].forEach { y ->
                horizontalLineMatrix[y][x] = 1
                horizontalLineMatrix[y][x + 1] = 2
            }
        }
        horizontalLineMatrix.map { it.toList() }.let { randomLineMatrix ->
            _randomLineMatrix.clear()
            _randomLineMatrix.addAll(randomLineMatrix)
        }
    }

    private fun getRandomIndices(): List<List<Int>> {
        val randomIndices = mutableListOf<List<Int>>()
        (0 until _playerNumbers - 1).forEach { index ->
            val availableIndices = (1 .. HORIZONTAL_LINE_SECTIONS).toMutableList()
            if (index > 0) {
                randomIndices[index - 1].forEach { value ->
                    if (availableIndices.contains(value)) availableIndices.remove(value)
                }
            }
            val remainingTrueCount = when {
                availableIndices.size >= MAXIMUM_COUNT -> (MINIMUM_COUNT..MAXIMUM_COUNT).random()
                else -> (MINIMUM_COUNT..availableIndices.size).random()
            }
            val indices = availableIndices.shuffled().take(remainingTrueCount)
            randomIndices.add(indices)
        }
        return randomIndices
    }

    private fun initVerticalLines() {
        (0 until _playerNumbers).map { index ->
            val startMatrix = _ladderMatrix.first()
            val endMatrix = _ladderMatrix.last()
            Line(startMatrix[index].first, startMatrix[index].second, endMatrix[index].first, endMatrix[index].second)
        }.let { verticalLines ->
            _verticalLinesFlow.update { verticalLines }
        }
    }

    private fun initHorizontalLines() {
        val horizontalLines = mutableListOf<Line>()
        (1 until _randomLineMatrix.size - 1).forEach { y ->
            (0 until _randomLineMatrix[y].size - 1).forEach { x ->
                if (_randomLineMatrix[y][x] == 1) {
                    val startMatrix = _ladderMatrix[y][x]
                    val endMatrix = _ladderMatrix[y][x + 1]
                    val line = Line(startMatrix.first, startMatrix.second, endMatrix.first, endMatrix.second)
                    horizontalLines.add(line)
                }
            }
        }
        _horizontalLinesFlow.update { horizontalLines }
    }

    private fun findRoutes() {
        (0 until _playerNumbers).map { index ->
            var y = 0
            var x = index
            val pathScales = mutableListOf<Pair<Float, Float>>()
            pathScales.add(getRoute(y, x))
            do {
                when(_randomLineMatrix[y][x]) {
                    // 오른쪽 이동 + 한칸 아래 이동
                    1 -> {
                        pathScales.add(getRoute(y, ++x))
                        pathScales.add(getRoute(++y, x))
                    }
                    // 왼쪽 이동 + 한칸 아래 이동
                    2-> {
                        pathScales.add(getRoute(y, --x))
                        pathScales.add(getRoute(++y, x))
                    }
                    // 한칸 아래 이동
                    else -> {
                        pathScales.add(getRoute(++y, x))
                    }
                }
            } while (y < HORIZONTAL_LINE_SECTIONS + 1)
            pathScales
        }.map { pathScale ->
            LadderRoute(pathScales = pathScale)
        }.let { ladderRoutes ->
            updateLadderRoutes(ladderRoutes)
        }
    }

    private fun getRoute(i: Int, j: Int): Pair<Float, Float> {
        val scales = _ladderMatrix[i][j]
        return Pair(scales.first, scales.second)
    }

    private fun updateLadderRoutes(ladderRoutes: List<LadderRoute>) {
        _ladderRoutesFlow.update { ladderRoutes }
    }

    companion object {
        private const val HORIZONTAL_LINE_SECTIONS = 10
        private const val MINIMUM_COUNT = 2
        private const val MAXIMUM_COUNT = 8
    }
}
