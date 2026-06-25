package com.ssafy.jjongle.oxgame.presentation.ui.screen

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.ssafy.jjongle.oxgame.presentation.R

/**
 * OX 게임의 OXTutorial 화면을 렌더링하는 Compose 진입점입니다.
 *
 * 전달받은 상태를 화면 요소로 배치하고, 사용자의 주요 액션은 콜백이나 ViewModel Intent로 넘겨 화면과 상태 변경 책임을 분리합니다.
 */
@Composable
fun OXTutorialScreen(
    onStartQuiz: () -> Unit,
) {
    val page = remember { mutableIntStateOf(0) }

    OXTutorialContent(
        page = page.intValue,
        onPrevious = { page.intValue -= 1 },
        onNext = { page.intValue += 1 },
        onStartQuiz = onStartQuiz
    )
}

@Composable
fun OXTutorialContent(
    page: Int,
    onPrevious: () -> Unit,
    onNext: () -> Unit,
    onStartQuiz: () -> Unit,
    modifier: Modifier = Modifier
) {
    val safePage = page.coerceIn(OX_TUTORIAL_IMAGES.indices)

    Box(
        modifier = modifier.fillMaxSize()
    ) {
        OXFeatureBackgroundImage(R.drawable.ox_tutorial_background)

        // 중앙 튜토리얼 이미지
        Image(
            painter = painterResource(id = OX_TUTORIAL_IMAGES[safePage]),
            contentDescription = "tutorial step",
            modifier = Modifier
                .align(Alignment.Center)
                .offset(y = 70.dp)
//                .padding(horizontal = 24.dp)
            ,
            contentScale = ContentScale.Fit
        )

        // 이전 버튼 (왼쪽 중앙)
        if (safePage > 0) {
            Image(
                painter = painterResource(id = R.drawable.previous_btn),
                contentDescription = "previous",
                modifier = Modifier
                    .align(Alignment.CenterStart)
                    .size(80.dp)
                    .padding(start = 24.dp)
                    .clickable { onPrevious() },
                contentScale = ContentScale.Fit
            )
        }

        // 다음 버튼 (오른쪽 중앙)
        if (safePage < OX_TUTORIAL_IMAGES.lastIndex) {
            Image(
                painter = painterResource(id = R.drawable.next_btn),
                contentDescription = "next",
                modifier = Modifier
                    .align(Alignment.CenterEnd)
                    .size(80.dp)
                    .padding(end = 24.dp)
                    .clickable { onNext() },
                contentScale = ContentScale.Fit
            )
        } else {
            // 마지막 페이지: 우하단 시작 버튼
            OXFeatureButton(
                text = "모험 떠나기",
                onClick = onStartQuiz,
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .padding(end = 24.dp, bottom = 24.dp)
            )
        }
    }
}

private val OX_TUTORIAL_IMAGES = listOf(
    R.drawable.ox_tutorial_1,
    R.drawable.ox_tutorial_2,
    R.drawable.ox_tutorial_3,
    R.drawable.ox_tutorial_4,
)
