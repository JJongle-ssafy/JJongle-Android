package com.ssafy.jjongle.presentation.ui.screen

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
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.ssafy.jjongle.presentation.ui.component.BaseButton
import com.ssafy.jjongle.presentation.ui.component.MainCharacter
import com.ssafy.jjongle.presentation.ui.component.ResponsiveBackgroundImage
import com.ssafy.jjongle.presentation.ui.layout.calculateAdaptiveBackgroundLayout
import com.ssafy.jjongle.presentation.viewmodel.TangramStageViewModel
import kotlinx.coroutines.launch
import kotlinx.coroutines.delay


@Composable
fun TangramStageScreen(
    gameName: String,
    backgroundImagePainter: Painter,
    onStartGameClick: (stageId: Int, accessToken: String?, refreshToken: String?) -> Unit,
    onGoMapClick: () -> Unit,
    onMeetAnimalClick: () -> Unit,
    modifier: Modifier = Modifier,
    startGameButtonText: String = "탐험을 떠나볼까요?",
    goHomeButtonText: String = "처음으로 돌아가기",
    meetAnimalButtonText: String = "동물 친구 만나기",
    viewModel: TangramStageViewModel = hiltViewModel()
) {
    // ViewModel 상태 구독
    val gameState by viewModel.gameState.collectAsState()
    val characterX by viewModel.characterX.collectAsState()
    val characterY by viewModel.characterY.collectAsState()
    val isCharacterMoving by viewModel.isCharacterMoving.collectAsState()
    val currentStage by viewModel.currentStage.collectAsState()
    val currentChallengeStageId by viewModel.currentChallengeStageId.collectAsState()
    val authTokens by viewModel.authTokens.collectAsState()
    
    // 스테이지 위치 데이터 (ViewModel에서 가져올 수도 있지만, UI 전용이므로 여기 유지)
    val stagePositions = remember {
        listOf(
            com.ssafy.jjongle.presentation.viewmodel.StagePosition(1, 420f, 500f),
            com.ssafy.jjongle.presentation.viewmodel.StagePosition(2, 590f, 420f),
            com.ssafy.jjongle.presentation.viewmodel.StagePosition(3, 440f, 320f),
            com.ssafy.jjongle.presentation.viewmodel.StagePosition(4, 260f, 350f),
            com.ssafy.jjongle.presentation.viewmodel.StagePosition(5, 140f, 250f),
            com.ssafy.jjongle.presentation.viewmodel.StagePosition(6, 160f, 130f),
            com.ssafy.jjongle.presentation.viewmodel.StagePosition(7, 360f, 60f),
            com.ssafy.jjongle.presentation.viewmodel.StagePosition(8, 520f, 130f),
            com.ssafy.jjongle.presentation.viewmodel.StagePosition(9, 700f, 120f)
        )
    }

    // 캐릭터 위치 애니메이션 상태 (UI 전용)
    val animatedCharacterX = remember { Animatable(characterX) }
    val animatedCharacterY = remember { Animatable(characterY) }
    var targetStageId by remember { mutableStateOf(0) }

    val coroutineScope = rememberCoroutineScope()
    
    // ViewModel의 캐릭터 위치 변화를 애니메이션으로 반영
    LaunchedEffect(characterX, characterY) {
        coroutineScope.launch {
            animatedCharacterX.animateTo(
                targetValue = characterX,
                animationSpec = tween(durationMillis = 800)
            )
        }
        coroutineScope.launch {
            animatedCharacterY.animateTo(
                targetValue = characterY,
                animationSpec = tween(durationMillis = 800)
            )
        }
    }

    // 이동 완료 후 스테이지 시작
    LaunchedEffect(isCharacterMoving) {
        if (!isCharacterMoving && targetStageId > 0) {
            delay(300)
            onStartGameClick(targetStageId, authTokens?.accessToken, authTokens?.refreshToken)
            targetStageId = 0
        }
    }

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
            val backgroundLayout = calculateAdaptiveBackgroundLayout(
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
                                // 스테이지 접근 가능 여부 확인
                                if (stage.stageId <= currentChallengeStageId) {
                                    if (stage.stageId == currentStage) {
                                        // 현재 스테이지를 터치한 경우 바로 게임 시작
                                        onStartGameClick(
                                            stage.stageId,
                                            authTokens?.accessToken,
                                            authTokens?.refreshToken
                                        )
                                    } else {
                                        // 다른 스테이지로 이동
                                        targetStageId = stage.stageId
                                        viewModel.moveToStage(stage.stageId)
                                    }
                                }
                            }
                        }
                )
            }

            // 몽이 캐릭터
            MainCharacter(
                modifier = Modifier
                    .offset(
                        x = backgroundLayout.x(stageX(animatedCharacterX.value)).dp,
                        y = backgroundLayout.y(stageY(animatedCharacterY.value)).dp
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
            fontSize = 16.sp,
            modifier = Modifier
                .align(Alignment.BottomStart)
                .padding(start = 30.dp, bottom = 30.dp)
        )

        // 동물 친구 만나기 버튼 (우측 하단)
        BaseButton(
            onClick = onMeetAnimalClick,
            text = meetAnimalButtonText,
            fontSize = 16.sp,
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(end = 30.dp, bottom = 30.dp)
        )

    }
}

private const val TANGRAM_STAGE_BACKGROUND_WIDTH_PX = 2800f
private const val TANGRAM_STAGE_BACKGROUND_HEIGHT_PX = 1752f
private const val TANGRAM_STAGE_DESIGN_WIDTH = 1280f
private const val TANGRAM_STAGE_DESIGN_HEIGHT = 800f
