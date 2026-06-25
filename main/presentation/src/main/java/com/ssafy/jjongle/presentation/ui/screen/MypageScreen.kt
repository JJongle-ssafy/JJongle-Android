package com.ssafy.jjongle.presentation.ui.screen

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.ssafy.jjongle.main.presentation.R
import com.ssafy.jjongle.common.presentation.ui.component.ArchiText
import com.ssafy.jjongle.common.presentation.ui.component.BaseButton
import com.ssafy.jjongle.common.presentation.ui.layout.SystemBackgroundImageEffect
import com.ssafy.jjongle.common.presentation.ui.theme.ArchiThemeImpl
import com.ssafy.jjongle.presentation.viewmodel.AuthIntent
import com.ssafy.jjongle.presentation.viewmodel.AuthViewModel

/**
 * 메인 기능의 Mypage 화면을 렌더링하는 Compose 진입점입니다.
 *
 * 전달받은 상태를 화면 요소로 배치하고, 사용자의 주요 액션은 콜백이나 ViewModel Intent로 넘겨 화면과 상태 변경 책임을 분리합니다.
 */
@Composable
fun MypageScreen(
    authViewModel: AuthViewModel = hiltViewModel(),
    onAnimalBookClick: () -> Unit, // 동물도감 (칠교 놀이)
    onQuizNoteClick: () -> Unit, // 지식노트 (OX 퀴즈)
    onSettingClick: () -> Unit, // 설정 버튼
    onGoMapClick: () -> Unit,   // 처음으로 돌아가기 버튼
    onLogoutClick: () -> Unit, // 로그아웃 버튼
    goHomeButtonText: String = "처음으로 돌아가기"
) {

    val authUiState by authViewModel.uiState.collectAsStateWithLifecycle()
    val authState = authUiState.authState
    val nickname = authState.user?.nickname ?: "사용자"
    val profileImageRes = when (authState.user?.profileImage) {
        "MONGI" -> R.drawable.profile_mongi
        "TOBY" -> R.drawable.profile_toby
        "LUNA" -> R.drawable.profile_luna
        else -> R.drawable.profile_mongi // 기본 프로필 이미지
    }

    MypageContent(
        nickname = nickname,
        profileImageRes = profileImageRes,
        onAnimalBookClick = onAnimalBookClick,
        onQuizNoteClick = onQuizNoteClick,
        onSettingClick = onSettingClick,
        onGoMapClick = onGoMapClick,
        onLogoutClick = {
            authViewModel.onIntent(AuthIntent.Logout)
            onLogoutClick()
        },
        goHomeButtonText = goHomeButtonText
    )
}

@Composable
fun MypageContent(
    nickname: String,
    profileImageRes: Int,
    onAnimalBookClick: () -> Unit,
    onQuizNoteClick: () -> Unit,
    onSettingClick: () -> Unit,
    onGoMapClick: () -> Unit,
    onLogoutClick: () -> Unit,
    goHomeButtonText: String = "처음으로 돌아가기"
) {
    val colors = ArchiThemeImpl.archiColor
    val typeScale = ArchiThemeImpl.typeScale

    SystemBackgroundImageEffect(R.drawable.mypage_bg)

    Box(modifier = Modifier.fillMaxSize()) {
        // 뒤로가기 버튼
        BaseButton(
            onClick = onGoMapClick,
            text = goHomeButtonText,
            textStyle = typeScale.textStrongM,
            modifier = Modifier
                .align(Alignment.TopStart)
                .padding(start = 24.dp, top = 24.dp)
        )

        // 로그아웃 버튼 (우측 상단) — 뒤로가기와 동일 스타일
        BaseButton(
            onClick = onLogoutClick,
            text = "로그아웃",
            textStyle = typeScale.textStrongM,
            modifier = Modifier
                .align(Alignment.TopEnd)
                .padding(end = 24.dp, top = 24.dp)
        )


        // 중앙 콘텐츠 (프로필 + 동물도감 + 지식노트)
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 80.dp)
                .padding(top = 92.dp, bottom = 44.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            // 프로필 + 프레임 겹치기
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier.size(300.dp)
            ) {
                // 1. 프로필 이미지
                Image(
                    painter = painterResource(id = profileImageRes),
                    contentDescription = "캐릭터 프로필",
                    modifier = Modifier
                        .size(210.dp)
                        .clip(CircleShape)
                        .border(4.dp, colors.borderAccent, CircleShape)
                )

                // 2. 프로필 프레임 (위에 겹침)
                Image(
                    painter = painterResource(id = R.drawable.profile_frame),
                    contentDescription = "프로필 프레임",
                    modifier = Modifier.size(300.dp)
                )
            }

            // — 중앙 프로필 + 이름 (Center 고정)
            Column(
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // 프로필+프레임 겹치기 (생략)
                Spacer(modifier = Modifier.height(4.dp))
                ArchiText(
                    text = "$nickname 대원",
                    style = typeScale.titleStrongL,
                    color = colors.contentDefaultLevel0,
                )
            }

            Spacer(modifier = Modifier.height(28.dp))

            // 하단 3등분 Row (contentDescription 추가)
            Row(
                modifier = Modifier
                    .height(250.dp)
                    .fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // — 1/3 칸: 동물도감
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Image(
                        painter = painterResource(R.drawable.animal_book),
                        contentDescription = "동물 친구들 이동 버튼",
                        modifier = Modifier
                            .size(230.dp)
                            .testTag("mypage_animal_book_button")
                            .clickable { onAnimalBookClick() }
                    )
                }

                // — 1/3 칸: 설정 버튼
                Box(
                    modifier = Modifier.weight(1f),
                    contentAlignment = Alignment.Center
                ) {
                    Box(
                        modifier = Modifier
                            .size(160.dp, 58.dp)
                            .clip(RoundedCornerShape(16.dp))
                            .background(colors.bgBrandLevel0)
                            .testTag("mypage_setting_button")
                            .clickable { onSettingClick() },
                        contentAlignment = Alignment.Center
                    ) {
                        ArchiText(
                            text = "설정",
                            style = typeScale.textStrongL,
                            color = colors.contentOnBrand,
                        )
                    }
                }

                // — 1/3 칸: 지식노트
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Image(
                        painter = painterResource(R.drawable.quiz_book),
                        contentDescription = "지식 노트 이동 버튼",
                        modifier = Modifier
                            .size(230.dp)
                            .testTag("mypage_quiz_note_button")
                            .clickable { onQuizNoteClick() }
                    )
                }
            }
        }
    }
}
