package com.ssafy.jjongle.presentation.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ssafy.jjongle.domain.entity.BgmGroup
import com.ssafy.jjongle.domain.repository.BgmRepository
import com.ssafy.jjongle.domain.repository.SettingsRepository
import com.ssafy.jjongle.presentation.state.MapState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MapViewModel @Inject constructor(
    private val bgmRepository: BgmRepository,
    private val settingsRepository: SettingsRepository
) : ViewModel() {

    private val _mapState = MutableStateFlow(MapState())
    val mapState: StateFlow<MapState> = combine(
        _mapState,
        settingsRepository.getBgmEnabled()
    ) { state, bgmEnabled ->
        state.copy(isBgmOn = bgmEnabled)
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = MapState()
    )

    init {
        Log.d("MapViewModel", "init: viewmodel 초기화")
    }

    fun moveCharacterTo(x: Float, y: Float) {
        viewModelScope.launch {
            _mapState.value = _mapState.value.copy(
                characterX = x,
                characterY = y,
                isWalking = false
            )
        }
        Log.d("MapViewModel", "moveCharacterTo: $x, $y")
    }

    fun stopWalking() {
        _mapState.value = _mapState.value.copy(
            isWalking = false
        )
    }

    fun startWalking() {
        _mapState.value = _mapState.value.copy(
            isWalking = true
        )
    }

    fun setError(error: String?) {
        _mapState.value = _mapState.value.copy(error = error)
    }

    fun toggleBgm() {
        viewModelScope.launch {
            val currentBgmState = settingsRepository.getBgmEnabled().first()
            val newBgmState = !currentBgmState
            settingsRepository.setBgmEnabled(newBgmState)
            
            if (newBgmState) {
                bgmRepository.playFor(BgmGroup.WORLD)
            } else {
                bgmRepository.pause()
            }
        }
    }

    override fun onCleared() {
        super.onCleared()
        Log.d("MapViewModel", "onCleared: viewmodel 소멸")
    }
}
