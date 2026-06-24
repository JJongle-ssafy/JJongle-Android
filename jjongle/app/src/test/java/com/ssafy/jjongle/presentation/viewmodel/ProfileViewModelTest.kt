package com.ssafy.jjongle.presentation.viewmodel

import com.ssafy.jjongle.presentation.model.CharacterType
import org.junit.Assert.assertEquals
import org.junit.Test

class ProfileViewModelTest {

    @Test
    fun profile_state_changes_only_through_intents() {
        val viewModel = ProfileViewModel()

        viewModel.onIntent(ProfileIntent.NicknameChanged("루나"))
        viewModel.onIntent(ProfileIntent.MainCharacterSelected(CharacterType.TOBY))

        val state = viewModel.uiState.value
        assertEquals("루나", state.nickname)
        assertEquals(CharacterType.TOBY, state.mainCharacter)
    }
}
