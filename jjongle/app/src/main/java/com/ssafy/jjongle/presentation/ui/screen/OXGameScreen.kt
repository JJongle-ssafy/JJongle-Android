package com.ssafy.jjongle.presentation.ui.screen

import android.graphics.BitmapFactory
import android.util.Base64
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.hilt.navigation.compose.hiltViewModel
import com.airbnb.lottie.compose.LottieAnimation
import com.airbnb.lottie.compose.LottieCompositionSpec
import com.airbnb.lottie.compose.LottieConstants
import com.airbnb.lottie.compose.animateLottieCompositionAsState
import com.airbnb.lottie.compose.rememberLottieComposition
import com.ssafy.jjongle.R
import com.ssafy.jjongle.data.websocket.ConnectionState
import com.ssafy.jjongle.domain.entity.Quiz
import com.ssafy.jjongle.presentation.state.TTSState
import com.ssafy.jjongle.presentation.ui.component.BaseButton
import com.ssafy.jjongle.presentation.ui.component.CameraComponent
import com.ssafy.jjongle.presentation.viewmodel.OXGameViewModel
import com.ssafy.jjongle.util.AudioPlayer
import kotlinx.coroutines.delay
import java.io.File

@Composable
fun OXGameScreen(
    onNavigateToMap: () -> Unit,
    viewModel: OXGameViewModel = hiltViewModel()
) {
    val gameState by viewModel.gameState.collectAsState()
    val quizSession by viewModel.quizSession.collectAsState()
    val currentQuiz by viewModel.currentQuiz.collectAsState()
    val currentQuizIndex by viewModel.currentQuizIndex.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val errorMessage by viewModel.errorMessage.collectAsState()
    val connectionState by viewModel.connectionState.collectAsState()
    val timeLeft by viewModel.timeLeft.collectAsState()
    val isQuizActive by viewModel.isQuizActive.collectAsState()
    val finishProfiles by viewModel.finishProfiles.collectAsState()
    val finalTop3 by viewModel.finalTop3.collectAsState()
    val captureTrigger by viewModel.captureTrigger.collectAsState()
    val userPosition by viewModel.userPosition.collectAsState()
    val isAnswerSubmitted by viewModel.isAnswerSubmitted.collectAsState()
    val showRewardAnimation by viewModel.showRewardAnimation.collectAsState()
    val animationType by viewModel.animationType.collectAsState()
    val ttsState by viewModel.ttsState.collectAsState()
    // 정답 애니메이션 상태
    var showCorrectAnimation by remember { mutableStateOf(false) }
    var correctAnswer by remember { mutableStateOf<String?>(null) }
    var animationCompleted by remember { mutableStateOf(false) }

    // AudioPlayer 초기화
    val context = LocalContext.current
    val audioPlayer = remember {
        AudioPlayer(context).apply {
            setOnPlaybackCompleted {
                // TTS 재생 완료 시 타이머 재시작
                viewModel.resetTTSState()
            }
        }
    }

    // 해설 TTS 시작 여부
    var explanationTtsStarted by remember { mutableStateOf(false) }

    // 화면 진입 시 즉시 게임 시작 및 WebSocket 연결
    LaunchedEffect(Unit) {
        // WebSocket 연결 상태 초기화
        viewModel.resetConnectionState()
        viewModel.connectToGame()
    }

    // TTS 상태 관찰 및 재생
    LaunchedEffect(ttsState) {
        val currentTTSState = ttsState // Assign to a local variable
        when (currentTTSState) {
            is TTSState.Success -> {
                // 해설 단계라면 시작 플래그 설정
                if (!isQuizActive) explanationTtsStarted = true
                audioPlayer.playTTS(currentTTSState.response) // Use the local variable
                // TTS 재생 완료 후 타이머 재시작
                delay(100) // 잠시 대기 후 재생 상태 확인
                if (!audioPlayer.isPlaying()) {
                    viewModel.resetTTSState()
                }
            }

            is TTSState.Error -> {
                // TTS 에러는 로그만 출력 (게임 진행에 영향 없음)
                println("TTS 에러: ${currentTTSState.message}") // Use the local variable
                viewModel.resetTTSState()
                // 해설 단계라면 시작 플래그 설정(실패 시에도 완료 신호와 동일하게 처리)
                if (!isQuizActive) explanationTtsStarted = true
            }

            else -> { /* 로딩 등은 무시 */
            }
        }
    }

    // 해설 단계에서 TTS가 끝난 뒤 다음 문제로 자동 진행
    LaunchedEffect(isQuizActive, isAnswerSubmitted, ttsState, explanationTtsStarted) {
        if (!isQuizActive && isAnswerSubmitted && explanationTtsStarted && ttsState is TTSState.Idle) {
            // TTS 종료 후 2초 대기
            delay(2000)
            // 여전히 해설 단계이고 TTS가 종료 상태인지 재확인 후 진행
            if (!isQuizActive && isAnswerSubmitted && explanationTtsStarted && ttsState is TTSState.Idle) {
                viewModel.nextQuiz()
                explanationTtsStarted = false
            }
        }
    }

    // 정답 애니메이션 표시 및 관리
    LaunchedEffect(showRewardAnimation, currentQuiz, animationType) {
        if (showRewardAnimation && currentQuiz != null && animationType != null) {
            // 애니메이션 시작
            correctAnswer = animationType // "CORRECT" 또는 "WRONG"
            showCorrectAnimation = true
            animationCompleted = false

            println("DEBUG: 애니메이션 시작 - 타입: $correctAnswer")

            // 3초 후 애니메이션 완료
            delay(3000)
            showCorrectAnimation = false
            correctAnswer = null
            animationCompleted = true

            println("DEBUG: 애니메이션 완료 - 해설 화면으로 전환")

            // 애니메이션 완료 후 해설 화면으로 전환
            viewModel.showExplanation()
        }
    }

    // 새로운 퀴즈 시작 시 애니메이션 상태 초기화
    LaunchedEffect(currentQuizIndex) {
        animationCompleted = false
        showCorrectAnimation = false
        correctAnswer = null
        viewModel.resetTTSState() // TTS 상태 초기화
        println("DEBUG: 새로운 퀴즈 시작 - 애니메이션 상태 초기화")
    }

    // 연결 상태에 따른 UI 처리
    when {
        // 로딩 중이거나 연결 대기 중
        isLoading || connectionState == ConnectionState.CONNECTING -> {
            LoadingDialog("게임 연결 중...")
        }

        // 연결 실패
        connectionState == ConnectionState.ERROR -> {
            ErrorDialog(
                message = "연결에 실패했습니다.",
                onConfirm = onNavigateToMap
            )
        }

        // 게임 시작 전 (연결 완료 후)
        quizSession != null && !gameState.isGameActive && !gameState.isGameFinished -> {
            GameStartContent(
                onStartQuiz = { viewModel.startCurrentQuiz() }
            )
        }

        // 게임 진행 중
        gameState.isGameActive && currentQuiz != null -> {
            currentQuiz?.let { quiz ->
                if (isQuizActive) {
                    // 퀴즈 진행 중
                    QuizGameContent(
                        quiz = quiz,
                        quizIndex = currentQuizIndex,
                        totalQuizzes = quizSession?.quizzes?.size ?: 0,
                        timeLeft = timeLeft,
                        captureTrigger = captureTrigger,
                        onFrameCaptured = { file ->
                            viewModel.sendFrameForAnalysis(file)
                        },
                        showCorrectAnimation = showCorrectAnimation,
                        correctAnswer = correctAnswer
                    )
                } else {
                    // 해설 페이지
                    QuizExplanationContent(
                        quiz = quiz,
                        quizIndex = currentQuizIndex,
                        totalQuizzes = quizSession?.quizzes?.size ?: 0,
                        isAnswerSubmitted = isAnswerSubmitted,
                        onNextQuiz = { viewModel.nextQuiz() }
                    )
                }
            }
        }

        // 게임 종료
        gameState.isGameFinished && !gameState.isLoading -> {

            GameResultContent(
                top3Rankings = finalTop3,
                profiles = finishProfiles,
                onRestartGame = { viewModel.restartGame() },
                onBackToMenu = onNavigateToMap
            )
        }
    }

    // 에러 메시지 처리
    errorMessage?.let { error ->
        // 게임 진행 중이 아닐 때는 스낵바, 게임 진행 중일 때는 다이얼로그
        if (!gameState.isGameActive) {
            LaunchedEffect(error) {
                delay(3000)
                viewModel.clearError()
            }
            ErrorSnackbar(message = error)
        } else {
            GameErrorDialog(
                message = error,
                onConfirm = {
                    viewModel.clearError()
                    onNavigateToMap() // 맵 화면으로 이동
                }
            )
        }
    }
}

