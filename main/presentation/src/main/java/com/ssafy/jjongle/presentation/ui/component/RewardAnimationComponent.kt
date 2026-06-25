package com.ssafy.jjongle.common.presentation.ui.component

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.airbnb.lottie.compose.*

/**
 * RewardAnimationComponent Compose UI를 구성합니다.
 *
 * - 계층: main/presentation
 * - 책임: 상태를 표시하고 사용자 이벤트를 상위 콜백이나 ViewModel로 전달합니다.
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
