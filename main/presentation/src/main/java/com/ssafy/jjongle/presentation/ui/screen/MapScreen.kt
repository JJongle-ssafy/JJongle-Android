package com.ssafy.jjongle.presentation.ui.screen

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
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.ssafy.jjongle.common.presentation.ui.component.ArchiText
import com.ssafy.jjongle.common.presentation.ui.component.MainCharacter
import com.ssafy.jjongle.common.presentation.ui.layout.SystemBackgroundImageEffect
import com.ssafy.jjongle.common.presentation.ui.layout.calculateFillBoundsBackgroundLayout
import com.ssafy.jjongle.common.presentation.ui.theme.ArchiThemeImpl
import com.ssafy.jjongle.main.presentation.R
import com.ssafy.jjongle.presentation.viewmodel.MapIntent
import com.ssafy.jjongle.presentation.viewmodel.MapViewModel
import kotlinx.coroutines.joinAll
import kotlinx.coroutines.launch


// Map 화면 구성

/**
 * MapScreen Compose UI를 구성합니다.
 *
 * - 계층: main/presentation
 * - 책임: 상태를 표시하고 사용자 이벤트를 상위 콜백이나 ViewModel로 전달합니다.
 */
@Composable
fun MapScreen(
    onNavigateToLogin: () -> Unit,
    onNavigateToOXGame: () -> Unit,
    onNavigateToTangram: () -> Unit,
    onNavigateToMyPage: () -> Unit,
    viewModel: MapViewModel = viewModel(),
) {
    val mapState by viewModel.uiState.collectAsStateWithLifecycle()

    LaunchedEffect(Unit) {
        viewModel.onInitialContentDisplayed()
    }

    DisposableEffect(viewModel) {
        onDispose { viewModel.onPageLeaving() }
    }

    // 애니메이션을 위한 로컬 상태값들 (ViewModel의 현재 위치로 초기화)
    val x = remember(mapState.characterX) { Animatable(mapState.characterX) }
    val y = remember(mapState.characterY) { Animatable(mapState.characterY) }
    val coroutineScope = rememberCoroutineScope()
    val animationSpeed = tween<Float>(
        durationMillis = 3000,
        easing = EaseInOut
    )

    MapContent(
        characterX = x.value,
        characterY = y.value,
        characterTargetX = x.targetValue,
        isWalking = mapState.isWalking,
        isBgmOn = mapState.isBgmOn,
        onTangramPanelClick = {
            coroutineScope.launch {
                var success = false
                try {
                    viewModel.onIntent(MapIntent.StartWalking)

                    // L자 형태가 아닌, 대각선으로 이동하기 위해 joinAll 사용
                    joinAll(
                        launch { x.animateTo(TANGRAM_CHARACTER_X, animationSpeed) },
                        launch { y.animateTo(TANGRAM_CHARACTER_Y, animationSpeed) }
                    )

                    viewModel.onIntent(MapIntent.MoveCharacterTo(TANGRAM_CHARACTER_X, TANGRAM_CHARACTER_Y))
                    success = true
                    onNavigateToTangram()
                } finally {
                    if (!success) {
                        viewModel.onIntent(MapIntent.MoveCharacterTo(x.value, y.value))
                    }
                }
            }
        },
        onOXPanelClick = {
            coroutineScope.launch {
                var success = false
                try {
                    viewModel.onIntent(MapIntent.StartWalking)

                    joinAll(
                        launch { x.animateTo(OX_CHARACTER_X, animationSpeed) },
                        launch { y.animateTo(OX_CHARACTER_Y, animationSpeed) }
                    )

                    viewModel.onIntent(MapIntent.MoveCharacterTo(OX_CHARACTER_X, OX_CHARACTER_Y))
                    success = true
                    onNavigateToOXGame()
                } finally {
                    if (!success) {
                        viewModel.onIntent(MapIntent.MoveCharacterTo(x.value, y.value))
                    }
                }
            }
        },
        onMypagePanelClick = {
            coroutineScope.launch {
                var success = false
                try {
                    viewModel.onIntent(MapIntent.StartWalking)

                    joinAll(
                        launch { x.animateTo(MYPAGE_CHARACTER_X, animationSpeed) },
                        launch { y.animateTo(MYPAGE_CHARACTER_Y, animationSpeed) }
                    )

                    viewModel.onIntent(MapIntent.MoveCharacterTo(MYPAGE_CHARACTER_X, MYPAGE_CHARACTER_Y))
                    success = true
                    onNavigateToMyPage()
                } finally {
                    if (!success) {
                        viewModel.onIntent(MapIntent.MoveCharacterTo(x.value, y.value))
                    }
                }
            }
        },
        onBgmClick = { viewModel.onIntent(MapIntent.ToggleBgm) }
    )
}

