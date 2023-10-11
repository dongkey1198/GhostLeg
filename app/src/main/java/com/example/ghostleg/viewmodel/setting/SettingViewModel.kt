package com.example.ghostleg.viewmodel.setting

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.ghostleg.data.repository.LadderGameRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class SettingViewModel(
    private val ladderGameRepository: LadderGameRepository
): ViewModel() {

    private val _playerCountFlow = MutableStateFlow<Int>(0)
    val playerCountFlow = _playerCountFlow.asStateFlow()

    private var _previousPlayerCount = 0

    init {
        getPlayerCount()
    }

    fun decrementButtonClicked() {
        if (_playerCountFlow.value > 2) {
            _playerCountFlow.update { _playerCountFlow.value - 1 }
        } else {

        }
    }

    fun incrementButtonClicked() {
        if (_playerCountFlow.value < 10) {
            _playerCountFlow.update { _playerCountFlow.value + 1 }
        } else {

        }
    }

    private fun getPlayerCount() {
        viewModelScope.launch(Dispatchers.Default) {
            val playerCount = ladderGameRepository.getPlayerCount()
            _previousPlayerCount = playerCount
            _playerCountFlow.update { playerCount }
        }
    }
}