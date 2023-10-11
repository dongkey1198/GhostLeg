package com.example.ghostleg.viewmodel.main

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.ghostleg.data.model.Line
import com.example.ghostleg.data.model.LadderRoute
import com.example.ghostleg.data.repository.LadderGameRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class MainViewModel(
    private val ladderGameRepository: LadderGameRepository
) : ViewModel() {

    private val _playerLabelsFlow = MutableStateFlow<List<String>>(emptyList())
    val playerLabelsFlow get() = _playerLabelsFlow.asStateFlow()

    private val _gameResultLabelsFlow = MutableStateFlow<List<String>>(emptyList())
    val gameResultLabelsFlow get() = _gameResultLabelsFlow.asStateFlow()

    private val _verticalLinesFlow = MutableStateFlow<List<Line>>(emptyList())
    val verticalLinesFlow get() = _verticalLinesFlow.asStateFlow()

    private val _horizontalLinesFlow = MutableStateFlow<List<Line>>(emptyList())
    val horizontalLinesFlow get() = _horizontalLinesFlow.asStateFlow()

    private val _ladderRoutesFlow = MutableStateFlow<List<LadderRoute>>(emptyList())
    val ladderRoutesFlow get() = _ladderRoutesFlow.asSharedFlow()

    private val _startButtonStateFlow = MutableStateFlow<Boolean>(true)
    val startButtonStateFlow get() = _startButtonStateFlow.asStateFlow()

    private val _resultBlindStateFlow = MutableStateFlow<Boolean>(true)
    val resultBlindStateFlow get() = _resultBlindStateFlow.asStateFlow()

    private val _moveToSettingPageFlow = MutableSharedFlow<Unit>()
    val moveToSettingPageFlow get() = _moveToSettingPageFlow.asSharedFlow()

    private val _gameStateMessageFlow = MutableSharedFlow<Unit>()
    val gameStateMessageFlow get() = _gameStateMessageFlow.asSharedFlow()

    private val _ladderViewSizeFlow = MutableStateFlow<Pair<Float, Float>>(Pair(0f, 0f))
    private val _ladderMatrix = mutableListOf<List<Pair<Float, Float>>>()
    private val _randomLineMatrix = mutableListOf<List<Int>>()
    private var _isPlaying = false

    init {
        viewModelScope.launch(Dispatchers.Default) {
            combine(ladderGameRepository.playerCount, _ladderViewSizeFlow) { playerCount, ladderViewSize ->
                Pair(playerCount, ladderViewSize)
            }.collect { (playerCount, ladderViewSize) ->
                initGame(playerCount, ladderViewSize)
            }
        }
    }

    fun ladderViewSizeDetected(width: Float, height: Float) {
        _ladderViewSizeFlow.update { Pair(width, height) }
    }

    fun startButtonClicked() {
        setIsPlaying(true)
        setStartButtonState(false)
        generateLadderRoutes()
    }

    fun gameEnded() {
        setIsPlaying(false)
        setResultBlindState(false)
    }

    fun resetButtonClicked() {
        if (_isPlaying) {
            setGameStateMessageFlow()
        } else {
            resetGame()
        }
    }

    fun settingButtonClicked() {
        if (_isPlaying) {
            setGameStateMessageFlow()
        } else {
            viewModelScope.launch(Dispatchers.Default) {
                _moveToSettingPageFlow.emit(Unit)
            }
        }
    }

    private fun initGame(playerCount: Int, ladderViewSize: Pair<Float, Float>) {
        initGamePlayers(playerCount)
        initGameResult()
        initLadderMatrix(ladderViewSize.first, ladderViewSize.second)
        initRandomLineMatrix()
        initVerticalLines()
        initHorizontalLines()
        setLadderRoutes(emptyList())
        setStartButtonState(true)
        setResultBlindState(true)
    }

    private fun resetGame() {
        initGameResult()
        initRandomLineMatrix()
        initHorizontalLines()
        setLadderRoutes(emptyList())
        setStartButtonState(true)
        setResultBlindState(true)
    }

    private fun initGamePlayers(playerCount: Int) {
        (1..playerCount)
            .map { index -> "$LABEL_PLAYER$index" }
            .let { playerLabels -> _playerLabelsFlow.update { playerLabels } }
    }

    private fun initGameResult() {
        val resultIndex = (0 until _playerLabelsFlow.value.size).random()
        (0 until _playerLabelsFlow.value.size).map { index ->
            if (index == resultIndex) LABEL_WIN else LABEL_LOSE
        }.let { gameResultLabels ->
            _gameResultLabelsFlow.update { gameResultLabels }
        }
    }

    private fun initLadderMatrix(width: Float, height: Float) {
        val lastPosition = HORIZONTAL_LINE_COUNT + 2
        val sectionWidth = width / _playerLabelsFlow.value.size
        val sectionHeight = height / HORIZONTAL_LINE_COUNT
        (0 until lastPosition).map { y ->
            (0 until _playerLabelsFlow.value.size).map { x ->
                val xScale = (sectionWidth * (x + 1) + sectionWidth * x) / 2
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
        val horizontalLineMatrix =
            Array(HORIZONTAL_LINE_COUNT + 2) { IntArray(_playerLabelsFlow.value.size) }
        (0 until _playerLabelsFlow.value.size - 1).forEach { x ->
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
        (0 until _playerLabelsFlow.value.size - 1).forEach { index ->
            val availableIndices = (1..HORIZONTAL_LINE_COUNT).toMutableList()
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
        (0 until _playerLabelsFlow.value.size).map { index ->
            val startMatrix = _ladderMatrix.first()
            val endMatrix = _ladderMatrix.last()
            Line(
                startMatrix[index].first,
                startMatrix[index].second,
                endMatrix[index].first,
                endMatrix[index].second
            )
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
                    val line = Line(
                        startMatrix.first,
                        startMatrix.second,
                        endMatrix.first,
                        endMatrix.second
                    )
                    horizontalLines.add(line)
                }
            }
        }
        _horizontalLinesFlow.update { horizontalLines }
    }

    private fun generateLadderRoutes() {
        (0 until _playerLabelsFlow.value.size).map { index ->
            var y = 0
            var x = index
            val paths = mutableListOf<Pair<Float, Float>>()
            do {
                when (_randomLineMatrix[y][x]) {
                    // 오른쪽 이동 + 한칸 아래 이동
                    1 -> {
                        paths.add(getPath(y, x++))
                        paths.add(getPath(y++, x))
                    }
                    // 왼쪽 이동 + 한칸 아래 이동
                    2 -> {
                        paths.add(getPath(y, x--))
                        paths.add(getPath(y++, x))
                    }
                    // 한칸 아래 이동
                    else -> {
                        paths.add(getPath(y++, x))
                    }
                }
            } while (y < HORIZONTAL_LINE_COUNT + 2)
            paths
        }.map {
            LadderRoute(pathScales = it)
        }.let {
            setLadderRoutes(it)
        }
    }

    private fun getPath(i: Int, j: Int): Pair<Float, Float> {
        val scales = _ladderMatrix[i][j]
        return Pair(scales.first, scales.second)
    }

    private fun setLadderRoutes(ladderRoutes: List<LadderRoute>) {
        _ladderRoutesFlow.update { ladderRoutes }
    }

    private fun setGameStateMessageFlow() {
        viewModelScope.launch(Dispatchers.Default) {
            _gameStateMessageFlow.emit(Unit)
        }
    }

    private fun setStartButtonState(isShow: Boolean) {
        _startButtonStateFlow.update { isShow }
    }

    private fun setResultBlindState(isShow: Boolean = false) {
        _resultBlindStateFlow.update { isShow }
    }

    private fun setIsPlaying(isPlaying: Boolean = false) {
        _isPlaying = isPlaying
    }

    companion object {
        private const val HORIZONTAL_LINE_COUNT = 10
        private const val MINIMUM_COUNT = 2
        private const val MAXIMUM_COUNT = 8
        private const val LABEL_PLAYER = "P"
        private const val LABEL_WIN = "WIN"
        private const val LABEL_LOSE = "LOSE"
    }
}
