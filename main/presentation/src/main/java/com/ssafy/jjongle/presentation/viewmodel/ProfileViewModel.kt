package com.ssafy.jjongle.presentation.viewmodel

import com.ssafy.jjongle.common.presentation.mvi.MviViewModel
import com.ssafy.jjongle.presentation.state.ProfileState
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

/**
 * ProfileViewModel 화면 상태와 이벤트를 처리하는 ViewModel입니다.
 *
 * - 계층: main/presentation
 * - 책임: 유스케이스를 호출하고 UI가 구독할 상태 흐름을 제공합니다.
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
