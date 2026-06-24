package com.ssafy.jjongle.presentation.viewmodel

import com.ssafy.jjongle.common.presentation.mvi.MviIntent
import com.ssafy.jjongle.presentation.model.CharacterType

sealed interface ProfileIntent : MviIntent {
    data class NicknameChanged(val nickname: String) : ProfileIntent
    data class MainCharacterSelected(val character: CharacterType) : ProfileIntent
}
