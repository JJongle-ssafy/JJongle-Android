package com.ssafy.jjongle.presentation.ui.screen

import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.ssafy.jjongle.R
import com.ssafy.jjongle.presentation.model.CharacterType
import com.ssafy.jjongle.presentation.ui.component.BaseButton
import com.ssafy.jjongle.presentation.ui.components.ProfileDialog
import com.ssafy.jjongle.presentation.viewmodel.AuthViewModel
import com.ssafy.jjongle.presentation.viewmodel.ProfileViewModel

@Composable
fun SettingScreen(
    onBackClick: () -> Unit,
    onUpdated: () -> Unit,     // 프로필 수정 완료 후 이동/토스트 등
    onWithdrawn: () -> Unit,   // 탈퇴 완료 후 로그인 화면 등으로 이동
    goHomeButtonText: String = "뒤로가기",
    profileViewModel: ProfileViewModel = hiltViewModel(),
    authViewModel: AuthViewModel = hiltViewModel()
) {
    val authState by authViewModel.authState.collectAsState()

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


    Box(Modifier.fillMaxSize()) {
        // 배경
        Image(
            painter = painterResource(R.drawable.mypage_bg),
            contentDescription = null,
            modifier = Modifier.fillMaxSize()
        )

        // SignupScreen과 동일한 다이얼로그 재사용
        ProfileDialog(
            nickname = editingNickname,
            onNicknameChange = { editingNickname = it },          // ← 로컬 상태 갱신
            selectedCharacter = editingCharacter,
            onCharacterSelect = { editingCharacter = it },        // ← 로컬 상태 갱신
            confirmText = "수정하기",
            placeholderText = if (serverNickname.isNotBlank()) serverNickname
            else profileViewModel.nickname.value, // 로컬 보정
            onConfirmClick = {
                val finalNickname =
                    if (editingNickname.isBlank()) serverNickname else editingNickname
                authViewModel.updateProfile(
                    nickname = finalNickname,
                    profileImage = editingCharacter.serverName,
                    onSuccess = { onUpdated() },
                    onFailure = { Log.e("SettingScreen", "프로필 수정 실패: ${it.message}") }
                )
            }
        )

        // 뒤로가기 버튼
        BaseButton(
            onClick = onBackClick,
            text = goHomeButtonText,
            fontSize = 18.sp,
            modifier = Modifier
                .align(Alignment.TopStart)
                .padding(start = 24.dp, top = 24.dp)
        )

        // 하단 우측: 회원탈퇴
        BaseButton(
            // TODO: 회원탈퇴 다이얼로그 구현
            onClick = { showWithdrawDialog = true },
            text = "회원탈퇴",
            fontSize = 16.sp,
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(24.dp)
        )
    }

    // 탈퇴 확인 다이얼로그
    if (showWithdrawDialog) {
        androidx.compose.material3.AlertDialog(
            onDismissRequest = { if (!isWithdrawing) showWithdrawDialog = false },
            title = { androidx.compose.material3.Text("회원탈퇴") },
            text = {
                androidx.compose.material3.Text(
                    "탈퇴하면 모든 기록이 사라지고\n" +
                            "다시 되돌릴 수 없어요."
                )
            },
            confirmButton = {
                androidx.compose.material3.TextButton(
                    enabled = !isWithdrawing,
                    onClick = {
                        isWithdrawing = true
                        authViewModel.withdraw(
                            onSuccess = {
                                isWithdrawing = false
                                showWithdrawDialog = false
                                onWithdrawn() // NavGraph에서 로그인으로 이동
                            },
                            onFailure = { e ->
                                isWithdrawing = false
                                showWithdrawDialog = false
                                android.util.Log.e("SettingScreen", "탈퇴 실패: ${e.message}")
                                // 필요 시 토스트/스낵바
                            }
                        )
                    }
                ) { androidx.compose.material3.Text("탈퇴") }
            },
            dismissButton = {
                androidx.compose.material3.TextButton(
                    enabled = !isWithdrawing,
                    onClick = { showWithdrawDialog = false }
                ) { androidx.compose.material3.Text("취소") }
            }
        )
    }
}
