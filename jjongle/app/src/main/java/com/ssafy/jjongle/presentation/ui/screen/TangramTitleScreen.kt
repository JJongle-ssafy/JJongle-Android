package com.ssafy.jjongle.presentation.ui.screen

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.ssafy.jjongle.R
import com.ssafy.jjongle.presentation.ui.component.BaseButton

@Composable
fun TangramTitleScreen(
    gameName: String,
    backgroundImagePainter: Painter,
    onStartGameClick: () -> Unit,
    onGoMapClick: () -> Unit,
    onGameRulesClick: () -> Unit,
    modifier: Modifier = Modifier,
    startGameButtonText: String = "탐험을 떠나볼까요?",
    goHomeButtonText: String = "처음으로 돌아가기",
    gameRulesButtonText: String = "놀이 설명"
) {
    Box(
        modifier = modifier.fillMaxSize()
    ) {
        // 배경 이미지
        Image(
            painter = backgroundImagePainter,
            contentDescription = "$gameName 배경 이미지",
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop
        )


        // 중앙 하단 버튼들
        Column(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = 48.dp)
                .width(400.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // 게임 시작 버튼
            BaseButton(
                onClick = onStartGameClick,
                text = startGameButtonText,
                fontSize = 24.sp,
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(16.dp))
            BaseButton(
                onClick = onGameRulesClick,
                text = gameRulesButtonText,
                fontSize = 18.sp,
                modifier = Modifier.fillMaxWidth()
            )
        }

        // 처음으로 버튼 (좌측 하단)
        BaseButton(
            onClick = onGoMapClick,
            text = goHomeButtonText,
            fontSize = 16.sp,
            modifier = Modifier
                .align(Alignment.TopStart)
                .padding(start = 30.dp, top = 30.dp)
        )
    }
}

// 미리보기
@Preview(
    showBackground = true,
    widthDp = 1280,
    heightDp = 800
)
@Composable
fun TangramTitleScreenPreview() {
    val dummyBackground = painterResource(id = R.drawable.tangram_title_background)
    TangramTitleScreen(
        gameName = "종글 탐험대",
        backgroundImagePainter = dummyBackground,
        onStartGameClick = {},
        onGoMapClick = {},
        onGameRulesClick = {},
    )
}