@Composable
fun MapContent(
    characterX: Float,
    characterY: Float,
    characterTargetX: Float,
    isWalking: Boolean,
    isBgmOn: Boolean,
    onTangramPanelClick: () -> Unit,
    onOXPanelClick: () -> Unit,
    onMypagePanelClick: () -> Unit,
    onBgmClick: () -> Unit
) {
    SystemBackgroundImageEffect(R.drawable.main_map)

    Box(
        modifier = Modifier.fillMaxSize()

    ) {

        BoxWithConstraints(modifier = Modifier.fillMaxSize()) {
            val backgroundLayout = calculateFillBoundsBackgroundLayout(
                containerWidth = maxWidth.value,
                containerHeight = maxHeight.value,
                imageWidth = MAP_BACKGROUND_WIDTH_PX,
                imageHeight = MAP_BACKGROUND_HEIGHT_PX
            )

            // 쫑글탐험대 표지판
            Image(
                modifier = Modifier
                    .offset(
                        x = backgroundLayout.x(TANGRAM_PANEL_X).dp,
                        y = backgroundLayout.y(TANGRAM_PANEL_Y).dp
                    )
                    .size(
                        width = backgroundLayout.scale(MAP_PANEL_WIDTH).dp,
                        height = backgroundLayout.scale(MAP_PANEL_HEIGHT).dp
                    )
                    .testTag("map_tangram_panel")
                    .clickable(
                        indication = null,
                        interactionSource = remember { MutableInteractionSource() },
                        enabled = !isWalking
                    ) { onTangramPanelClick() },
                painter = painterResource(id = R.drawable.tangram_panel),
                contentDescription = "tangram panel",
            )


            // 쫑글OX대모험 표지판
            Image(
                modifier = Modifier
                    .offset(
                        x = backgroundLayout.x(OX_PANEL_X).dp,
                        y = backgroundLayout.y(OX_PANEL_Y).dp
                    )
                    .size(
                        width = backgroundLayout.scale(MAP_PANEL_WIDTH).dp,
                        height = backgroundLayout.scale(MAP_PANEL_HEIGHT).dp
                    )
                    .testTag("map_ox_panel")
                    .clickable(
                        indication = null,
                        interactionSource = remember { MutableInteractionSource() },
                        enabled = !isWalking
                    ) { onOXPanelClick() },
                painter = painterResource(id = R.drawable.ox_panel),
                contentDescription = "ox panel",
            )


            // 마이페이지 표지판
            Image(
                modifier = Modifier
                    .offset(
                        x = backgroundLayout.x(MYPAGE_PANEL_X).dp,
                        y = backgroundLayout.y(MYPAGE_PANEL_Y).dp
                    )
                    .size(
                        width = backgroundLayout.scale(MAP_PANEL_WIDTH).dp,
                        height = backgroundLayout.scale(MAP_PANEL_HEIGHT).dp
                    )
                    .testTag("map_mypage_panel")
                    .clickable(
                        indication = null,
                        interactionSource = remember { MutableInteractionSource() },
                        enabled = !isWalking
                    ) { onMypagePanelClick() },
                painter = painterResource(id = R.drawable.mypage_panel),
                contentDescription = "mypage panel",
            )


            // 몽이 캐릭터
            MainCharacter(
                modifier = Modifier
                    .offset(
                        x = backgroundLayout.x(characterX).dp,
                        y = backgroundLayout.y(characterY).dp
                    ),
                isWalking = isWalking,
                assetName = "mongi_walk.json",
                size = backgroundLayout.scale(MAP_CHARACTER_SIZE).dp,
                mirrorHorizontally = characterTargetX < characterX
            )
        }

        // BGM on/off 버튼
        Box(
            modifier = Modifier
                .align(Alignment.TopEnd)
                .padding(top = 16.dp, end = 16.dp)
                .size(48.dp)
                .background(
                    color = ArchiThemeImpl.archiColor.contentOnBrand.copy(alpha = 0.4f),
                    shape = CircleShape
                )
                .clickable(
                    indication = null,
                    interactionSource = remember { MutableInteractionSource() }
                ) { onBgmClick() },
            contentAlignment = Alignment.Center
        ) {
            ArchiText(
                text = if (isBgmOn) "🎧" else "🔇",
                style = ArchiThemeImpl.typeScale.textStrongL,
                color = ArchiThemeImpl.archiColor.contentOnBrand
            )
        }

    }
}

private const val MAP_BACKGROUND_WIDTH_PX = 2800f
private const val MAP_BACKGROUND_HEIGHT_PX = 1752f
private const val MAP_PANEL_WIDTH = 759.062f
private const val MAP_PANEL_HEIGHT = 510.27f
private const val MAP_CHARACTER_SIZE = 656.25f

private const val TANGRAM_PANEL_X = 437.5f
private const val TANGRAM_PANEL_Y = 164.25f
private const val TANGRAM_CHARACTER_X = 280f
private const val TANGRAM_CHARACTER_Y = 306.6f

private const val OX_PANEL_X = 1750f
private const val OX_PANEL_Y = 284.7f
private const val OX_CHARACTER_X = 1596.875f
private const val OX_CHARACTER_Y = 394.2f

private const val MYPAGE_PANEL_X = 721.875f
private const val MYPAGE_PANEL_Y = 810.3f
private const val MYPAGE_CHARACTER_X = 371.875f
private const val MYPAGE_CHARACTER_Y = 678.9f


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
