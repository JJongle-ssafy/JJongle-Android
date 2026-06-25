package com.ssafy.jjongle.common.presentation.ui.component

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.airbnb.lottie.compose.*

/**
 * Reward Animation Component는 메인에서 반복되는 계산이나 변환을 담당합니다.
 *
 * 호출부가 세부 구현을 직접 갖지 않도록 작은 공개 함수/값으로 분리합니다.
 */
@Composable
fun RewardAnimationComponent(
    modifier: Modifier = Modifier,
    isVisible: Boolean = false,
    onAnimationFinished: () -> Unit = {}
) {
    if (!isVisible) return

    val composition by rememberLottieComposition(
        LottieCompositionSpec.Asset("Rewards.json")
    )
    
    val progress by animateLottieCompositionAsState(
        composition = composition,
        iterations = 1,
        isPlaying = true
    )

    LaunchedEffect(progress) {
        if (progress >= 1f) {
            onAnimationFinished()
        }
    }

    Box(
        modifier = modifier,
        contentAlignment = Alignment.Center
    ) {
        LottieAnimation(
            composition = composition,
            progress = { progress },
            modifier = Modifier.size(200.dp)
        )
    }
}
