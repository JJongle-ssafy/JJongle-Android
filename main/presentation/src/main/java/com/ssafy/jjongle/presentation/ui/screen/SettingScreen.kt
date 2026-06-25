package com.ssafy.jjongle.presentation.ui.screen

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.ssafy.jjongle.main.presentation.R
import com.ssafy.jjongle.presentation.model.CharacterType
import com.ssafy.jjongle.common.presentation.ui.component.ArchiText
import com.ssafy.jjongle.common.presentation.ui.component.BaseButton
import com.ssafy.jjongle.common.presentation.ui.component.ResponsiveBackgroundImage
import com.ssafy.jjongle.common.presentation.ui.theme.ArchiThemeImpl
import com.ssafy.jjongle.common.presentation.ui.components.ProfileDialog
import com.ssafy.jjongle.presentation.viewmodel.AuthIntent
import com.ssafy.jjongle.presentation.viewmodel.AuthViewModel
import com.ssafy.jjongle.presentation.viewmodel.ProfileViewModel

/**
 * 메인 기능의 Setting 화면을 렌더링하는 Compose 진입점입니다.
 *
 * 전달받은 상태를 화면 요소로 배치하고, 사용자의 주요 액션은 콜백이나 ViewModel Intent로 넘겨 화면과 상태 변경 책임을 분리합니다.
 */
@Composable
fun SettingScreen(
    onBackClick: () -> Unit,
    onUpdated: () -> Unit,     // 프로필 수정 완료 후 이동/토스트 등
    onWithdrawn: () -> Unit,   // 탈퇴 완료 후 로그인 화면 등으로 이동
    goHomeButtonText: String = "뒤로가기",
    profileViewModel: ProfileViewModel = hiltViewModel(),
    authViewModel: AuthViewModel = hiltViewModel()
) {
    val authUiState by authViewModel.uiState.collectAsStateWithLifecycle()
    val authState = authUiState.authState
    val profileState by profileViewModel.uiState.collectAsStateWithLifecycle()

    // 서버에서 온 현재 값들
    val serverNickname = authState.user?.nickname.orEmpty()
    val serverProfile = authState.user?.profileImage ?: "DEFAULT"

    // 편집용 스테이트 (닉네임은 빈값으로 시작 → placeholder가 보임)
    var editingNickname by rememberSaveable { mutableStateOf("") }
    var editingCharacter by remember { mutableStateOf(CharacterType.fromServerName(serverProfile)) }

    // 탈퇴 처리를 위한 상태
    var showWithdrawDialog by rememberSaveable { mutableStateOf(false) }
    var isWithdrawing by remember { mutableStateOf(false) }


    // 서버값이 바뀌면 캐릭터 초기선택도 맞춰줌
    LaunchedEffect(serverProfile) {
        editingCharacter = CharacterType.fromServerName(serverProfile)
    }

    SettingContent(
        serverNickname = serverNickname,
        editingNickname = editingNickname,
        editingCharacter = editingCharacter,
        showWithdrawDialog = showWithdrawDialog,
        isWithdrawing = isWithdrawing,
        onNicknameChange = { editingNickname = it },
        onCharacterSelect = { editingCharacter = it },
        onConfirmClick = {
            val finalNickname =
                if (editingNickname.isBlank()) serverNickname else editingNickname
            authViewModel.onIntent(AuthIntent.UpdateProfile(
                nickname = finalNickname,
                profileImage = editingCharacter.serverName,
                onSuccess = { onUpdated() },
                onFailure = {}
            ))
        },
        onBackClick = onBackClick,
        onWithdrawClick = { showWithdrawDialog = true },
        onWithdrawDismiss = { if (!isWithdrawing) showWithdrawDialog = false },
        onWithdrawConfirm = {
            isWithdrawing = true
            authViewModel.onIntent(AuthIntent.Withdraw(
                onSuccess = {
                    isWithdrawing = false
                    showWithdrawDialog = false
                    onWithdrawn()
                },
                onFailure = {
                    isWithdrawing = false
                    showWithdrawDialog = false
                }
            ))
        },
        goHomeButtonText = goHomeButtonText,
        placeholderText = if (serverNickname.isNotBlank()) serverNickname
        else profileState.nickname
    )
}

@Composable
fun SettingContent(
    serverNickname: String,
    editingNickname: String,
    editingCharacter: CharacterType,
    showWithdrawDialog: Boolean,
    isWithdrawing: Boolean,
    onNicknameChange: (String) -> Unit,
    onCharacterSelect: (CharacterType) -> Unit,
    onConfirmClick: () -> Unit,
    onBackClick: () -> Unit,
    onWithdrawClick: () -> Unit,
    onWithdrawDismiss: () -> Unit,
    onWithdrawConfirm: () -> Unit,
    goHomeButtonText: String = "뒤로가기",
    placeholderText: String = serverNickname
) {
    Box(Modifier.fillMaxSize()) {
        // 배경
        ResponsiveBackgroundImage(
            painter = painterResource(R.drawable.mypage_bg),
            contentDescription = null,
            modifier = Modifier.fillMaxSize()
        )

        // SignupScreen과 동일한 다이얼로그 재사용
        ProfileDialog(
            nickname = editingNickname,
            onNicknameChange = onNicknameChange,
            selectedCharacter = editingCharacter,
            onCharacterSelect = onCharacterSelect,
            confirmText = "수정하기",
            placeholderText = placeholderText,
            onConfirmClick = onConfirmClick
        )

        // 뒤로가기 버튼
        BaseButton(
            onClick = onBackClick,
            text = goHomeButtonText,
            textStyle = ArchiThemeImpl.typeScale.textStrongM,
            modifier = Modifier
                .align(Alignment.TopStart)
                .padding(start = 24.dp, top = 24.dp)
        )

        // 하단 우측: 회원탈퇴
        BaseButton(
            onClick = onWithdrawClick,
            text = "회원탈퇴",
            textStyle = ArchiThemeImpl.typeScale.textStrongM,
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(24.dp)
        )
    }

    // 탈퇴 확인 다이얼로그
    if (showWithdrawDialog) {
        androidx.compose.material3.AlertDialog(
            onDismissRequest = onWithdrawDismiss,
            title = {
                ArchiText(
                    text = "회원탈퇴",
                    style = ArchiThemeImpl.typeScale.titleStrongM,
                    color = ArchiThemeImpl.archiColor.contentDefaultLevel0,
                )
            },
            text = {
                ArchiText(
                    text = "탈퇴하면 모든 기록이 사라지고\n" +
                        "다시 되돌릴 수 없어요.",
                    style = ArchiThemeImpl.typeScale.textRegularM,
                    color = ArchiThemeImpl.archiColor.contentDefaultLevel1,
                )
            },
            confirmButton = {
                androidx.compose.material3.TextButton(
                    enabled = !isWithdrawing,
                    onClick = onWithdrawConfirm
                ) {
                    ArchiText(
                        text = "탈퇴",
                        style = ArchiThemeImpl.typeScale.textStrongM,
                        color = ArchiThemeImpl.archiColor.contentAccent,
                    )
                }
            },
            dismissButton = {
                androidx.compose.material3.TextButton(
                    enabled = !isWithdrawing,
                    onClick = onWithdrawDismiss
                ) {
                    ArchiText(
                        text = "취소",
                        style = ArchiThemeImpl.typeScale.textStrongM,
                        color = ArchiThemeImpl.archiColor.contentDefaultLevel1,
                    )
                }
            }
        )
    }
}
