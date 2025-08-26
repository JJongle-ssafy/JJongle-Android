package com.ssafy.jjongle.presentation.ui.screen

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.ssafy.jjongle.R
import com.ssafy.jjongle.presentation.ui.theme.JalnanFont
import com.ssafy.jjongle.presentation.ui.theme.JjongleTheme
import com.ssafy.jjongle.presentation.viewmodel.AuthViewModel

@Composable
fun SplashScreen(
    onNavigateToLogin: () -> Unit,
    onNavigateToMap: () -> Unit,
    viewModel: AuthViewModel = hiltViewModel()
) {

    // 로그인 상태를 관찰
    val authState by viewModel.authState.collectAsState()

    // ✨ 깜빡임 효과 위한 alpha 애니메이션 정의
    val infiniteTransition = rememberInfiniteTransition()
    val alpha by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = 0f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 1500, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        )
    )


    Box(
        modifier = Modifier
            .fillMaxSize()
            .clickable {
                if (authState.isAuthenticated) {
                    // 로그인 상태라면 게임 화면으로 이동
                    onNavigateToMap()
                } else {
                    // 로그인 상태가 아니라면 로그인 화면으로 이동
                    onNavigateToLogin()
                }
            }
    ) {
        Image(
            painter = painterResource(id = R.drawable.login_bg),
            contentDescription = "Login Background",
        )
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(bottom = 60.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Image(
                painter = painterResource(id = R.drawable.jjongle_logo),
                contentDescription = "Logo",
                modifier = Modifier.size(600.dp)
            )

            Text(
                text = "화면을 누르면 모험이 시작돼요 !",
                fontFamily = JalnanFont,
                fontSize = 35.sp,
                color = Color(0xFFFFFFFF),
                modifier = Modifier
                    .padding(top = 10.dp)
                    .alpha(alpha)       // blinking 효과 적용
            )
        }
    }
}


@Preview(showBackground = true)
@Composable
fun SplashScreenPreview() {
    JjongleTheme {
        SplashScreen(
            onNavigateToLogin = {},
            onNavigateToMap = {}
        ) // NavController는 실제로는 context가 필요합니다.
    }
}