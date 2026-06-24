package com.ssafy.jjongle.presentation.ui.screen

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.ssafy.jjongle.main.presentation.R
import com.ssafy.jjongle.presentation.model.CharacterType
import com.ssafy.jjongle.common.presentation.ui.component.ResponsiveBackgroundImage
import com.ssafy.jjongle.common.presentation.ui.components.ProfileDialog
import com.ssafy.jjongle.presentation.viewmodel.AuthIntent
import com.ssafy.jjongle.presentation.viewmodel.AuthViewModel
import com.ssafy.jjongle.presentation.viewmodel.ProfileIntent
import com.ssafy.jjongle.presentation.viewmodel.ProfileViewModel

@Composable
fun SignupScreen(
    idToken: String,
    onNavigateToMap: () -> Unit,
    viewModel: ProfileViewModel = hiltViewModel(),
    authViewModel: AuthViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    SignupContent(
        nickname = uiState.nickname,
        selectedCharacter = uiState.mainCharacter,
        onNicknameChange = { viewModel.onIntent(ProfileIntent.NicknameChanged(it)) },
        onCharacterSelect = { viewModel.onIntent(ProfileIntent.MainCharacterSelected(it)) },
        onConfirmClick = {
            authViewModel.onIntent(AuthIntent.SignUp(
                idToken = idToken,
                nickname = uiState.nickname,
                profileImage = uiState.mainCharacter.serverName,
                onSuccess = {
                    onNavigateToMap()
                },
                onFailure = {},
                onNeedLogin = {},
            ))
        }
    )
}

@Composable
fun SignupContent(
    nickname: String,
    selectedCharacter: CharacterType,
    onNicknameChange: (String) -> Unit,
    onCharacterSelect: (CharacterType) -> Unit,
    onConfirmClick: () -> Unit
) {
    Box(modifier = Modifier.fillMaxSize()) {
        ResponsiveBackgroundImage(
            painter = painterResource(id = R.drawable.login_bg),
            contentDescription = null,
            modifier = Modifier.fillMaxSize()
        )

        ProfileDialog(
            nickname = nickname,
            onNicknameChange = onNicknameChange,
            selectedCharacter = selectedCharacter,
            onCharacterSelect = onCharacterSelect,
            onConfirmClick = onConfirmClick
        )
    }
}