@Composable
fun LoadingDialog(message: String) {
    Dialog(onDismissRequest = { }) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                CircularProgressIndicator(
                    modifier = Modifier.size(48.dp),
                    color = MaterialTheme.colorScheme.primary
                )
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = message,
                    style = MaterialTheme.typography.bodyLarge,
                    textAlign = TextAlign.Center
                )
            }
        }
    }
}

@Composable
fun ErrorDialog(
    message: String,
    onConfirm: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onConfirm,
        title = { Text("연결 오류") },
        text = { Text(message) },
        confirmButton = {
            TextButton(onClick = onConfirm) {
                Text("확인")
            }
        }
    )
}

@Composable
fun GameErrorDialog(
    message: String,
    onConfirm: () -> Unit
) {
    AlertDialog(
        onDismissRequest = { /* 무시 */ },
        title = { Text("게임 오류") },
        text = { Text("게임 도중 오류가 발생했습니다.\n($message)") },
        confirmButton = {
            TextButton(onClick = onConfirm) {
                Text("확인")
            }
        }
    )
}

@Composable
fun GameStartContent(
    onStartQuiz: () -> Unit
) {
    Box(
        modifier = Modifier.fillMaxSize()
    ) {
        // 배경 이미지
        Image(
            painter = painterResource(id = R.drawable.ox_start_content_bg),
            contentDescription = "게임 시작 배경",
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop
        )

        // 버튼을 상단 오른쪽에 배치
        BaseButton(
            text = "모험 떠나기",
            onClick = onStartQuiz,
            modifier = Modifier
                .wrapContentWidth()
                .padding(end = 25.dp, top = 25.dp)
                .align(Alignment.TopEnd)
        )

    }
}

