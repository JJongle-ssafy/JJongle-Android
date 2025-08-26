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
fun OXGameTitleScreen(
    gameName: String,
    backgroundImagePainter: Painter,
    onStartGameClick: () -> Unit,
    onGoMapClick: () -> Unit,
    onGameRulesClick: () -> Unit,
    modifier: Modifier = Modifier,
    startGameButtonText: String = "친구들과 문제를 풀어볼까요?",
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


        // 처음으로 돌아가기 버튼
        BaseButton( // 호출 이름을 BaseButton으로 변경
            onClick = onGoMapClick,
            text = goHomeButtonText,
            fontSize = 18.sp,
            modifier = Modifier
                .align(Alignment.BottomStart)
                .padding(start = 24.dp, bottom = 24.dp)
        )

        // 우측 상단 버튼들
        Column(
            modifier = Modifier
                .align(Alignment.TopEnd)
                .padding(top = 24.dp, end = 24.dp)
                .width(400.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // 게임 시작 버튼
            BaseButton( // 호출 이름을 BaseButton으로 변경
                onClick = onStartGameClick,
                text = startGameButtonText,
                fontSize = 24.sp,
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(16.dp))
            BaseButton( // 호출 이름을 BaseButton으로 변경
                onClick = onGameRulesClick,
                text = gameRulesButtonText,
                fontSize = 18.sp,
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}

// 미리보기 (Preview) 부분도 동일하게 BaseButton으로 변경 필요 (만약 아직 안했다면)
@Preview(
    showBackground = true,
    widthDp = 1280,
    heightDp = 800
)
@Composable
fun TitleScreenPreview() {
    val dummyBackground = painterResource(id = R.drawable.ox_title_background)
    OXGameTitleScreen(
        gameName = "OX 게임",
        backgroundImagePainter = dummyBackground,
        onStartGameClick = {},
        onGoMapClick = {},
        onGameRulesClick = {},
    )
}