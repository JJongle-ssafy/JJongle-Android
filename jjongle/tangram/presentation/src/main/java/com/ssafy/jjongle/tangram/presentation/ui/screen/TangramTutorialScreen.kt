package com.ssafy.jjongle.tangram.presentation.ui.screen

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.ssafy.jjongle.common.presentation.ui.component.BaseButton
import com.ssafy.jjongle.common.presentation.ui.component.ResponsiveBackgroundImage
import com.ssafy.jjongle.common.presentation.ui.theme.ArchiThemeImpl
import com.ssafy.jjongle.tangram.presentation.R

@Composable
fun TangramTutorialScreen(
    onStartTutorial: () -> Unit,
) {
    var currentTutorial by remember { mutableIntStateOf(0) }

    TangramTutorialContent(
        currentTutorial = currentTutorial,
        onPrevious = { currentTutorial-- },
        onNext = { currentTutorial++ },
        onStartTutorial = onStartTutorial
    )
}

@Composable
fun TangramTutorialContent(
    currentTutorial: Int,
    onPrevious: () -> Unit,
    onNext: () -> Unit,
    onStartTutorial: () -> Unit
) {
    val safeCurrentTutorial = currentTutorial.coerceIn(TANGRAM_TUTORIAL_IMAGES.indices)

    Box(modifier = Modifier.fillMaxSize()) {
        // 배경 이미지
        ResponsiveBackgroundImage(
            painter = painterResource(id = R.drawable.tangram_tutorial_background),
            contentDescription = "Tangram Tutorial Background",
            modifier = Modifier.fillMaxSize()
        )

        // 가운데 튜토리얼 이미지
        Image(
            painter = painterResource(id = TANGRAM_TUTORIAL_IMAGES[safeCurrentTutorial]),
            contentDescription = "Tutorial ${safeCurrentTutorial + 1}",
            modifier = Modifier
                .align(Alignment.Center)
                .offset(y = 50.dp)
//                .size(400.dp)
            ,
            contentScale = ContentScale.Fit
        )

        // 이전 버튼 (화면 왼쪽, 세로 중앙)
        if (safeCurrentTutorial > 0) {
            Image(
                painter = painterResource(id = R.drawable.previous_btn),
                contentDescription = "이전",
                modifier = Modifier
                    .align(Alignment.CenterStart)
                    .size(80.dp)
                    .padding(start = 24.dp)
                    .clickable { onPrevious() },
                contentScale = ContentScale.Fit
            )
        }

        // 다음/시작 버튼
        if (safeCurrentTutorial < TANGRAM_TUTORIAL_IMAGES.size - 1) {
            // 다음 버튼 (화면 오른쪽, 세로 중앙)
            Image(
                painter = painterResource(id = R.drawable.next_btn),
                contentDescription = "다음",
                modifier = Modifier
                    .align(Alignment.CenterEnd)
                    .size(80.dp)
                    .padding(end = 24.dp)
                    .clickable { onNext() },
                contentScale = ContentScale.Fit
            )
        } else {
            // 마지막 페이지에서는 시작 버튼 (오른쪽 아래)
            BaseButton(
                onClick = onStartTutorial,
                text = "탐험 떠나기",
                textStyle = ArchiThemeImpl.typeScale.textStrongM,
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .padding(end = 24.dp, bottom = 24.dp)
            )
        }
    }
}

private val TANGRAM_TUTORIAL_IMAGES = listOf(
    R.drawable.tangram_tutorial1,
    R.drawable.tangram_tutorial2,
    R.drawable.tangram_tutorial3,
    R.drawable.tangram_tutorial4
)
