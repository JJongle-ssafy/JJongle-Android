package com.ssafy.jjongle.presentation.viewmodel

import com.ssafy.jjongle.common.presentation.mvi.ReducerEvent
import com.ssafy.jjongle.presentation.model.CharacterType

sealed interface ProfileReducerEvent : ReducerEvent {
    data class NicknameChanged(val nickname: String) : ProfileReducerEvent
    data class MainCharacterSelected(val character: CharacterType) : ProfileReducerEvent
}
