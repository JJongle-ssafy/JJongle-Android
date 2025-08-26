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
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.ssafy.jjongle.R
import com.ssafy.jjongle.presentation.ui.component.BaseButton
import com.ssafy.jjongle.presentation.viewmodel.TutorialViewModel
import com.ssafy.jjongle.util.AudioPlayer
import kotlinx.coroutines.delay

@Composable
fun TangramTutorialScreen(
    onStartTutorial: () -> Unit,
) {
    var currentTutorial by remember { mutableIntStateOf(0) }
    val contentList = listOf(
        "깨끗한 책상을 준비해주세요! 칠교 조각들이 흩어지지 않게 가지런히 놓아주세요!",
        "반사 거울을 끼워주세요! 거울이 빠지지 않게 잘 꽂아주세요.",
        "태블릿 각도를 맞춰주세요 칠교 조각들이 화면 안에 쏙 들어오게 해주세요!!",
        "이제 준비는 끝났어요!! 동물 친구들을 만나러 정글로 떠나볼까요?"
    )

    val tutorialImages = listOf(
        R.drawable.tangram_tutorial1,
        R.drawable.tangram_tutorial2,
        R.drawable.tangram_tutorial3,
        R.drawable.tangram_tutorial4
    )

    val context = LocalContext.current
    val audioPlayer = remember { AudioPlayer(context) }
    val tutorialViewModel: TutorialViewModel = hiltViewModel()

    Box(modifier = Modifier.fillMaxSize()) {
        // 배경 이미지
        Image(
            painter = painterResource(id = R.drawable.tangram_tutorial_background),
            contentDescription = "Tangram Tutorial Background",
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop
        )

        // 가운데 튜토리얼 이미지
        Image(
            painter = painterResource(id = tutorialImages[currentTutorial]),
            contentDescription = "Tutorial ${currentTutorial + 1}",
            modifier = Modifier
                .align(Alignment.Center)
                .offset(y = 50.dp)
//                .size(400.dp)
            ,
            contentScale = ContentScale.Fit
        )

        // 이전 버튼 (화면 왼쪽, 세로 중앙)
        if (currentTutorial > 0) {
            Image(
                painter = painterResource(id = R.drawable.previous_btn),
                contentDescription = "이전",
                modifier = Modifier
                    .align(Alignment.CenterStart)
                    .size(80.dp)
                    .padding(start = 24.dp)
                    .clickable { currentTutorial-- },
                contentScale = ContentScale.Fit
            )
        }

        // 다음/시작 버튼
        if (currentTutorial < tutorialImages.size - 1) {
            // 다음 버튼 (화면 오른쪽, 세로 중앙)
            Image(
                painter = painterResource(id = R.drawable.next_btn),
                contentDescription = "다음",
                modifier = Modifier
                    .align(Alignment.CenterEnd)
                    .size(80.dp)
                    .padding(end = 24.dp)
                    .clickable { currentTutorial++ },
                contentScale = ContentScale.Fit
            )
        } else {
            // 마지막 페이지에서는 시작 버튼 (오른쪽 아래)
            BaseButton(
                onClick = onStartTutorial,
                text = "탐험 떠나기",
                fontSize = 18.sp,
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .padding(end = 24.dp, bottom = 24.dp)
            )
        }
    }

    // 페이지 변경 시 TTS 재생
    LaunchedEffect(currentTutorial) {
        audioPlayer.stop()
        val text = contentList.getOrNull(currentTutorial)
        if (text != null) {
            val result = tutorialViewModel.generateTTS(text)
            if (result.isSuccess) {
                audioPlayer.playTTS(result.getOrNull()!!)
                delay(100)
            }
        }
    }
}