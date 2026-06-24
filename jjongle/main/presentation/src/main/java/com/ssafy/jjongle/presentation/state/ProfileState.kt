package com.ssafy.jjongle.presentation.state

import com.ssafy.jjongle.common.presentation.mvi.UiState
import com.ssafy.jjongle.presentation.model.CharacterType

data class ProfileState(
    val nickname: String = "",
    val mainCharacter: CharacterType = CharacterType.MONGI,
) : UiState {
    companion object {
        val empty = ProfileState()
    }
}
