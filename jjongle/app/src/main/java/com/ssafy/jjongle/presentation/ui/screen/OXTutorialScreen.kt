package com.ssafy.jjongle.presentation.ui.screen

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.ssafy.jjongle.R
import com.ssafy.jjongle.presentation.ui.component.BaseButton
import com.ssafy.jjongle.presentation.viewmodel.TutorialViewModel
import com.ssafy.jjongle.util.AudioPlayer
import kotlinx.coroutines.delay

@Composable
fun OXTutorialScreen(
    onStartQuiz: () -> Unit,
) {
    val page = remember { mutableIntStateOf(0) }
    val contentList = listOf(
        "넓은 공간을 준비해주세요. 주위 물건과 친구에게 부딪히지 않게 자리를 넓혀요!",
        "귀는? 쫑긋! 눈은? 반짝! 문제가 나오면 집중해서 들어주세요",
        "O는 왼쪽! X는 오른쪽! 논란 시간막대가 다 줄어들면 움직이지 말고 제자리에 있어요!!",
        "지혜의 미로 속 지식 여행, 준비됐나요?? 친구들과 함께 OX대모험을 떠나볼까요?"
    )
    val pages = listOf(
        R.drawable.ox_tutorial_1,
        R.drawable.ox_tutorial_2,
        R.drawable.ox_tutorial_3,
        R.drawable.ox_tutorial_4,
    )

    val context = LocalContext.current
    val audioPlayer = remember { AudioPlayer(context) }
    val tutorialViewModel: TutorialViewModel = hiltViewModel()

    Box(modifier = Modifier.fillMaxSize()) {
        // 배경
        Image(
            painter = painterResource(id = R.drawable.ox_tutorial_background),
            contentDescription = "tutorial background",
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop
        )

        // 중앙 튜토리얼 이미지
        Image(
            painter = painterResource(id = pages[page.intValue]),
            contentDescription = "tutorial step",
            modifier = Modifier
                .align(Alignment.Center)
                .offset(y = 70.dp)
//                .padding(horizontal = 24.dp)
            ,
            contentScale = ContentScale.Fit
        )

        // 이전 버튼 (왼쪽 중앙)
        if (page.intValue > 0) {
            Image(
                painter = painterResource(id = R.drawable.previous_btn),
                contentDescription = "previous",
                modifier = Modifier
                    .align(Alignment.CenterStart)
                    .size(80.dp)
                    .padding(start = 24.dp)
                    .clickable { page.intValue -= 1 },
                contentScale = ContentScale.Fit
            )
        }

        // 다음 버튼 (오른쪽 중앙)
        if (page.intValue < pages.lastIndex) {
            Image(
                painter = painterResource(id = R.drawable.next_btn),
                contentDescription = "next",
                modifier = Modifier
                    .align(Alignment.CenterEnd)
                    .size(80.dp)
                    .padding(end = 24.dp)
                    .clickable { page.intValue += 1 },
                contentScale = ContentScale.Fit
            )
        } else {
            // 마지막 페이지: 우하단 시작 버튼
            BaseButton(
                text = "모험 떠나기",
                onClick = onStartQuiz,
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .padding(end = 24.dp, bottom = 24.dp)
            )
        }
    }

    // 페이지 변경 시 TTS 재생
    LaunchedEffect(page.intValue) {
        audioPlayer.stop()
        val text = contentList.getOrNull(page.intValue)
        if (text != null) {
            val result = tutorialViewModel.generateTTS(text)
            if (result.isSuccess) {
                audioPlayer.playTTS(result.getOrNull()!!)
                delay(100)
            }
        }
    }
}