@Composable
fun QuizGameContent(
    quiz: Quiz,
    quizIndex: Int,
    totalQuizzes: Int,
    timeLeft: Int,
    captureTrigger: Int,
    onFrameCaptured: (File) -> Unit,
    showCorrectAnimation: Boolean,
    correctAnswer: String?
) {
    Box(modifier = Modifier.fillMaxSize()) {
        // 배경 이미지
        Image(
            painter = painterResource(id = R.drawable.ox_game_camera_background),
            contentDescription = "게임 카메라 배경",
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // 상단 정보 바 (문제만 표시)
            QuizInfoBar(
                quiz = quiz,
                quizIndex = quizIndex,
                totalQuizzes = totalQuizzes
            )

            // 화면의 나머지 공간을 차지하는 Spacer
            Spacer(modifier = Modifier.weight(1f))

            Box(
                modifier = Modifier
                    .fillMaxWidth(0.85f)
                    .fillMaxHeight(0.85f)
                    .padding(bottom = 30.dp),
                contentAlignment = Alignment.Center
            ) {
                Card(
                    modifier = Modifier.fillMaxSize(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.Black.copy(alpha = 0.15f))
                ) {}

                CameraComponent(
                    modifier = Modifier
                        .fillMaxSize()
                        .clip(RoundedCornerShape(16.dp))
                        .border(2.dp, Color.White.copy(alpha = 0.8f), RoundedCornerShape(16.dp)),
                    captureTrigger = captureTrigger,
                    onFrameCaptured = onFrameCaptured
                )

                Box(
                    modifier = Modifier
                        .width(4.dp)
                        .fillMaxSize()
                        .align(Alignment.Center)
                        .background(Color.Yellow)
                )
            }

            // 하단 타이머 프로그레스 바
            TimerProgressBar(
                timeLeft = timeLeft,
                totalTime = 10,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp, vertical = 16.dp)
            )
        }

        // O X 이미지들을 카메라 위에 오버레이
        // 왼쪽 상단 O 이미지
        Image(
            painter = painterResource(id = R.drawable.ox_o),
            contentDescription = "O",
            modifier = Modifier
                .size(280.dp)
                .offset(x = 20.dp, y = (-50).dp)
                .align(Alignment.TopStart)
        )

        // 오른쪽 상단 X 이미지
        Image(
            painter = painterResource(id = R.drawable.ox_x),
            contentDescription = "X",
            modifier = Modifier
                .size(280.dp)
                .offset(x = 20.dp, y = (-50).dp)
                .align(Alignment.TopEnd)
        )

        // 정답 애니메이션
        if (showCorrectAnimation && correctAnswer != null) {
            CorrectAnswerAnimation(
                answer = correctAnswer!!,
                modifier = Modifier.fillMaxSize()
            )
        }
    }
}

