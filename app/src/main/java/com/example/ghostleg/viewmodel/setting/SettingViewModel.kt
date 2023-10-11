package com.example.ghostleg.viewmodel.setting

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.ghostleg.data.repository.LadderGameRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class SettingViewModel(
    private val ladderGameRepository: LadderGameRepository
): ViewModel() {

    private val _playerCountFlow = MutableStateFlow<Int>(0)
    val playerCountFlow = _playerCountFlow.asStateFlow()

    private val _playerLimitMessageFlow = MutableSharedFlow<Unit>()
    val playerLimitMessageFlow get() = _playerLimitMessageFlow.asSharedFlow()

    private val _pageCloseFlow = MutableStateFlow<Boolean>(false)
    val pageCloseFlow get() = _pageCloseFlow.asStateFlow()

    private var _previousPlayerCount = 0

    init {
        getPlayerCount()
    }

    fun decrementButtonClicked() {
        if (_playerCountFlow.value > 2) {
            _playerCountFlow.update { _playerCountFlow.value - 1 }
        } else {
            updatePlayerLimitMessageFlow()
        }
    }

    fun incrementButtonClicked() {
        if (_playerCountFlow.value < 10) {
            _playerCountFlow.update { _playerCountFlow.value + 1 }
        } else {
            updatePlayerLimitMessageFlow()
        }
    }

    fun closeButtonClicked() {
        updatePageCloseFlow()
    }

    fun saveButtonClicked() {
        if (_playerCountFlow.value == _previousPlayerCount) {
            updatePageCloseFlow()
        } else {
            setPlayerCount(_playerCountFlow.value)
            updatePageCloseFlow()
        }
    }

    private fun getPlayerCount() {
        viewModelScope.launch(Dispatchers.Default) {
            val playerCount = ladderGameRepository.getPlayerCount()
            _previousPlayerCount = playerCount
            _playerCountFlow.update { playerCount }
        }
    }

    private fun setPlayerCount(playerCount: Int) {
        viewModelScope.launch(Dispatchers.Default) {
            ladderGameRepository.setPlayerCount(playerCount)
        }
    }

    private fun updatePlayerLimitMessageFlow() {
        viewModelScope.launch(Dispatchers.Default) {
            _playerLimitMessageFlow.emit(Unit)
        }
    }

    private fun updatePageCloseFlow() {
        _pageCloseFlow.update { true }
    }
}