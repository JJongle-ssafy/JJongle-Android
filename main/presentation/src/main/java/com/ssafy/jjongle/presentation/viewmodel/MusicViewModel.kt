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
 * MusicViewModel 화면 상태와 이벤트를 처리하는 ViewModel입니다.
 *
 * - 계층: main/presentation
 * - 책임: 유스케이스를 호출하고 UI가 구독할 상태 흐름을 제공합니다.
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
