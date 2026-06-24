package com.ssafy.jjongle.oxgame.presentation.ui.screen

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
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.ssafy.jjongle.common.presentation.ui.theme.ArchiThemeImpl
import com.ssafy.jjongle.oxgame.presentation.R

@Composable
fun OXGameTitleScreen(
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
        OXFeatureBackgroundImage(R.drawable.ox_title_background)


        // 처음으로 돌아가기 버튼
        OXFeatureButton(
            onClick = onGoMapClick,
            text = goHomeButtonText,
            textStyle = ArchiThemeImpl.typeScale.textStrongM,
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
            OXFeatureButton(
                onClick = onStartGameClick,
                text = startGameButtonText,
                textStyle = ArchiThemeImpl.typeScale.textStrongL,
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(16.dp))
            OXFeatureButton(
                onClick = onGameRulesClick,
                text = gameRulesButtonText,
                textStyle = ArchiThemeImpl.typeScale.textStrongM,
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}

@Preview(
    showBackground = true,
    widthDp = 1280,
    heightDp = 800
)
@Composable
fun TitleScreenPreview() {
    OXGameTitleScreen(
        onStartGameClick = {},
        onGoMapClick = {},
        onGameRulesClick = {},
    )
}
