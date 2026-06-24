package com.ssafy.jjongle.tangram.presentation.ui.screen

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.ssafy.jjongle.common.presentation.ui.component.BaseButton
import com.ssafy.jjongle.common.presentation.ui.component.MainCharacter
import com.ssafy.jjongle.common.presentation.ui.component.ResponsiveBackgroundImage
import com.ssafy.jjongle.common.presentation.ui.layout.calculateFillBoundsBackgroundLayout
import com.ssafy.jjongle.common.presentation.ui.theme.ArchiThemeImpl
import com.ssafy.jjongle.tangram.presentation.viewmodel.TangramStageIntent
import com.ssafy.jjongle.tangram.presentation.viewmodel.TangramStageViewModel
import kotlinx.coroutines.launch
import kotlinx.coroutines.delay


@Composable
fun TangramStageScreen(
    gameName: String,
    backgroundImagePainter: Painter,
    onStartGameClick: (stageId: Int) -> Unit,
    onGoMapClick: () -> Unit,
    onMeetAnimalClick: () -> Unit,
    modifier: Modifier = Modifier,
    startGameButtonText: String = "탐험을 떠나볼까요?",
    goHomeButtonText: String = "처음으로 돌아가기",
    meetAnimalButtonText: String = "동물 친구 만나기",
    viewModel: TangramStageViewModel = hiltViewModel()
) {
    // ViewModel 상태 구독
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    // 캐릭터 위치 애니메이션 상태 (UI 전용)
    val animatedCharacterX = remember { Animatable(uiState.characterX) }
    val animatedCharacterY = remember { Animatable(uiState.characterY) }
    var targetStageId by remember { mutableStateOf(0) }

    val coroutineScope = rememberCoroutineScope()
    
    // ViewModel의 캐릭터 위치 변화를 애니메이션으로 반영
    LaunchedEffect(uiState.characterX, uiState.characterY) {
        coroutineScope.launch {
            animatedCharacterX.animateTo(
                targetValue = uiState.characterX,
                animationSpec = tween(durationMillis = 800)
            )
        }
        coroutineScope.launch {
            animatedCharacterY.animateTo(
                targetValue = uiState.characterY,
                animationSpec = tween(durationMillis = 800)
            )
        }
    }

    // 이동 완료 후 스테이지 시작
    LaunchedEffect(uiState.isCharacterMoving) {
        if (!uiState.isCharacterMoving && targetStageId > 0) {
            delay(300)
            onStartGameClick(targetStageId)
            targetStageId = 0
        }
    }

    TangramStageContent(
        gameName = gameName,
        backgroundImagePainter = backgroundImagePainter,
        characterX = animatedCharacterX.value,
        characterY = animatedCharacterY.value,
        isCharacterMoving = uiState.isCharacterMoving,
        onStageClick = { stageId ->
            if (!uiState.isCharacterMoving && stageId <= uiState.currentChallengeStageId) {
                if (stageId == uiState.currentStage) {
                    onStartGameClick(stageId)
                } else {
                    targetStageId = stageId
                    viewModel.onIntent(TangramStageIntent.MoveToStage(stageId))
                }
            }
        },
        onGoMapClick = onGoMapClick,
        onMeetAnimalClick = onMeetAnimalClick,
        modifier = modifier,
        goHomeButtonText = goHomeButtonText,
        meetAnimalButtonText = meetAnimalButtonText
    )
}

