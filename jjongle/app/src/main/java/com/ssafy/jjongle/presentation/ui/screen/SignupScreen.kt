package com.ssafy.jjongle.presentation.ui.screen

import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.hilt.navigation.compose.hiltViewModel
import com.ssafy.jjongle.R
import com.ssafy.jjongle.presentation.ui.components.ProfileDialog
import com.ssafy.jjongle.presentation.viewmodel.AuthViewModel
import com.ssafy.jjongle.presentation.viewmodel.ProfileViewModel

@Composable
fun SignupScreen(
    idToken: String, // ✅ NavGraph에서 전달된 토큰
    onNavigateToMap: () -> Unit,
    viewModel: ProfileViewModel = hiltViewModel(),
    authViewModel: AuthViewModel = hiltViewModel() // ✅ AuthViewModel 주입
) {
    val nickname by viewModel.nickname
    val selectedCharacter by viewModel.mainCharacter

    Box(modifier = Modifier.fillMaxSize()) {
        Image(
            painter = painterResource(id = R.drawable.login_bg),
            contentDescription = null,
            modifier = Modifier.fillMaxSize()
        )

        ProfileDialog(
            nickname = nickname,
            onNicknameChange = { viewModel.setNickname(it) },
            selectedCharacter = selectedCharacter,
            onCharacterSelect = { viewModel.setMainCharacter(it) },
            onConfirmClick = {
                Log.d("SignupScreen", "✅ 가입 버튼 클릭됨")
                authViewModel.signUp(
                    idToken = idToken,
                    nickname = nickname,

                    // TODO : test용 이미지 default로 설정 - 서버에서 받아온 프로필명 적용되는지 확인 필요.
//                    profileImage = "DEFAULT",
                    profileImage = selectedCharacter.serverName,

                    onSuccess = {
                        Log.d("SignupScreen", "✅ 회원가입 성공, 지도 화면으로 이동")
                        onNavigateToMap()
                    },
                    onFailure = {
                        Log.e("SignupScreen", "❌ 회원가입 실패: ${it.message}")
                        Log.d(
                            "SignupScreen",
                            "🔥 가입 요청: nickname=$nickname, profileImage=${selectedCharacter.serverName}"
                        )

                    }
                )
            }

        )
    }
}
