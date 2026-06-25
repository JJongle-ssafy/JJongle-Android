package com.ssafy.jjongle.presentation.viewmodel

import com.ssafy.jjongle.common.presentation.mvi.MviIntent
import com.ssafy.jjongle.presentation.model.CharacterType

/**
 * 메인 기능 화면에서 ViewModel로 전달되는 사용자 입력과 화면 이벤트입니다.
 *
 * 버튼 클릭, 화면 진입, 선택 변경 같은 입력을 타입으로 분리해 상태 변경의 시작점을 명확히 남깁니다.
 */
sealed interface ProfileIntent : MviIntent {
    data class NicknameChanged(val nickname: String) : ProfileIntent
    data class MainCharacterSelected(val character: CharacterType) : ProfileIntent
}