@Composable
fun QuizInfoBar(
    quiz: Quiz,
    quizIndex: Int,
    totalQuizzes: Int
) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {

        // 문제
        Card(
            modifier = Modifier
                .width(700.dp)
                .height(150.dp),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White.copy(alpha = 0.8f))
        ) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = quiz.question,
                    style = MaterialTheme.typography.headlineSmall,
                    fontSize = 36.sp,
                    lineHeight = 48.sp,
                    textAlign = TextAlign.Center,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp),
                    fontWeight = FontWeight.Bold
                )
            }
        }

        // 문제 번호
        Text(
            //TODO: 문제 수 3 문제로 조정
            text = "문제 ${quizIndex + 1}/$totalQuizzes",
//            text = "문제 ${quizIndex + 1}/3",
            style = MaterialTheme.typography.titleMedium,
            color = Color.White,
            modifier = Modifier.padding(top = 8.dp)
        )
    }
}

@Composable
fun GameResultContent(
    top3Rankings: List<Pair<Int, Int>>, // (userId, score)
    profiles: Map<Int, String>,
    onRestartGame: () -> Unit,
    onBackToMenu: () -> Unit
) {
    // 발랄한 폰트
    val playfulFont = FontFamily(Font(R.font.jalnan2))

    Box(modifier = Modifier.fillMaxSize()) {
        // 결과 배경
        Image(
            painter = painterResource(id = R.drawable.ox_game_result_background),
            contentDescription = "result bg",
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {


            // 중앙 영역: 배경에 시상대가 포함되어 있으므로,
            // 프로필 이미지 + 정답 개수 배지만 지정 위치에 오버레이
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                contentAlignment = Alignment.Center
            ) {
                val rankedWithProfile = top3Rankings.filter { (userId, _) -> profiles.containsKey(userId) }
                val first = rankedWithProfile.getOrNull(0)
                val second = rankedWithProfile.getOrNull(1)
                val third = rankedWithProfile.getOrNull(2)

                // 1위: 가운데 상단
                if (first != null) {
                    RankedProfile(
                        base64 = profiles[first.first],
                        score = first.second,
                        size = 180.dp,
                        modifier = Modifier
                            .align(Alignment.TopCenter)
                            .offset(y = 140.dp)
                    )
                }

                // 2위: 오른쪽 중간
                if (second != null) {
                    RankedProfile(
                        base64 = profiles[second.first],
                        score = second.second,
                        size = 160.dp,
                        modifier = Modifier
                            .align(Alignment.Center)
                            .offset(x = 210.dp)
                    )
                }

                // 3위: 왼쪽 살짝 아래
                if (third != null) {
                    RankedProfile(
                        base64 = profiles[third.first],
                        score = third.second,
                        size = 150.dp,
                        modifier = Modifier
                            .align(Alignment.Center)
                            .offset(x = (-210).dp)
                    )
                }
            }

            // 하단 버튼: 항상 바닥에 위치
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 24.dp),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                BaseButton(
                    text = "다시 게임",
                    onClick = onRestartGame,
                    modifier = Modifier.weight(1f)
                )

                BaseButton(
                    text = "메인으로",
                    onClick = onBackToMenu,
                    modifier = Modifier.weight(1f)
                )
            }
        }
    }
}

// 기존 Podium 기반 UI는 배경 이미지에 통합되어 삭제됨

@Composable
private fun ScoreRibbon(score: Int, fontFamily: FontFamily) {
    Box(
        modifier = Modifier
            .background(
                brush = Brush.horizontalGradient(
                    colors = listOf(Color(0xFF4DD0E1), Color(0xFF7E57C2))
                ),
                shape = RoundedCornerShape(20.dp)
            )
            .padding(horizontal = 12.dp, vertical = 6.dp)
    ) {
        Text(
            text = "정답 ${score}개",
            style = MaterialTheme.typography.bodyLarge,
            fontWeight = FontWeight.Bold,
            color = Color.White,
            fontFamily = fontFamily
        )
    }
}

