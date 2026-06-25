package com.ssafy.jjongle.presentation.viewmodel

import com.ssafy.jjongle.presentation.model.CharacterType
import org.junit.Assert.assertEquals
import org.junit.Test

/**
 * Profile View Model Test 회귀를 막기 위한 테스트입니다.
 *
 * 리팩터링이나 모듈 이동 중에도 기존 동작, 아키텍처 경계, 오류 처리 계약이 유지되는지 확인합니다.
 */
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