@Composable
fun TangramStageContent(
    gameName: String,
    backgroundImagePainter: Painter,
    characterX: Float,
    characterY: Float,
    isCharacterMoving: Boolean,
    onStageClick: (stageId: Int) -> Unit,
    onGoMapClick: () -> Unit,
    onMeetAnimalClick: () -> Unit,
    modifier: Modifier = Modifier,
    goHomeButtonText: String = "처음으로 돌아가기",
    meetAnimalButtonText: String = "동물 친구 만나기"
) {
    val stagePositions = remember { defaultTangramStagePositions() }

    Box(
        modifier = modifier.fillMaxSize()
    ) {
        // 배경 이미지
        ResponsiveBackgroundImage(
            painter = backgroundImagePainter,
            contentDescription = "$gameName 배경 이미지",
            modifier = Modifier.fillMaxSize()
        )

        BoxWithConstraints(modifier = Modifier.fillMaxSize()) {
            val backgroundLayout = calculateFillBoundsBackgroundLayout(
                containerWidth = maxWidth.value,
                containerHeight = maxHeight.value,
                imageWidth = TANGRAM_STAGE_BACKGROUND_WIDTH_PX,
                imageHeight = TANGRAM_STAGE_BACKGROUND_HEIGHT_PX
            )

            fun stageX(value: Float) = value * TANGRAM_STAGE_BACKGROUND_WIDTH_PX / TANGRAM_STAGE_DESIGN_WIDTH
            fun stageY(value: Float) = value * TANGRAM_STAGE_BACKGROUND_HEIGHT_PX / TANGRAM_STAGE_DESIGN_HEIGHT

            // 투명한 발판 터치 영역들
            stagePositions.forEach { stage ->
                Box(
                    modifier = Modifier
                        .offset(
                            x = backgroundLayout.x(stageX(stage.x + 110f)).dp,
                            y = backgroundLayout.y(stageY(stage.y + 170f)).dp
                        )
                        .size(backgroundLayout.scale(stageX(100f)).dp)
                        .clip(CircleShape)
                        .clickable {
                            if (!isCharacterMoving) {
                                onStageClick(stage.stageId)
                            }
                        }
                )
            }

            // 몽이 캐릭터
            MainCharacter(
                modifier = Modifier
                    .offset(
                        x = backgroundLayout.x(stageX(characterX)).dp,
                        y = backgroundLayout.y(stageY(characterY)).dp
                    ),
                isWalking = isCharacterMoving,
                assetName = "mongi_walk.json",
                size = backgroundLayout.scale(stageX(300f)).dp,
            )
        }

        // 처음으로 버튼 (좌측 하단)
        BaseButton(
            onClick = onGoMapClick,
            text = goHomeButtonText,
            textStyle = ArchiThemeImpl.typeScale.textStrongM,
            modifier = Modifier
                .align(Alignment.BottomStart)
                .padding(start = 30.dp, bottom = 30.dp)
        )

        // 동물 친구 만나기 버튼 (우측 하단)
        BaseButton(
            onClick = onMeetAnimalClick,
            text = meetAnimalButtonText,
            textStyle = ArchiThemeImpl.typeScale.textStrongM,
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(end = 30.dp, bottom = 30.dp)
        )

    }
}

private fun defaultTangramStagePositions() = listOf(
    com.ssafy.jjongle.tangram.presentation.viewmodel.StagePosition(1, 420f, 500f),
    com.ssafy.jjongle.tangram.presentation.viewmodel.StagePosition(2, 590f, 420f),
    com.ssafy.jjongle.tangram.presentation.viewmodel.StagePosition(3, 440f, 320f),
    com.ssafy.jjongle.tangram.presentation.viewmodel.StagePosition(4, 260f, 350f),
    com.ssafy.jjongle.tangram.presentation.viewmodel.StagePosition(5, 140f, 250f),
    com.ssafy.jjongle.tangram.presentation.viewmodel.StagePosition(6, 160f, 130f),
    com.ssafy.jjongle.tangram.presentation.viewmodel.StagePosition(7, 360f, 60f),
    com.ssafy.jjongle.tangram.presentation.viewmodel.StagePosition(8, 520f, 130f),
    com.ssafy.jjongle.tangram.presentation.viewmodel.StagePosition(9, 700f, 120f)
)

private const val TANGRAM_STAGE_BACKGROUND_WIDTH_PX = 2800f
private const val TANGRAM_STAGE_BACKGROUND_HEIGHT_PX = 1752f
private const val TANGRAM_STAGE_DESIGN_WIDTH = 1280f
private const val TANGRAM_STAGE_DESIGN_HEIGHT = 800f
