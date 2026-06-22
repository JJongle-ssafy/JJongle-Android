package com.ssafy.jjongle.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ssafy.jjongle.domain.entity.GameConnectionState
import com.ssafy.jjongle.domain.entity.GameEvent
import com.ssafy.jjongle.domain.entity.GameScore
import com.ssafy.jjongle.domain.entity.Quiz
import com.ssafy.jjongle.domain.entity.QuizResult
import com.ssafy.jjongle.domain.entity.QuizSession
import com.ssafy.jjongle.domain.entity.UserPosition
import com.ssafy.jjongle.domain.usecase.GameActionUseCase
import com.ssafy.jjongle.domain.usecase.StartOXGameUseCase
import com.ssafy.jjongle.domain.usecase.TTSUseCase
import com.ssafy.jjongle.domain.usecase.CalculateOXRankingsUseCase
import com.ssafy.jjongle.domain.usecase.UpdateOXScoreUseCase
import com.ssafy.jjongle.presentation.state.GameState
import com.ssafy.jjongle.presentation.state.TTSState
import com.ssafy.jjongle.presentation.vision.OXAnswerArea
import com.ssafy.jjongle.presentation.vision.OXParticipantProfileCache
import com.ssafy.jjongle.presentation.vision.OXTrackedFace
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
import javax.inject.Inject

