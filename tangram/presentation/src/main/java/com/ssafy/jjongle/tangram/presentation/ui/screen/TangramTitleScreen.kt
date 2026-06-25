package com.ssafy.jjongle.tangram.presentation.ui.screen

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
import com.ssafy.jjongle.common.presentation.ui.layout.SystemBackgroundImageEffect
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.ssafy.jjongle.tangram.presentation.R
import com.ssafy.jjongle.common.presentation.ui.component.BaseButton
import com.ssafy.jjongle.common.presentation.ui.theme.ArchiThemeImpl

/**
 * 탱그램의 Tangram Title 화면을 렌더링하는 Compose 진입점입니다.
 *
 * 전달받은 상태를 화면 요소로 배치하고, 사용자의 주요 액션은 콜백이나 ViewModel Intent로 넘겨 화면과 상태 변경 책임을 분리합니다.
 */
@Composable
fun TangramTitleScreen(
    gameName: String,
    backgroundImageRes: Int,
    onStartGameClick: () -> Unit,
    onGoMapClick: () -> Unit,
    onGameRulesClick: () -> Unit,
    modifier: Modifier = Modifier,
    startGameButtonText: String = "탐험을 떠나볼까요?",
    goHomeButtonText: String = "처음으로 돌아가기",
    gameRulesButtonText: String = "놀이 설명"
) {
    SystemBackgroundImageEffect(backgroundImageRes)

    Box(
        modifier = modifier.fillMaxSize()
    ) {


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
                textStyle = ArchiThemeImpl.typeScale.textStrongL,
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(16.dp))
            BaseButton(
                onClick = onGameRulesClick,
                text = gameRulesButtonText,
                textStyle = ArchiThemeImpl.typeScale.textStrongM,
                modifier = Modifier.fillMaxWidth()
            )
        }

        // 처음으로 버튼 (좌측 하단)
        BaseButton(
            onClick = onGoMapClick,
            text = goHomeButtonText,
            textStyle = ArchiThemeImpl.typeScale.textStrongM,
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
    TangramTitleScreen(
        gameName = "쫑글 탐험대",
        backgroundImageRes = R.drawable.tangram_title_background,
        onStartGameClick = {},
        onGoMapClick = {},
        onGameRulesClick = {},
    )
}
