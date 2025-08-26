package com.ssafy.jjongle.presentation.ui.screen

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.ssafy.jjongle.R
import com.ssafy.jjongle.presentation.ui.component.BaseButton
import com.ssafy.jjongle.presentation.viewmodel.IntroViewModel
import com.ssafy.jjongle.util.AudioPlayer
import kotlinx.coroutines.delay

data class IntroPage(
    val title: String,
    val content: String,
    val backgroundImage: Int,
    val profileImage: Int? = null
)

@Composable
fun IntroScreen(
    gameName: String,
    pages: List<IntroPage>,
    onStartGameClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    var currentPage by remember { mutableIntStateOf(0) }

    if (pages.isEmpty()) return

    val currentPageData = pages[currentPage]

    Box(
        modifier = modifier.fillMaxSize()
    ) {
        // TTS 재생기 (페이지 변경 시 자동 낭독)
        val context = androidx.compose.ui.platform.LocalContext.current
        val audioPlayer = remember { AudioPlayer(context) }
        val introViewModel: IntroViewModel = hiltViewModel()

        // 배경 이미지
        Image(
            painter = painterResource(id = currentPageData.backgroundImage),
            contentDescription = "$gameName 배경 이미지",
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop
        )


        // 중앙 스크롤 영역
        Box(
            modifier = Modifier
                .align(Alignment.Center)
                .fillMaxWidth()
                .padding(horizontal = 32.dp),
            contentAlignment = Alignment.Center
        ) {

            // 텍스트 내용 (스크롤 위에 오버레이)
            Text(
                text = currentPageData.content,
                fontSize = 30.sp,
                lineHeight = 44.sp,
                textAlign = TextAlign.Center,
                color = Color.Black,
                modifier = Modifier
                    .width(600.dp)
                    .offset(y = 60.dp)
                    .padding(horizontal = 32.dp)
                    .padding(vertical = 16.dp),
                softWrap = true,
                maxLines = 10
            )
        }

        // 페이지 변경 시 TTS로 설명 읽어주기
        LaunchedEffect(currentPage) {
            val result = introViewModel.generateTTS(currentPageData.content)
            if (result.isSuccess) {
                audioPlayer.playTTS(result.getOrNull()!!)
                delay(100)
            }
        }

        // 이전 버튼 (화면 왼쪽, 세로 중앙)
        if (currentPage > 0) {
            Image(
                painter = painterResource(id = R.drawable.previous_btn),
                contentDescription = "이전",
                modifier = Modifier
                    .align(Alignment.CenterStart)
                    .size(80.dp)
                    .padding(start = 24.dp)
                    .clickable { currentPage-- },
                contentScale = ContentScale.Fit
            )
        }

        // 다음/시작 버튼
        if (currentPage < pages.size - 1) {
            // 다음 버튼 (화면 오른쪽, 세로 중앙)
            Image(
                painter = painterResource(id = R.drawable.next_btn),
                contentDescription = "다음",
                modifier = Modifier
                    .align(Alignment.CenterEnd)
                    .size(80.dp)
                    .padding(end = 24.dp)
                    .clickable { currentPage++ },
                contentScale = ContentScale.Fit
            )
        } else {
            // 마지막 페이지에서는 시작 버튼 (오른쪽 아래)
            BaseButton(
                onClick = onStartGameClick,
                text = "모험 떠나기",
                fontSize = 18.sp,
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .padding(end = 24.dp, bottom = 24.dp)
            )
        }
    }
}

// 미리보기
@Preview(
    showBackground = true,
    widthDp = 1280,
    heightDp = 800
)
@Composable
fun IntroScreenPreview() {
    val oxGamePages = listOf(
        IntroPage(
            title = "종글 O/X 대모험",
            content = "정글 어딘가에, 지혜의 미로라 불리는 신비한 장소가 있어요. 이 관문은 오직 O 또는 X로만 대답할 수 있는 문제를 풀어야만 열리고, 모두의 센스와 상식, 그리고 웃음소리가 있어야만 앞으로 나아갈 수 있죠!",
            backgroundImage = R.drawable.ox_intro_background
        ),
        IntroPage(
            title = "종글 O/X 대모험",
            content = "함께 정글의 지식 여정을 떠날 준비가 됐나요? O는 왼쪽! X는 오른쪽! 친구들과 함께 종글 O/X 대모험을 시작해볼까요?",
            backgroundImage = R.drawable.ox_intro_background
        )
    )

    IntroScreen(
        gameName = "종글 O/X 대모험",
        pages = oxGamePages,
        onStartGameClick = {},
    )
}

// 게임별 페이지 데이터
object IntroPages {
    val oxGamePages = listOf(
        IntroPage(
            title = "종글 O/X 대모험",
            content = "정글 어딘가에, 지혜의 미로라 불리는 신비한 장소가 있어요. 이 관문은 오직 O 또는 X로만 대답할 수 있는 문제를 풀어야만 열리고, 모두의 센스와 상식, 그리고 웃음소리가 있어야만 앞으로 나아갈 수 있죠!",
            backgroundImage = R.drawable.ox_intro_background
        ),
        IntroPage(
            title = "종글 O/X 대모험",
            content = "함께 정글의 지식 여정을 떠날 준비가 됐나요? O는 왼쪽! X는 오른쪽! 친구들과 함께 종글 O/X 대모험을 시작해볼까요?",
            backgroundImage = R.drawable.ox_intro_background
        )
    )

    val tangramGamePages = listOf(
        IntroPage(
            title = "종글 탐험대",
            content = "깊은 정글 속, 누구도 쉽게 찾을 수 없는 신비한 비밀의 신전이 있습니다. 전설에 따르면, 이 신전은 숲의 요정이 만든 신성한 장소로, 신전으로 향하는 길 곳곳에는 정글을 수호하던 동물 친구들이 오랜 시간 잠들어 있다고 해요.",
            backgroundImage = R.drawable.tangram_intro_background
        ),
        IntroPage(
            title = "종글 탐험대",
            content = "그들을 깨우는 유일한 방법은, 고대의 석판인 '칠교 조각'을 맞추는 것! 퍼즐을 해결할 때마다 숨겨진 동물 친구들이 하나씩 깨어나고, 탐험대는 점점 신전의 비밀에 가까워집니다. 자, 이제 우리 함께 모험을 떠나볼까요?",
            backgroundImage = R.drawable.tangram_intro_background
        )
    )
}