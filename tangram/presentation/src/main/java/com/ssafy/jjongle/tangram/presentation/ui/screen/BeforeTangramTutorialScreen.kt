package com.ssafy.jjongle.tangram.presentation.ui.screen

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.ssafy.jjongle.tangram.presentation.R
import com.ssafy.jjongle.common.presentation.ui.component.BaseButton
import com.ssafy.jjongle.common.presentation.ui.component.ResponsiveBackgroundImage

/**
 * BeforeTangramTutorialScreen Compose UI를 구성합니다.
 *
 * - 계층: tangram/presentation
 * - 책임: 상태를 표시하고 사용자 이벤트를 상위 콜백이나 ViewModel로 전달합니다.
 */
@Composable
fun BeforeTangramTutorialScreen(
    onStartTutorial: () -> Unit,
    onSkipTutorial: () -> Unit
) {
    Box(modifier = Modifier.fillMaxSize()) {
        ResponsiveBackgroundImage(
            painter = painterResource(id = R.drawable.before_tangram_tutorial),
            contentDescription = "Before Tangram Tutorial Background",
            modifier = Modifier.fillMaxSize()
        )
        
        Column(
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(bottom = 60.dp, end = 40.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            BaseButton(
                onClick = onSkipTutorial,
                text = "바로 시작하기",
                modifier = Modifier.width(300.dp)
            )
            
            BaseButton(
                onClick = onStartTutorial,
                text = "다음",
                modifier = Modifier.width(300.dp)
            )
        }
    }
}
