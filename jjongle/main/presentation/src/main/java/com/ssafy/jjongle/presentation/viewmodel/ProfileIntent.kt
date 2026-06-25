package com.ssafy.jjongle.presentation.viewmodel

import com.ssafy.jjongle.common.presentation.mvi.MviIntent
import com.ssafy.jjongle.presentation.model.CharacterType

/**
 * ProfileIntent 화면에서 ViewModel로 전달되는 사용자 입력을 정의합니다.
 *
 * - 계층: main/presentation
 * - 책임: UI 이벤트를 MVI intent로 분리해 상태 변경 진입점을 명확히 합니다.
 */
sealed interface ProfileIntent : MviIntent {
    data class NicknameChanged(val nickname: String) : ProfileIntent
    data class MainCharacterSelected(val character: CharacterType) : ProfileIntent
}
