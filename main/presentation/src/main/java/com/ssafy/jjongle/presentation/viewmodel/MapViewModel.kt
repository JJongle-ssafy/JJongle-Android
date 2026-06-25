package com.ssafy.jjongle.presentation.viewmodel

import androidx.lifecycle.viewModelScope
import com.ssafy.jjongle.common.entity.BgmGroup
import com.ssafy.jjongle.common.domain.repository.BgmRepository
import com.ssafy.jjongle.common.domain.repository.SettingsRepository
import com.ssafy.jjongle.common.presentation.mvi.MviViewModel
import com.ssafy.jjongle.main.entity.tti.MapTTIPage
import com.ssafy.jjongle.presentation.state.MapState
import com.ssafy.jjongle.tti.TTIHelper
import com.ssafy.jjongle.tti.TTITimelineCategory
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * Map 화면의 사용자 입력과 비동기 결과를 UI 상태로 변환하는 ViewModel입니다.
 *
 * UseCase 호출, 오류 처리, 상태 전이를 한곳에 모아 Compose 화면은 상태 구독과 Intent 전달에 집중하도록 합니다.
 */
@HiltViewModel
class MapViewModel @Inject constructor(
    private val bgmRepository: BgmRepository,
    private val settingsRepository: SettingsRepository,
    private val ttiHelper: TTIHelper,
) : MviViewModel<MapIntent, MapState, MapReducerEvent>(MapState.empty) {

    private val bgmEnabled: StateFlow<Boolean> = settingsRepository.getBgmEnabled().stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = MapState.empty.isBgmOn,
    )

    init {
        ttiHelper.startTTITracking(MapTTIPage)
        ttiHelper.startTTITimeline(MapTTIPage, TTITimelineCategory.VIEW_BINDING)
        viewModelScope.launch {
            bgmEnabled.collect { enabled ->
                dispatch(MapReducerEvent.BgmChanged(enabled))
            }
        }
    }

    override fun onIntent(intent: MapIntent) {
        when (intent) {
            is MapIntent.MoveCharacterTo -> dispatch(
                MapReducerEvent.CharacterMoved(
                    x = intent.x,
                    y = intent.y,
                )
            )
            MapIntent.StartWalking -> dispatch(MapReducerEvent.WalkingStarted)
            MapIntent.ToggleBgm -> toggleBgm()
        }
    }

    override fun reduce(state: MapState, event: MapReducerEvent): MapState = when (event) {
        is MapReducerEvent.BgmChanged -> state.copy(isBgmOn = event.enabled)
        is MapReducerEvent.CharacterMoved -> state.copy(
            characterX = event.x,
            characterY = event.y,
            isWalking = false,
        )
        MapReducerEvent.WalkingStarted -> state.copy(isWalking = true)
    }

    private fun toggleBgm() {
        viewModelScope.launch {
            val currentBgmState = bgmEnabled.first()
            val newBgmState = !currentBgmState
            settingsRepository.setBgmEnabled(newBgmState)

            if (newBgmState) {
                bgmRepository.playFor(BgmGroup.WORLD)
            } else {
                bgmRepository.pause()
            }
        }
    }

    fun onInitialContentDisplayed() {
        ttiHelper.endTTITimeline(MapTTIPage, TTITimelineCategory.VIEW_BINDING)
        ttiHelper.endTTITracking(MapTTIPage)
    }

    fun onPageLeaving() {
        ttiHelper.shotTTILogging(MapTTIPage)
    }
}
