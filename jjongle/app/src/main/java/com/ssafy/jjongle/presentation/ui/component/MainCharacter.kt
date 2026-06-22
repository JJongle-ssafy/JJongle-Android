package com.ssafy.jjongle.presentation.ui.component

import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.airbnb.lottie.compose.LottieAnimation
import com.airbnb.lottie.compose.LottieCompositionSpec
import com.airbnb.lottie.compose.LottieConstants
import com.airbnb.lottie.compose.animateLottieCompositionAsState
import com.airbnb.lottie.compose.rememberLottieComposition


import androidx.compose.ui.graphics.graphicsLayer

@Composable
fun MainCharacter(
    modifier: Modifier = Modifier,
    isWalking: Boolean = false,// 기본 상태는 걷지 않도록 설정
    size: Dp = 300.dp,// 크기 조절을 위한 파라미터 추가
    assetName: String = "mongi_walk.json", // 애니메이션 파일 이름
    iterations: Int = LottieConstants.IterateForever, // 반복 횟수 설정
    mirrorHorizontally: Boolean = false
) {
    val composition by rememberLottieComposition(LottieCompositionSpec.Asset(assetName))
    val progress by animateLottieCompositionAsState(
        composition = composition,
        isPlaying = isWalking,  // 🔁 상태에 따라 재생/정지
        iterations = iterations,
    )

    LottieAnimation(
        composition = composition,
        modifier = modifier
            .size(size)
            .graphicsLayer {
                scaleX = if (mirrorHorizontally) -1f else 1f
            },
        progress = { progress }, // 애니메이션 진행 상태
    )
}


