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
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.ssafy.jjongle.R
import com.ssafy.jjongle.presentation.ui.component.BaseButton
import com.ssafy.jjongle.presentation.viewmodel.AuthViewModel


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

    val nickname = authViewModel.authState.value.user?.nickname ?: "사용자"
    val profileImageRes = when (authViewModel.authState.value.user?.profileImage) {
        "MONGI" -> R.drawable.profile_mongi
        "TOBY" -> R.drawable.profile_toby
        "LUNA" -> R.drawable.profile_luna
        else -> R.drawable.profile_mongi // 기본 프로필 이미지
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
    ) {

        // 배경 이미지
        Image(
            painter = painterResource(id = R.drawable.mypage_bg),
            contentDescription = "Mypage Background",
            modifier = Modifier.fillMaxSize()
        )

        // 뒤로가기 버튼
        BaseButton(
            onClick = onGoMapClick,
            text = goHomeButtonText,
            fontSize = 18.sp,
            modifier = Modifier
                .align(Alignment.TopStart)
                .padding(start = 24.dp, top = 24.dp)
        )

        // 로그아웃 버튼 (우측 상단) — 뒤로가기와 동일 스타일
        BaseButton(
            onClick = {
                authViewModel.logout()  // 토큰 삭제 + 상태 초기화 (AuthViewModel에 구현되어 있어야 함)
                onLogoutClick()         // 네비게이션 콜백
            },
            text = "로그아웃",
            fontSize = 18.sp,
            modifier = Modifier
                .align(Alignment.TopEnd)
                .padding(end = 24.dp, top = 24.dp)
        )


        // 중앙 콘텐츠 (프로필 + 동물도감 + 지식노트)
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 30.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            // 프로필 + 프레임 겹치기
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier.size(430.dp) // 전체 사이즈는 프로필 기준
            ) {
                // 1. 프로필 이미지
                Image(
                    painter = painterResource(id = profileImageRes),
                    contentDescription = "캐릭터 프로필",
                    modifier = Modifier
                        .size(300.dp) // 내부 이미지
                        .clip(CircleShape)
                        .border(4.dp, Color(0xFF567147), CircleShape)
                )

                // 2. 프로필 프레임 (위에 겹침)
                Image(
                    painter = painterResource(id = R.drawable.profile_frame),
                    contentDescription = "프로필 프레임",
                    modifier = Modifier.size(400.dp) // 프레임이 바깥으로 감싸는 크기
                )
            }

            // — 중앙 프로필 + 이름 (Center 고정)
            Column(
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // 프로필+프레임 겹치기 (생략)
                Spacer(modifier = Modifier.height(8.dp))
                Text(text = "$nickname 대원", fontSize = 50.sp, color = Color(0xFF3F1E13))
            }

            // 하단 3등분 Row (contentDescription 추가)
            Row(
                modifier = Modifier
//                    .align(Alignment.BottomCenter)
                    .height(900.dp)
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
                            .size(400.dp)
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
                            .size(180.dp, 70.dp)
                            .clip(RoundedCornerShape(16.dp))
                            .background(Color(0xFF562405))
                            .clickable { onSettingClick() },
                        contentAlignment = Alignment.Center
                    ) {
                        Text("설정", color = Color.White, fontSize = 26.sp)
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
                            .size(400.dp)
                            .clickable { onQuizNoteClick() }
                    )
                }
            }
        }
    }
}


