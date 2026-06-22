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
import androidx.compose.foundation.layout.BoxWithConstraints
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
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.ssafy.jjongle.R
import com.ssafy.jjongle.presentation.ui.component.MainCharacter
import com.ssafy.jjongle.presentation.viewmodel.MapViewModel
import kotlinx.coroutines.joinAll
import kotlinx.coroutines.launch
import kotlin.math.min


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
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop
        )

        BoxWithConstraints(modifier = Modifier.fillMaxSize()) {
            val mapScale = min(
                maxWidth.value / MAP_DESIGN_WIDTH_DP,
                maxHeight.value / MAP_DESIGN_HEIGHT_DP
            )

            fun scaledDp(value: Float) = (value * mapScale).dp
            val contentOriginX = (maxWidth - scaledDp(MAP_DESIGN_WIDTH_DP)) / 2
            val contentOriginY = (maxHeight - scaledDp(MAP_DESIGN_HEIGHT_DP)) / 2

            // 쫑글탐험대 표지판
            Image(
                modifier = Modifier
                    .offset(
                        x = contentOriginX + scaledDp(TANGRAM_PANEL_X),
                        y = contentOriginY + scaledDp(TANGRAM_PANEL_Y)
                    )
                    .size(width = scaledDp(MAP_PANEL_WIDTH), height = scaledDp(MAP_PANEL_HEIGHT))
                    .clickable(
                        indication = null,
                        interactionSource = remember { MutableInteractionSource() },
                        enabled = !mapState.isWalking
                    ) {
                        coroutineScope.launch {
                            viewModel.startWalking()

                            // L자 형태가 아닌, 대각선으로 이동하기 위해 joinAll 사용
                            joinAll(
                                launch { x.animateTo(TANGRAM_CHARACTER_X, animationSpeed) },
                                launch { y.animateTo(TANGRAM_CHARACTER_Y, animationSpeed) }
                            )

                            viewModel.moveCharacterTo(TANGRAM_CHARACTER_X, TANGRAM_CHARACTER_Y)
                            onNavigateToTangram()
                        }

                    },
                painter = painterResource(id = R.drawable.tangram_panel),
                contentDescription = "tangram panel",
            )


            // 쫑글OX대모험 표지판
            Image(
                modifier = Modifier
                    .offset(
                        x = contentOriginX + scaledDp(OX_PANEL_X),
                        y = contentOriginY + scaledDp(OX_PANEL_Y)
                    )
                    .size(width = scaledDp(MAP_PANEL_WIDTH), height = scaledDp(MAP_PANEL_HEIGHT))
                    .clickable(
                        indication = null,
                        interactionSource = remember { MutableInteractionSource() },
                        enabled = !mapState.isWalking
                    ) {
                        coroutineScope.launch {
                            viewModel.startWalking()

                            joinAll(
                                launch { x.animateTo(OX_CHARACTER_X, animationSpeed) },
                                launch { y.animateTo(OX_CHARACTER_Y, animationSpeed) }
                            )

                            viewModel.moveCharacterTo(OX_CHARACTER_X, OX_CHARACTER_Y)
                            onNavigateToOXGame()
                        }

                    },
                painter = painterResource(id = R.drawable.ox_panel),
                contentDescription = "ox panel",
            )


            // 마이페이지 표지판
            Image(
                modifier = Modifier
                    .offset(
                        x = contentOriginX + scaledDp(MYPAGE_PANEL_X),
                        y = contentOriginY + scaledDp(MYPAGE_PANEL_Y)
                    )
                    .size(width = scaledDp(MAP_PANEL_WIDTH), height = scaledDp(MAP_PANEL_HEIGHT))
                    .clickable(
                        indication = null,
                        interactionSource = remember { MutableInteractionSource() },
                        enabled = !mapState.isWalking
                    ) {
                        coroutineScope.launch {
                            viewModel.startWalking()

                            joinAll(
                                launch { x.animateTo(MYPAGE_CHARACTER_X, animationSpeed) },
                                launch { y.animateTo(MYPAGE_CHARACTER_Y, animationSpeed) }
                            )

                            viewModel.moveCharacterTo(MYPAGE_CHARACTER_X, MYPAGE_CHARACTER_Y)
                            onNavigateToMyPage()
                        }
                    },
                painter = painterResource(id = R.drawable.mypage_panel),
                contentDescription = "mypage panel",
            )


            // 몽이 캐릭터
            MainCharacter(
                modifier = Modifier
                    .offset(
                        x = contentOriginX + scaledDp(x.value),
                        y = contentOriginY + scaledDp(y.value)
                    ),
                isWalking = mapState.isWalking,
                assetName = "mongi_walk.json",
                size = scaledDp(MAP_CHARACTER_SIZE),
            )
        }

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

private const val MAP_DESIGN_WIDTH_DP = 1280f
private const val MAP_DESIGN_HEIGHT_DP = 800f
private const val MAP_PANEL_WIDTH = 759f
private const val MAP_PANEL_HEIGHT = 509f
private const val MAP_CHARACTER_SIZE = 300f

private const val TANGRAM_PANEL_X = 128f
private const val TANGRAM_PANEL_Y = 75f
private const val TANGRAM_CHARACTER_X = 128f
private const val TANGRAM_CHARACTER_Y = 140f

private const val OX_PANEL_X = 730f
private const val OX_PANEL_Y = 130f
private const val OX_CHARACTER_X = 730f
private const val OX_CHARACTER_Y = 180f

private const val MYPAGE_PANEL_X = 330f
private const val MYPAGE_PANEL_Y = 370f
private const val MYPAGE_CHARACTER_X = 170f
private const val MYPAGE_CHARACTER_Y = 310f


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
