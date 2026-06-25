package com.ssafy.jjongle.presentation.viewmodel

import com.ssafy.jjongle.common.presentation.mvi.MviViewModel
import com.ssafy.jjongle.presentation.state.ProfileState
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

/**
 * Profile 화면의 사용자 입력과 비동기 결과를 UI 상태로 변환하는 ViewModel입니다.
 *
 * UseCase 호출, 오류 처리, 상태 전이를 한곳에 모아 Compose 화면은 상태 구독과 Intent 전달에 집중하도록 합니다.
 */
@HiltViewModel
class ProfileViewModel @Inject constructor() :
    MviViewModel<ProfileIntent, ProfileState, ProfileReducerEvent>(ProfileState.empty) {

    override fun onIntent(intent: ProfileIntent) {
        when (intent) {
            is ProfileIntent.NicknameChanged -> dispatch(
                ProfileReducerEvent.NicknameChanged(intent.nickname)
            )
            is ProfileIntent.MainCharacterSelected -> dispatch(
                ProfileReducerEvent.MainCharacterSelected(intent.character)
            )
        }
    }

    override fun reduce(state: ProfileState, event: ProfileReducerEvent): ProfileState =
        when (event) {
            is ProfileReducerEvent.NicknameChanged -> state.copy(nickname = event.nickname)
            is ProfileReducerEvent.MainCharacterSelected -> state.copy(mainCharacter = event.character)
        }
}
