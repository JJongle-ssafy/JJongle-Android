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

/**
 * 메인 기능의 Signup 화면을 렌더링하는 Compose 진입점입니다.
 *
 * 전달받은 상태를 화면 요소로 배치하고, 사용자의 주요 액션은 콜백이나 ViewModel Intent로 넘겨 화면과 상태 변경 책임을 분리합니다.
 */
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