@Composable
private fun ProfileImageCircle(base64: String?) {
    val bmp = remember(base64) { decodeBase64ToBitmapOrNull(base64) }
    Box(
        modifier = Modifier
            .size(180.dp)
            .clip(CircleShape)
            .border(4.dp, Color.White, CircleShape)
            .background(Color(0x22000000), CircleShape),
        contentAlignment = Alignment.Center
    ) {
        if (bmp != null) {
            Image(
                bitmap = bmp.asImageBitmap(),
                contentDescription = "profile",
                modifier = Modifier
                    .fillMaxSize()
                    .clip(CircleShape),
                contentScale = ContentScale.Crop
            )
        } else {
            Text(
                text = "?",
                style = MaterialTheme.typography.headlineSmall,
                color = Color.White,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

@Composable
private fun RankedProfile(
    base64: String?,
    score: Int,
    size: Dp,
    modifier: Modifier = Modifier
) {
    Box(modifier = modifier) {
        // 프로필 이미지
        val bmp = remember(base64) { decodeBase64ToBitmapOrNull(base64) }
        Box(
            modifier = Modifier
                .size(size)
                .clip(CircleShape)
                .border(4.dp, Color.White, CircleShape)
                .background(Color(0x22000000), CircleShape)
                .align(Alignment.Center)
        ) {
            if (bmp != null) {
                Image(
                    bitmap = bmp.asImageBitmap(),
                    contentDescription = "profile",
                    modifier = Modifier
                        .fillMaxSize()
                        .clip(CircleShape),
                    contentScale = ContentScale.Crop
                )
            }
        }

        // 점수 배지 (상단)
        Box(
            modifier = Modifier
                .align(Alignment.TopCenter)
                .offset(y = (-12).dp)
                .background(
                    brush = Brush.horizontalGradient(
                        colors = listOf(Color(0xFFFFD54F), Color(0xFFFF8A65))
                    ),
                    shape = RoundedCornerShape(20.dp)
                )
                .padding(horizontal = 10.dp, vertical = 4.dp)
        ) {
            Text(
                text = "정답 ${score}개",
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )
        }
    }
}

private fun decodeBase64ToBitmapOrNull(raw: String?): android.graphics.Bitmap? {
    if (raw.isNullOrBlank()) return null
    return try {
        val data = if (raw.startsWith("data:image")) raw.substringAfter(",") else raw
        val bytes = Base64.decode(data, Base64.DEFAULT)
        BitmapFactory.decodeByteArray(bytes, 0, bytes.size)
    } catch (_: Exception) {
        null
    }
}

@Composable
fun ErrorSnackbar(message: String) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.Red),
        shape = RoundedCornerShape(8.dp)
    ) {
        Text(
            text = message,
            style = MaterialTheme.typography.bodyMedium,
            color = Color.White,
            modifier = Modifier.padding(16.dp)
        )
    }
}

@Composable
fun QuizExplanationContent(
    quiz: Quiz,
    quizIndex: Int,
    totalQuizzes: Int,
    isAnswerSubmitted: Boolean,
    onNextQuiz: () -> Unit
) {
    // 자동 진행은 상위 컴포저블에서 TTS 종료 시점에 맞춰 처리합니다.

    Box(modifier = Modifier.fillMaxSize()) {
        // 배경 이미지
        Image(
            painter = painterResource(id = R.drawable.ox_game_background),
            contentDescription = "해설 배경",
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop
        )
        val oxResult = if (quiz.answer == "O") {
            R.drawable.ox_o
        } else {
            R.drawable.ox_x
        }
        // 왼쪽 상단 O 이미지
        Image(
            painter = painterResource(oxResult),
            contentDescription = "OX 정답",
            modifier = Modifier
                .size(400.dp)
                .offset(x = 20.dp, y = (-50).dp)
                .align(Alignment.TopStart)
        )


        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.weight(1f))

            // 해설 텍스트: 행간 확대 및 살짝 아래로 오프셋
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.offset(y = 16.dp)
            ) {
                Text(
                    text = quiz.description,
                    style = MaterialTheme.typography.bodyLarge,
                    textAlign = TextAlign.Center,
                    color = Color.Black,
                    fontSize = 30.sp,
                    lineHeight = 44.sp,
                    modifier = Modifier
                        .widthIn(max = 700.dp)
                        .padding(horizontal = 16.dp)
                )

                Text(
                    //TODO: 문제 수 3 문제로 조정
                    text = if (quizIndex + 1 >= totalQuizzes) "\n\n5초 후 결과화면으로 넘어갑니다..." else "\n\n5초 후 다음 문제로 넘어갑니다...",
//                    text = if (quizIndex + 1 >= 3) "\n\n5초 후 결과화면으로 넘어갑니다..." else "\n\n5초 후 다음 문제로 넘어갑니다...",
                    style = MaterialTheme.typography.bodyLarge,
                    textAlign = TextAlign.Center,
                    color = Color.Black,
                    fontSize = 30.sp,
                    modifier = Modifier
                        .widthIn(max = 700.dp)
                        .padding(horizontal = 16.dp)
                )
            }

            Spacer(modifier = Modifier.weight(1f))

            // 다음 문제 버튼 (SUBMIT_ANSWER 응답이 와야 활성화)
            BaseButton(
                //TODO: 문제 수 3 문제로 조정
                text = if (quizIndex + 1 >= totalQuizzes) "결과 화면으로 가기" else "다음 문제",
//                text = if (quizIndex + 1 >= 3) "결과 화면으로 가기" else "다음 문제",
                onClick = onNextQuiz,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp),
                enabled = isAnswerSubmitted
            )
        }
    }
}

