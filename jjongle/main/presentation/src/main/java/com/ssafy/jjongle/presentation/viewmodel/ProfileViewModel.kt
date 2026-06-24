package com.ssafy.jjongle.presentation.viewmodel

import com.ssafy.jjongle.common.presentation.mvi.MviViewModel
import com.ssafy.jjongle.presentation.state.ProfileState
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

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
