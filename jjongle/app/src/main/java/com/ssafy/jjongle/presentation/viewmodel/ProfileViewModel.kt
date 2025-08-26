package com.ssafy.jjongle.presentation.viewmodel

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import com.ssafy.jjongle.presentation.model.CharacterType
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class ProfileViewModel @Inject constructor() : ViewModel() {

    private val _nickname = mutableStateOf("")
    val nickname: State<String> = _nickname

    private val _mainCharacter = mutableStateOf(CharacterType.MONGI)
    val mainCharacter: State<CharacterType> = _mainCharacter

    fun setNickname(nick: String) {
        _nickname.value = nick
    }

    fun setMainCharacter(character: CharacterType) {
        _mainCharacter.value = character
    }
}