@Composable
fun TimerProgressBar(
    timeLeft: Int,
    totalTime: Int,
    modifier: Modifier = Modifier
) {
    val progress = timeLeft.toFloat() / totalTime.toFloat()
    val backgroundColor = Color(0xFF8B4513) // 어두운 갈색 배경
    val progressColor = Color(0xFFFFD700) // 밝은 노란색 프로그레스 바

    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(40.dp)
            .background(
                color = backgroundColor,
                shape = RoundedCornerShape(20.dp)
            ),
        contentAlignment = Alignment.CenterStart
    ) {
        // 프로그레스 바 (애니메이션 적용)
        androidx.compose.animation.core.animateFloatAsState(
            targetValue = progress,
            animationSpec = androidx.compose.animation.core.tween(
                durationMillis = 1000, // 1초 동안 부드럽게 애니메이션
                easing = androidx.compose.animation.core.LinearEasing
            ),
            label = "progress"
        ).let { animatedProgress ->
            Box(
                modifier = Modifier
                    .fillMaxWidth(animatedProgress.value)
                    .height(40.dp)
                    .background(
                        color = progressColor,
                        shape = RoundedCornerShape(20.dp)
                    )
            )
        }

        // 타이머 텍스트 (오른쪽에 배치)
        Text(
            text = "${timeLeft}초",
            style = MaterialTheme.typography.titleMedium,
            color = Color.White,
            fontWeight = FontWeight.Bold,
            modifier = Modifier
                .align(Alignment.CenterEnd)
                .padding(end = 16.dp)
        )
    }
}

@Composable
fun CorrectAnswerAnimation(
    answer: String,
    modifier: Modifier = Modifier
) {
    var animationScale by remember { mutableStateOf(0f) }
    var animationAlpha by remember { mutableStateOf(0f) }

    LaunchedEffect(Unit) {
        // 애니메이션 시작 - 즉시 나타남
        animationScale = 1.2f
        animationAlpha = 1f

        // 0.5초 후 원래 크기로
        delay(500)
        animationScale = 1f

        // 2초 후 페이드 아웃
        delay(2000)
        animationAlpha = 0f
    }

    // 정답일 때는 O/X에 따라 위치 결정, 오답일 때는 가운데
    val alignment = when (answer) {
        "O" -> Alignment.CenterStart
        "X" -> Alignment.CenterEnd
        else -> Alignment.Center // 오답일 때
    }

    Box(
        modifier = modifier,
        contentAlignment = alignment
    ) {
        Card(
            modifier = Modifier
                .size(450.dp)
                .graphicsLayer(
                    scaleX = animationScale,
                    scaleY = animationScale,
                    alpha = animationAlpha
                ),
            colors = CardDefaults.cardColors(containerColor = Color.Transparent),
            elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
            shape = RoundedCornerShape(16.dp)
        ) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                when (answer) {
                    "O", "X" -> {
                        // 정답일 때 Trophy.json 애니메이션 표시
                        val composition by rememberLottieComposition(
                            LottieCompositionSpec.Asset("Trophy.json")
                        )
                        val progress by animateLottieCompositionAsState(
                            composition = composition,
                            iterations = LottieConstants.IterateForever,
                            isPlaying = true
                        )

                        LottieAnimation(
                            composition = composition,
                            progress = { progress },
                            modifier = Modifier.size(400.dp)
                        )
                    }

                    else -> {
                        // 오답일 때 가운데에 sad.json 애니메이션 표시
                        val composition by rememberLottieComposition(
                            LottieCompositionSpec.Asset("sad.json")
                        )
                        val progress by animateLottieCompositionAsState(
                            composition = composition,
                            iterations = LottieConstants.IterateForever,
                            isPlaying = true
                        )

                        LottieAnimation(
                            composition = composition,
                            progress = { progress },
                            modifier = Modifier.size(450.dp)
                        )
                    }
                }
            }
        }
    }
}