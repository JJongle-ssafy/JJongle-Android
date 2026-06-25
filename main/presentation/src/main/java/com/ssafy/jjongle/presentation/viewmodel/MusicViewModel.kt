package com.ssafy.jjongle.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ssafy.jjongle.common.entity.BgmGroup
import com.ssafy.jjongle.common.domain.repository.BgmRepository
import com.ssafy.jjongle.common.domain.repository.SettingsRepository
import com.ssafy.jjongle.presentation.navigation.routeToBgmGroup
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * Music 화면의 사용자 입력과 비동기 결과를 UI 상태로 변환하는 ViewModel입니다.
 *
 * UseCase 호출, 오류 처리, 상태 전이를 한곳에 모아 Compose 화면은 상태 구독과 Intent 전달에 집중하도록 합니다.
 */
@HiltViewModel
class MusicViewModel @Inject constructor(
    private val bgm: BgmRepository,
    private val settingsRepository: SettingsRepository
) : ViewModel() {

    fun onRouteChanged(route: String?) {
        viewModelScope.launch {
            val bgmEnabled = settingsRepository.getBgmEnabled().first()
            if (!bgmEnabled) {
                bgm.pause()
                return@launch
            }
            
            val group: BgmGroup? = routeToBgmGroup(route)
            group?.let { bgm.playFor(it) }
        }
    }

    fun pause() = bgm.pause()
    fun resume() = bgm.resume()
}
