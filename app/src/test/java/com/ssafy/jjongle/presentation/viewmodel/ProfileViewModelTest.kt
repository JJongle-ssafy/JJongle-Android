package com.ssafy.jjongle.presentation.viewmodel

import com.ssafy.jjongle.presentation.model.CharacterType
import org.junit.Assert.assertEquals
import org.junit.Test

/**
 * Profile의 아키텍처 또는 동작 계약을 검증하는 테스트입니다.
 *
 * - 계층: test
 * - 책임: 회귀를 막는 대표 시나리오와 모듈 경계 조건을 실행합니다.
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
