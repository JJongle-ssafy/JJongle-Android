package com.ssafy.jjongle.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ssafy.jjongle.data.service.BgmManager
import com.ssafy.jjongle.domain.repository.SettingsRepository
import com.ssafy.jjongle.presentation.media.BgmGroup
import com.ssafy.jjongle.presentation.navigation.routeToBgmGroup
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MusicViewModel @Inject constructor(
    private val bgm: BgmManager,
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
