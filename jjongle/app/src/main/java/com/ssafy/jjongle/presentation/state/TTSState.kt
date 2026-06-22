package com.ssafy.jjongle.presentation.state

import com.ssafy.jjongle.domain.entity.TtsAudio

sealed class TTSState {
    object Idle : TTSState()
    object Loading : TTSState()
    data class Success(val audio: TtsAudio) : TTSState()

    data class Error(val message: String) : TTSState()
}