@HiltViewModel
class OXGameViewModel @Inject constructor(
    private val startGameUseCase: StartOXGameUseCase,
    private val gameActionUseCase: GameActionUseCase,
    private val ttsUseCase: TTSUseCase,
    private val updateOXScoreUseCase: UpdateOXScoreUseCase,
    private val calculateOXRankingsUseCase: CalculateOXRankingsUseCase
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
    val connectionState: StateFlow<GameConnectionState> = gameActionUseCase.connectionState
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

    private val _latestTrackedFaces = MutableStateFlow<List<OXTrackedFace>>(emptyList())
    val latestTrackedFaces: StateFlow<List<OXTrackedFace>> = _latestTrackedFaces.asStateFlow()

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
    private var quizTimerJob: Job? = null
    private val participantProfileCache = OXParticipantProfileCache()

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
     * 퀴즈 시작 - 타이머 시작과 얼굴 위치 추적 초기화
     */
    fun startCurrentQuiz() {
        val currentQuiz = currentQuiz.value ?: return

        _gameState.value = GameState(isGameActive = true) // 게임 활성화
        _isQuizActive.value = true
        _timeLeft.value = 10 // 10초 제한시간

        _latestTrackedFaces.value = emptyList()

        // 문제 TTS 생성 및 재생
        generateQuestionTTS()

        // 퀴즈 타이머 시작
        startQuizTimer()

    }

    fun updateTrackedFaces(faces: List<OXTrackedFace>) {
        if (!_isQuizActive.value || _gameState.value.isGameFinished) return
        _latestTrackedFaces.value = faces
        _finishProfiles.value = participantProfileCache.updateFrom(faces)
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

        quizTimerJob?.cancel()

        val trackedFaces = _latestTrackedFaces.value
        val profiles = participantProfileCache.updateFrom(trackedFaces)
        _finishProfiles.value = profiles
        val oAreaUserPositions = trackedFaces
            .filter { it.area == OXAnswerArea.O }
            .map { it.toUserPosition() }
        val xAreaUserPositions = trackedFaces
            .filter { it.area == OXAnswerArea.X }
            .map { it.toUserPosition() }

        println("DEBUG: SUBMIT_ANSWER 전송 - 퀴즈 ID: ${currentQuiz.id}, O: ${oAreaUserPositions.size}, X: ${xAreaUserPositions.size}")
        gameActionUseCase.sendSubmitAnswer(
            sessionKey = sessionKey,
            quizId = currentQuiz.id,
            oAreaUserPositions = oAreaUserPositions,
            xAreaUserPositions = xAreaUserPositions
        )

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
        correctUserPositions: List<UserPosition>
    ) {
        val session = _quizSession.value ?: return
        val scoreUpdate = updateOXScoreUseCase(
            session = session,
            currentResults = _quizResults.value,
            quizId = quizId,
            correctAnswer = correctAnswer,
            correctUserPositions = correctUserPositions,
            totalParticipants = _latestTrackedFaces.value.size
        )
        _quizResults.value = scoreUpdate.quizResults
        _gameScore.value = scoreUpdate.gameScore
    }

    /**
     * 보상 애니메이션 표시
     */
    private fun showRewardAnimation(correctUserPositions: List<UserPosition>) {
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
     * 순위 계산 (상위 3명의 사용자 ID)
     */
    fun getTop3Rankings(): List<Pair<Int, Int>> { // (userId, 맞은 문제 수)
        return calculateOXRankingsUseCase(_quizResults.value)
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
            // 마지막 문제를 넘긴 경우: 로컬 프로필 캐시로 결과 화면 구성
            if (isGameFinishRequested) {
                // 이미 전송 요청됨 (자동 진행/버튼 중복 방지)
                return
            }
            isGameFinishRequested = true
            finishGameLocally()
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
        _latestTrackedFaces.value = emptyList()
        participantProfileCache.clear()
        _finishProfiles.value = emptyMap()
        _finalTop3.value = emptyList()
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
                    is GameEvent.GameStart -> {
                        val session = QuizSession(
                            quizzes = event.quizzes,
                            sessionKey = event.sessionKey
                        )

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

                    is GameEvent.Error -> {
                        handleGameError(event.message)
                    }

                    GameEvent.Unknown -> {
                        // 알 수 없는 이벤트
                    }

                    is GameEvent.GameFinish -> {
                        println("DEBUG: GAME_FINISH 이벤트 수신")
                        // 더 이상 얼굴 추적/타이머가 동작하지 않도록 즉시 중지
                        stopLiveStreaming()

                        // 프로필 맵 구성 및 최종 TOP3 계산
                        val profilesMap = buildProfilesMap(event)
                        _finishProfiles.value = profilesMap

                        val top3 = computeFinalTop3WithProfiles()
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
     * 얼굴 추적과 타이머를 중단하고 퀴즈 활성 상태를 종료합니다.
     */
    private fun stopLiveStreaming() {
        _isQuizActive.value = false
        quizTimerJob?.cancel()
    }

    private fun finishGameLocally() {
        stopLiveStreaming()

        val profilesMap = participantProfileCache.snapshot()
        _finishProfiles.value = profilesMap
        _finalTop3.value = computeFinalTop3WithProfiles()
        _isLoading.value = false
        _gameState.value = GameState(isGameActive = false, isGameFinished = true)
        resetTTSState()

        viewModelScope.launch {
            try {
                val sessionKey = startGameUseCase.getSessionKey()
                if (sessionKey != null) {
                    gameActionUseCase.reportGameFinish(sessionKey)
                    println("DEBUG: finishOXGame 호출 성공")
                } else {
                    println("WARN: finishOXGame 호출 불가 - sessionKey null")
                }
            } catch (e: Exception) {
                println("ERROR: finishOXGame 호출 실패 - ${e.message}")
            }
        }
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
    private fun computeFinalTop3WithProfiles(): List<Pair<Int, Int>> {
        return getTop3Rankings()
    }

    /**
     * 게임 진행 중 발생하는 오류를 처리하는 공통 함수
     */
    private fun handleGameError(errorMessage: String) {
        _isLoading.value = false
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

    override fun onCleared() {
        super.onCleared()
        gameActionUseCase.disconnectWebSocket()
    }

    private fun OXTrackedFace.toUserPosition(): UserPosition {
        return UserPosition(
            userId = participantId,
            x = x,
            y = y
        )
    }
}
