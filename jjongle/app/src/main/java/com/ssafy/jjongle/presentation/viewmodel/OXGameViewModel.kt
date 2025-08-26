package com.ssafy.jjongle.presentation.viewmodel

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.util.Base64
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ssafy.jjongle.data.local.SessionDataSource
import com.ssafy.jjongle.data.model.UserPositionDto
import com.ssafy.jjongle.data.model.toDomain
import com.ssafy.jjongle.data.websocket.ConnectionState
import com.ssafy.jjongle.data.websocket.GameEvent
import com.ssafy.jjongle.domain.entity.GameScore
import com.ssafy.jjongle.domain.entity.Quiz
import com.ssafy.jjongle.domain.entity.QuizResult
import com.ssafy.jjongle.domain.entity.QuizSession
import com.ssafy.jjongle.domain.usecase.GameActionUseCase
import com.ssafy.jjongle.domain.usecase.StartOXGameUseCase
import com.ssafy.jjongle.domain.usecase.TTSUseCase
import com.ssafy.jjongle.presentation.state.GameState
import com.ssafy.jjongle.presentation.state.TTSState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.io.ByteArrayOutputStream
import java.io.File
import javax.inject.Inject

@HiltViewModel
class OXGameViewModel @Inject constructor(
    private val startGameUseCase: StartOXGameUseCase,
    private val gameActionUseCase: GameActionUseCase,
    private val sessionDataSource: SessionDataSource,
    private val ttsUseCase: TTSUseCase
) : ViewModel() {

    // 게임 상태
    private val _gameState = MutableStateFlow(GameState())
    val gameState: StateFlow<GameState> = _gameState.asStateFlow()

    // 퀴즈 데이터
    private val _quizSession = MutableStateFlow<QuizSession?>(null)
    val quizSession: StateFlow<QuizSession?> = _quizSession.asStateFlow()

    // 현재 퀴즈 인덱스
    private val _currentQuizIndex = MutableStateFlow(0)
    val currentQuizIndex: StateFlow<Int> = _currentQuizIndex.asStateFlow()

    // 현재 퀴즈
    val currentQuiz: StateFlow<Quiz?> = combine(
        _quizSession,
        _currentQuizIndex
    ) { session, index ->
        session?.quizzes?.getOrNull(index)
    }.stateIn(viewModelScope, SharingStarted.Lazily, null)

    // 로딩 상태
    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    // 에러 메시지
    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage.asStateFlow()

    // WebSocket 관련 상태들
    val connectionState: StateFlow<ConnectionState> = gameActionUseCase.connectionState
    val gameEvents = gameActionUseCase.gameEvents

    // 타이머 관련 상태들
    private val _timeLeft = MutableStateFlow(10) // 문제당 10초
    val timeLeft: StateFlow<Int> = _timeLeft.asStateFlow()

    private val _isQuizActive = MutableStateFlow(false)
    val isQuizActive: StateFlow<Boolean> = _isQuizActive.asStateFlow()

    // 성적 관리
    private val _gameScore = MutableStateFlow(GameScore(0, 0, 0, emptyList()))
    val gameScore: StateFlow<GameScore> = _gameScore.asStateFlow()

    private val _quizResults = MutableStateFlow<List<QuizResult>>(emptyList())
    val quizResults: StateFlow<List<QuizResult>> = _quizResults.asStateFlow()


    private val _finalTop3 = MutableStateFlow<List<Pair<Int, Int>>>(emptyList())
    val finalTop3: StateFlow<List<Pair<Int, Int>>> = _finalTop3.asStateFlow()

    // 카메라 캡처 트리거
    private val _captureTrigger = MutableStateFlow(0)
    val captureTrigger: StateFlow<Int> = _captureTrigger.asStateFlow()

    // 마지막 캡처 이미지 저장 (StateFlow로 관리)
    private val _lastCapturedImageBase64 = MutableStateFlow<String?>(null)
    val lastCapturedImageBase64: StateFlow<String?> = _lastCapturedImageBase64.asStateFlow()

    // 보상 애니메이션 상태
    private val _showRewardAnimation = MutableStateFlow(false)
    val showRewardAnimation: StateFlow<Boolean> = _showRewardAnimation.asStateFlow()

    // 애니메이션 타입 (정답/오답)
    private val _animationType = MutableStateFlow<String?>(null)
    val animationType: StateFlow<String?> = _animationType.asStateFlow()

    // 사용자 위치 정보 (애니메이션 위치 계산용)
    private val _userPosition = MutableStateFlow<Pair<Double, Double>?>(null)
    val userPosition: StateFlow<Pair<Double, Double>?> = _userPosition.asStateFlow()

    // SUBMIT_ANSWER 응답 상태
    private val _isAnswerSubmitted = MutableStateFlow(false)
    val isAnswerSubmitted: StateFlow<Boolean> = _isAnswerSubmitted.asStateFlow()


    // 게임 종료시 서버로부터 받은 프로필 이미지 (userId -> base64)
    private val _finishProfiles = MutableStateFlow<Map<Int, String>>(emptyMap())
    val finishProfiles: StateFlow<Map<Int, String>> = _finishProfiles.asStateFlow()


    // TTS 관련 상태
    private val _ttsState = MutableStateFlow<TTSState>(TTSState.Idle)
    val ttsState: StateFlow<TTSState> = _ttsState.asStateFlow()

    // 타이머 작업들
    private var imageTransmissionJob: Job? = null
    private var quizTimerJob: Job? = null

    // 중복 GAME_FINISH 전송 방지 플래그
    private var isGameFinishRequested: Boolean = false


    init {
        // 게임 이벤트 관찰
        observeGameEvents()
    }

    /**
     * 게임 참가를 위해 WebSocket 연결을 시작합니다.
     */
    fun connectToGame() {
        viewModelScope.launch {
            _isLoading.value = true
            _errorMessage.value = null
            startGameUseCase.connectWebSocket()
        }
    }

    /**
     * 에러 메시지 클리어
     */
    fun clearError() {
        _errorMessage.value = null
    }

    /**
     * 퀴즈 시작 - 타이머 시작과 이미지 전송 시작
     */
    fun startCurrentQuiz() {
        val currentQuiz = currentQuiz.value ?: return

        _gameState.value = GameState(isGameActive = true) // 게임 활성화
        _isQuizActive.value = true
        _timeLeft.value = 10 // 10초 제한시간

        // 마지막 캡처 이미지 초기화
        _lastCapturedImageBase64.value = null

        // 문제 TTS 생성 및 재생
        generateQuestionTTS()

        // 퀴즈 타이머 시작
        startQuizTimer()

        // 이미지 전송 시작 (0.2초마다)
        startImageTransmission()
    }

    /**
     * 이미지 전송을 위해 0.2초마다 카메라 캡처를 트리거합니다.
     */
    private fun startImageTransmission() {
        imageTransmissionJob?.cancel()
        imageTransmissionJob = viewModelScope.launch {

            while (_isQuizActive.value) {
                _captureTrigger.value++ // 값 변경으로 캡처 트리거
                delay(10) // 0.2초 대기
            }
        }
    }

    /**
     * UI(카메라)에서 캡처된 프레임을 받아 서버로 전송합니다.
     * @param imageFile 캡처된 이미지 파일
     */
    fun sendFrameForAnalysis(imageFile: File) {
        // 게임 종료 또는 퀴즈 비활성 상태에서는 전송하지 않음
        if (_gameState.value.isGameFinished || !_isQuizActive.value) {
            imageFile.delete()
            return
        }
        // 웹소켓이 연결된 상태가 아니면 아무 작업도 하지 않고 파일을 삭제합니다.
        if (connectionState.value != ConnectionState.CONNECTED) {
            imageFile.delete()
            return
        }

        viewModelScope.launch {
            val sessionKey = startGameUseCase.getSessionKey() ?: return@launch
            val currentQuizId = currentQuiz.value?.id ?: return@launch

            try {
                // 이미지를 리사이징하고 압축하여 용량을 줄입니다.
                val resizedAndCompressedImage = resizeAndCompressImage(imageFile, 640, 480, 50)

                // Base64로 인코딩
                val base64Image = Base64.encodeToString(resizedAndCompressedImage, Base64.NO_WRAP)

                // 마지막 캡처 이미지 저장
                _lastCapturedImageBase64.value = base64Image

                gameActionUseCase.sendImageAnalysis(sessionKey, currentQuizId, base64Image)
            } catch (e: Exception) {
                handleGameError("이미지 처리 오류: ${e.message}")
            } finally {
                // 처리 후 파일 삭제
                imageFile.delete()
            }
        }
    }

    /**
     * 퀴즈 타이머 시작 (10초 카운트다운)
     */
    private fun startQuizTimer() {
        quizTimerJob?.cancel()
        quizTimerJob = viewModelScope.launch {
            while (_timeLeft.value > 0 && _isQuizActive.value) {
                delay(1000) // 1초 대기

                // TTS가 재생 중이면 타이머를 멈춤
                if (_ttsState.value is TTSState.Loading || _ttsState.value is TTSState.Success) {
                    continue
                }

                _timeLeft.value -= 1
            }

            // 시간 종료 시 자동으로 답변 제출
            if (_timeLeft.value <= 0) {
                submitFinalAnswer()
            }
        }
    }

    /**
     * 최종 답변 제출 (시간 종료 시 자동 호출)
     */
    private fun submitFinalAnswer() {
        println("DEBUG: submitFinalAnswer 호출됨")
        val currentQuiz = currentQuiz.value ?: return
        val sessionKey = startGameUseCase.getSessionKey() ?: return

        // 이미지 전송 중단
        imageTransmissionJob?.cancel()
        quizTimerJob?.cancel()

        // SUBMIT_ANSWER 전송 - 저장된 마지막 이미지 사용
        val imageData = _lastCapturedImageBase64.value ?: run {
            handleGameError("이미지가 캡처되지 않았습니다.")
            return
        }

        println("DEBUG: SUBMIT_ANSWER 전송 - 퀴즈 ID: ${currentQuiz.id}")
        gameActionUseCase.sendSubmitAnswer(sessionKey, currentQuiz.id, imageData)

        // 답변 제출 상태 초기화
        _isAnswerSubmitted.value = false

        // isQuizActive는 서버에서 SubmitResult 이벤트를 받은 후에 false로 설정됨
        // 여기서는 false로 설정하지 않음
        println("DEBUG: submitFinalAnswer 완료 - isQuizActive 유지")
    }

    /**
     * 성적 기록 처리 (SUBMIT_RESULT 이벤트 받았을 때)
     */
    private fun recordQuizResult(
        quizId: Int,
        correctAnswer: String,
        correctUserPositions: List<UserPositionDto>
    ) {
        val newResult = QuizResult(
            quizId = quizId,
            correctAnswer = correctAnswer,
            correctCount = correctUserPositions.size,
            totalParticipants = 10, // 가정값, 실제로는 서버에서 전체 참가자 수 받아야 함
            correctUserIds = correctUserPositions.map { it.userId }
        )

        val currentResults = _quizResults.value.toMutableList()
        currentResults.add(newResult)
        _quizResults.value = currentResults

        // 전체 게임 점수 업데이트
        updateGameScore()
    }

    /**
     * 보상 애니메이션 표시
     */
    private fun showRewardAnimation(correctUserPositions: List<UserPositionDto>) {
        println("DEBUG: showRewardAnimation 호출됨 - 정답자 수: ${correctUserPositions.size}")

        // 정답자가 있으면 정답 애니메이션 표시
        if (correctUserPositions.isNotEmpty()) {
            _userPosition.value = Pair(correctUserPositions[0].x, correctUserPositions[0].y)
            _showRewardAnimation.value = true
            // 현재 퀴즈의 정답을 전달 (O 또는 X)
            val currentQuiz = currentQuiz.value
            _animationType.value = currentQuiz?.answer ?: "O"
            println("DEBUG: 정답 애니메이션 상태를 true로 설정했습니다 - 정답: ${currentQuiz?.answer}")
            // 애니메이션이 끝나면 해설 화면으로 전환 (화면에서 처리)
        } else {
            // 정답자가 없으면 오답 애니메이션 표시
            _showRewardAnimation.value = true
            _animationType.value = "WRONG"
            println("DEBUG: 오답 애니메이션 상태를 true로 설정했습니다")
            // 애니메이션이 끝나면 해설 화면으로 전환 (화면에서 처리)
        }
    }

    /**
     * 해설 화면으로 전환
     */
    fun showExplanation() {
        _isQuizActive.value = false
        _showRewardAnimation.value = false

        // 해설 TTS 생성 및 재생
        generateExplanationTTS()

        println("DEBUG: 해설 화면으로 전환")
    }

    /**
     * 전체 게임 점수 업데이트
     */
    private fun updateGameScore() {
        val session = _quizSession.value ?: return
        val results = _quizResults.value

        val totalCorrect = results.sumOf { it.correctCount }
        val completed = results.size

        _gameScore.value = GameScore(
            totalQuizzes = session.quizzes.size,
            completedQuizzes = completed,
            totalCorrectAnswers = totalCorrect,
            quizResults = results
        )
    }

    /**
     * 순위 계산 (상위 3명의 사용자 ID)
     */
    fun getTop3Rankings(): List<Pair<Int, Int>> { // (userId, 맞은 문제 수)
        val userScores = mutableMapOf<Int, Int>()

        _quizResults.value.forEach { result ->
            result.correctUserIds.forEach { userId ->
                userScores[userId] = userScores.getOrDefault(userId, 0) + 1
            }
        }

        return userScores.toList()
            .sortedByDescending { it.second }
            .take(3)
    }

    /**
     * 다음 퀴즈로 이동
     */
    fun nextQuiz() {
        val session = _quizSession.value
        //TODO: 문제 수 3 문제로 조정
        if (session != null && _currentQuizIndex.value < session.quizzes.size - 1) {
//        if (session != null && _currentQuizIndex.value < 2) {
            _currentQuizIndex.value += 1
            _isAnswerSubmitted.value = false // 답변 제출 상태 초기화
            startCurrentQuiz() // 다음 문제 타이머 시작
        } else {
            // 마지막 문제를 넘긴 경우: 서버에 GAME_FINISH 요청 전송 후 로딩 상태로 전환
            if (isGameFinishRequested) {
                // 이미 전송 요청됨 (자동 진행/버튼 중복 방지)
                return
            }
            val sessionKey = startGameUseCase.getSessionKey()
            val lastQuizId = session?.quizzes?.lastOrNull()?.id

            // 더 이상 퀴즈 진행 없음
            stopLiveStreaming()

            if (sessionKey != null && lastQuizId != null) {
                _isLoading.value = true
                val imageData = _lastCapturedImageBase64.value ?: ""
                println("DEBUG: GAME_FINISH 전송 - quizId: $lastQuizId, hasImage: ${imageData.isNotEmpty()}")
                isGameFinishRequested = true
                gameActionUseCase.sendGameFinish(sessionKey, lastQuizId, imageData)
            } else {
                // 세션키/퀴즈ID가 없으면 즉시 게임 종료 처리
                _gameState.value = GameState(isGameActive = false, isGameFinished = true)
            }
        }
    }

    fun resetConnectionState() {
        _gameState.value = GameState(isGameActive = false, isGameFinished = false)
        _quizSession.value = null
        _currentQuizIndex.value = 0
        _errorMessage.value = null
        _timeLeft.value = 10
        _isQuizActive.value = false
        _gameScore.value = GameScore(0, 0, 0, emptyList())
        _quizResults.value = emptyList()
        _lastCapturedImageBase64.value = null
        _showRewardAnimation.value = false
        _userPosition.value = null
        _animationType.value = null
        isGameFinishRequested = false
    }

    /**
     * 게임 재시작
     */
    fun restartGame() {
        _quizSession.value = null
        _currentQuizIndex.value = 0
        _gameState.value = GameState()
        _errorMessage.value = null
        gameActionUseCase.disconnectWebSocket()
        resetConnectionState()
        connectToGame() // 게임 다시 시작
        isGameFinishRequested = false
    }

    /**
     * 게임 이벤트 관찰
     */
    private fun observeGameEvents() {
        viewModelScope.launch {
            gameEvents.collect { event ->
                when (event) {
                    is GameEvent.ConnectionEstablished -> {
                        _isLoading.value = false
                        // 연결 성공. GAME_START 이벤트를 기다림
                    }

                    is GameEvent.GameStart -> {
                        // DTO 리스트를 엔티티 리스트로 변환
                        val quizzes = event.quizList.map { it.toDomain() }
                        val session = QuizSession(quizzes = quizzes, sessionKey = event.sessionKey)

                        // 세션키 저장
                        startGameUseCase.saveSessionKey(session.sessionKey)
                        // 퀴즈 세션 저장
                        _quizSession.value = session
                        _currentQuizIndex.value = 0
                        _isLoading.value = false
                    }

                    is GameEvent.SubmitResult -> {
                        println("DEBUG: SUBMIT_RESULT 이벤트 수신 - 퀴즈 ID: ${event.quizId}, 정답: ${event.correctAnswer}, 정답자 수: ${event.correctUserPositions.size}")
                        // 퀴즈 결과 이벤트 처리 - 성적 기록
                        recordQuizResult(
                            event.quizId,
                            event.correctAnswer,
                            event.correctUserPositions
                        )

                        // 보상 애니메이션 표시
                        showRewardAnimation(event.correctUserPositions)

                        // SUBMIT_ANSWER 응답을 받았으므로 다음 문제 버튼 활성화
                        _isAnswerSubmitted.value = true
                    }

                    is GameEvent.AnalysisResult -> {
                        // 서버로부터 이미지 분석 결과 메시지 수신 (현재는 특별한 처리 없음)
                    }

                    is GameEvent.Error -> {
                        handleGameError(event.message)
                    }

                    GameEvent.Unknown -> {
                        // 알 수 없는 이벤트
                    }

                    is GameEvent.GameFinish -> {
                        println("DEBUG: GAME_FINISH 이벤트 수신")
                        // 더 이상 프레임 전송/타이머가 동작하지 않도록 즉시 중지
                        stopLiveStreaming()

                        // 프로필 맵 구성 및 최종 TOP3 계산
                        val profilesMap = buildProfilesMap(event)
                        _finishProfiles.value = profilesMap

                        val top3 = computeFinalTop3WithProfiles(profilesMap)
                        _finalTop3.value = top3

                        // 로딩 해제 및 게임 종료 처리
                        _isLoading.value = false
                        _gameState.value = GameState(isGameActive = false, isGameFinished = true)

                        resetTTSState()

                        // REST로 게임 종료 보고
                        viewModelScope.launch {
                            try {
                                val sessionKey = startGameUseCase.getSessionKey()
                                if (sessionKey != null) {
                                    gameActionUseCase.reportGameFinish(sessionKey)
                                    println("DEBUG: finishOXGame 호출 성공")
                                } else {
                                    println("WARN: finishOXGame 호출 불가 - sessionKey or userId null")
                                }
                            } catch (e: Exception) {
                                println("ERROR: finishOXGame 호출 실패 - ${e.message}")
                            }
                        }
                    }
                }
            }
        }
    }

    /**
     * 라이브 전송(프레임/타이머)을 모두 중단하고 퀴즈 활성 상태를 종료합니다.
     */
    private fun stopLiveStreaming() {
        _isQuizActive.value = false
        imageTransmissionJob?.cancel()
        quizTimerJob?.cancel()
    }

    /**
     * 서버에서 받은 GameFinish 이벤트에서 프로필 맵을 구성합니다.
     */
    private fun buildProfilesMap(event: GameEvent.GameFinish): Map<Int, String> {
        return event.profiles
            .filter { it.base64.isNotBlank() }
            .associate { it.userId to it.base64 }
    }

    /**
     * 현 시점까지의 정답 기록으로 상위 3명을 계산하고, 프로필이 있는 유저만 남깁니다.
     */
    private fun computeFinalTop3WithProfiles(profilesMap: Map<Int, String>): List<Pair<Int, Int>> {
        return getTop3Rankings().filter { (userId, _) -> profilesMap.containsKey(userId) }
    }

    /**
     * 게임 진행 중 발생하는 오류를 처리하는 공통 함수
     */
    private fun handleGameError(errorMessage: String) {
        _isLoading.value = false
        // 모든 타이머와 작업 중지
        imageTransmissionJob?.cancel()
        quizTimerJob?.cancel()

        // 게임 상태를 비활성화
        _isQuizActive.value = false
        _gameState.value = GameState(isGameActive = false)

        // 에러 메시지 설정
        _errorMessage.value = errorMessage
    }

    /**
     * 문제 TTS 생성 및 재생
     */
    fun generateQuestionTTS() {
        val currentQuiz = currentQuiz.value ?: return

        viewModelScope.launch {
            _ttsState.value = TTSState.Loading

            val result = ttsUseCase.generateTTS(
                text = currentQuiz.question
            )

            _ttsState.value = when {
                result.isSuccess -> TTSState.Success(result.getOrNull()!!)
                result.isFailure -> TTSState.Error(result.exceptionOrNull()?.message ?: "TTS 생성 실패")
                else -> TTSState.Error("알 수 없는 오류")
            }
        }
    }

    /**
     * 해설 TTS 생성 및 재생
     */
    fun generateExplanationTTS() {
        val currentQuiz = currentQuiz.value ?: return

        viewModelScope.launch {
            _ttsState.value = TTSState.Loading

            val result = ttsUseCase.generateTTS(
                text = currentQuiz.description
            )

            _ttsState.value = when {
                result.isSuccess -> TTSState.Success(result.getOrNull()!!)
                result.isFailure -> TTSState.Error(result.exceptionOrNull()?.message ?: "TTS 생성 실패")
                else -> TTSState.Error("알 수 없는 오류")
            }
        }
    }

    /**
     * TTS 상태 초기화
     */
    fun resetTTSState() {
        _ttsState.value = TTSState.Idle
        // TTS가 완료되면 타이머를 다시 시작
        if (_isQuizActive.value && _timeLeft.value > 0) {
            startQuizTimer()
        }
    }

    private fun resizeAndCompressImage(
        file: File,
        targetWidth: Int,
        targetHeight: Int,
        quality: Int
    ): ByteArray {
        // 1. 파일을 비트맵으로 디코딩
        val originalBitmap = BitmapFactory.decodeFile(file.absolutePath)
            ?: throw IllegalStateException("Failed to decode image file.")

        // 2. 원하는 크기로 비트맵 리사이즈
        val resizedBitmap =
            Bitmap.createScaledBitmap(originalBitmap, targetWidth, targetHeight, true)

        // 3. 비트맵을 JPEG으로 압축
        val outputStream = ByteArrayOutputStream()
        resizedBitmap.compress(Bitmap.CompressFormat.JPEG, quality, outputStream)

        // 4. 압축된 데이터를 ByteArray로 반환
        return outputStream.toByteArray()
    }

    override fun onCleared() {
        super.onCleared()
        gameActionUseCase.disconnectWebSocket()
    }
}
