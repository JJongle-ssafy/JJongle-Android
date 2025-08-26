package com.ssafy.jjongle.presentation.state

import com.ssafy.jjongle.data.model.TTSResponseWrapper

sealed class TTSState {
    object Idle : TTSState()
    object Loading : TTSState()
    data class Success(val response: TTSResponseWrapper) :
        TTSState() // Or perhaps a byte array, etc.

    data class Error(val message: String) : TTSState()
}