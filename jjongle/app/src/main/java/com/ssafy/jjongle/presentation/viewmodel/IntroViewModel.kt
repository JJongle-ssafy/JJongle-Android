package com.ssafy.jjongle.presentation.viewmodel

import androidx.lifecycle.ViewModel
import com.ssafy.jjongle.data.model.TTSResponseWrapper
import com.ssafy.jjongle.domain.usecase.TTSUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class IntroViewModel @Inject constructor(
    private val ttsUseCase: TTSUseCase
) : ViewModel() {

    suspend fun generateTTS(text: String): Result<TTSResponseWrapper> {
        return ttsUseCase.generateTTS(text)
    }
}


