package com.ssafy.jjongle.presentation.ui.screen

import android.util.Log
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.EaseInOut
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.ssafy.jjongle.R
import com.ssafy.jjongle.presentation.ui.component.MainCharacter
import com.ssafy.jjongle.presentation.viewmodel.MapViewModel
import kotlinx.coroutines.joinAll
import kotlinx.coroutines.launch


// Map 화면 구성
@Composable
fun MapScreen(
    onNavigateToLogin: () -> Unit,
    onNavigateToOXGame: () -> Unit,
    onNavigateToTangram: () -> Unit,
    onNavigateToMyPage: () -> Unit,
    viewModel: MapViewModel = viewModel(),
) {
    val mapState by viewModel.mapState.collectAsState()

    // 애니메이션을 위한 로컬 상태값들 (ViewModel의 현재 위치로 초기화)
    val x = remember(mapState.characterX) { Animatable(mapState.characterX) }
    val y = remember(mapState.characterY) { Animatable(mapState.characterY) }
    val coroutineScope = rememberCoroutineScope()
    val animationSpeed = tween<Float>(
        durationMillis = 3000,
        easing = EaseInOut
    )

    LaunchedEffect(Unit) {
        Log.d("Map", "CurrentPosition: ${mapState.characterX}, ${mapState.characterY}")
    }

    Box(
        modifier = Modifier.fillMaxSize()

    ) {
        Image(
            painter = painterResource(id = R.drawable.main_map),
            contentDescription = "Map Background",
        )

        // 쫑글탐험대 표지판
        Image(
            modifier = Modifier
                .offset(x = 128.dp, y = 75.dp)
                .clickable(
                    indication = null,
                    interactionSource = remember { MutableInteractionSource() },
                    enabled = !mapState.isWalking
                ) {
                    coroutineScope.launch {
                        viewModel.startWalking()

                        // L자 형태가 아닌, 대각선으로 이동하기 위해 joinAll 사용
                        joinAll(
                            launch { x.animateTo(128f, animationSpeed) },
                            launch { y.animateTo(140f, animationSpeed) }
                        )

                        viewModel.moveCharacterTo(128f, 140f)
                        onNavigateToTangram()
                    }

                },
            painter = painterResource(id = R.drawable.tangram_panel),
            contentDescription = "tangram panel",
        )


        // 쫑글OX대모험 표지판
        Image(
            modifier = Modifier
                .offset(x = 730.dp, y = 130.dp)
                .clickable(
                    indication = null,
                    interactionSource = remember { MutableInteractionSource() },
                    enabled = !mapState.isWalking
                ) {
                    coroutineScope.launch {
                        viewModel.startWalking()

                        joinAll(
                            launch { x.animateTo(730f, animationSpeed) },
                            launch { y.animateTo(180f, animationSpeed) }
                        )

                        viewModel.moveCharacterTo(730f, 180f)
                        onNavigateToOXGame()
                    }

                },
            painter = painterResource(id = R.drawable.ox_panel),
            contentDescription = "ox panel",
        )


        // 마이페이지 표지판
        Image(
            modifier = Modifier
                .offset(x = 330.dp, y = 370.dp)
                .clickable(
                    indication = null,
                    interactionSource = remember { MutableInteractionSource() },
                    enabled = !mapState.isWalking
                ) {
                    coroutineScope.launch {
                        viewModel.startWalking()

                        joinAll(
                            launch { x.animateTo(170f, animationSpeed) },
                            launch { y.animateTo(310f, animationSpeed) }
                        )

                        viewModel.moveCharacterTo(170f, 310f)
                        onNavigateToMyPage()
                    }
                },
            painter = painterResource(id = R.drawable.mypage_panel),
            contentDescription = "mypage panel",
        )


        // 몽이 캐릭터
        MainCharacter(
            modifier = Modifier
                .offset(x.value.dp, y.value.dp),
            isWalking = mapState.isWalking,
            assetName = "mongi_walk.json",
            size = 300.dp,
        )

        // BGM on/off 버튼
        Box(
            modifier = Modifier
                .align(Alignment.TopEnd)
                .padding(top = 16.dp, end = 16.dp)
                .size(48.dp)
                .background(
                    color = Color.White.copy(alpha = 0.4f),
                    shape = CircleShape
                )
                .clickable(
                    indication = null,
                    interactionSource = remember { MutableInteractionSource() }
                ) {
                    viewModel.toggleBgm()
                },
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = if (mapState.isBgmOn) "🎧" else "🔇",
                fontSize = 20.sp,
                color = Color.White
            )
        }

    }
}


//@Preview(
//    showBackground = true,
//    device = Devices.TABLET
//)
//@Composable
//fun MapScreenPreview() {
//    JjongleTheme {
//        MapScreen(
//            onNavigateToLogin = {},
//            onNavigateToOXGame = {},
//            onNavigateToTangram = {},
//            onNavigateToProfile = {
//
//            }
//        )
//    }
//}