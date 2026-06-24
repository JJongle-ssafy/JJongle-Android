package com.ssafy.jjongle.oxgame.presentation.viewmodel

import androidx.lifecycle.viewModelScope
import com.ssafy.jjongle.oxgame.entity.GameErrorEvent
import com.ssafy.jjongle.oxgame.entity.GameFinishEvent
import com.ssafy.jjongle.oxgame.entity.GameStartEvent
import com.ssafy.jjongle.oxgame.entity.GameScore
import com.ssafy.jjongle.oxgame.entity.Quiz
import com.ssafy.jjongle.oxgame.entity.QuizResult
import com.ssafy.jjongle.oxgame.entity.QuizSession
import com.ssafy.jjongle.oxgame.entity.SubmitResultEvent
import com.ssafy.jjongle.oxgame.entity.UnknownGameEvent
import com.ssafy.jjongle.oxgame.entity.UserPosition
import com.ssafy.jjongle.oxgame.domain.usecase.GameActionUseCase
import com.ssafy.jjongle.oxgame.domain.usecase.StartOXGameUseCase
import com.ssafy.jjongle.oxgame.domain.usecase.CalculateOXRankingsUseCase
import com.ssafy.jjongle.oxgame.domain.usecase.UpdateOXScoreUseCase
import com.ssafy.jjongle.common.presentation.mvi.MviViewModel
import com.ssafy.jjongle.oxgame.presentation.state.GameState
import com.ssafy.jjongle.oxgame.presentation.state.OXGameUiState
import com.ssafy.jjongle.oxgame.presentation.vision.OXAnswerArea
import com.ssafy.jjongle.oxgame.presentation.vision.OXParticipantProfileCache
import com.ssafy.jjongle.oxgame.presentation.vision.OXTrackedFace
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.collections.immutable.persistentListOf
import kotlinx.collections.immutable.toPersistentList
import kotlinx.collections.immutable.toPersistentMap
import javax.inject.Inject

@HiltViewModel
class OXGameViewModel @Inject constructor(
    private val startGameUseCase: StartOXGameUseCase,
    private val gameActionUseCase: GameActionUseCase,
    private val updateOXScoreUseCase: UpdateOXScoreUseCase,
    private val calculateOXRankingsUseCase: CalculateOXRankingsUseCase
) : MviViewModel<OXGameIntent, OXGameUiState, OXGameReducerEvent>(OXGameUiState.empty) {

    // OX 게임 이벤트/연결 상태
    private val gameEvents = gameActionUseCase.gameEvents

    // 타이머 작업들
    private var quizTimerJob: Job? = null
    private val participantProfileCache = OXParticipantProfileCache()
    private var latestTrackedFaces: List<OXTrackedFace> = emptyList()

    // 중복 GAME_FINISH 전송 방지 플래그
    private var isGameFinishRequested: Boolean = false


    init {
        observeConnectionState()
        observeGameEvents()
    }

    override fun onIntent(intent: OXGameIntent) {
        when (intent) {
            OXGameIntent.EnterGame -> {
                resetConnectionState()
                connectToGame()
            }
            OXGameIntent.ConnectToGame -> connectToGame()
            OXGameIntent.StartCurrentQuiz -> startCurrentQuiz()
            OXGameIntent.ClearError -> clearError()
            OXGameIntent.RestartGame -> restartGame()
            OXGameIntent.NextQuiz -> nextQuiz()
            OXGameIntent.ShowExplanation -> showExplanation()
            is OXGameIntent.UpdateTrackedFaces -> updateTrackedFaces(intent.faces)
        }
    }

    override fun reduce(state: OXGameUiState, event: OXGameReducerEvent): OXGameUiState {
        return when (event) {
            is OXGameReducerEvent.ConnectionStateChanged -> state.copy(connectionState = event.connectionState)
            OXGameReducerEvent.LoadingStarted -> state.copy(isLoading = true, errorMessage = null)
            is OXGameReducerEvent.LoadingChanged -> state.copy(isLoading = event.isLoading)
            is OXGameReducerEvent.Failed -> state.copy(isLoading = false, errorMessage = event.message)
            is OXGameReducerEvent.ErrorMessageChanged -> state.copy(errorMessage = event.message)
            is OXGameReducerEvent.GameStateChanged -> state.copy(gameState = event.gameState)
            is OXGameReducerEvent.QuizSessionChanged -> state.copy(quizSession = event.quizSession)
            is OXGameReducerEvent.QuizActiveChanged -> state.copy(isQuizActive = event.isActive)
            is OXGameReducerEvent.TimeLeftChanged -> state.copy(timeLeft = event.timeLeft)
            is OXGameReducerEvent.CurrentQuizIndexChanged -> state.copy(currentQuizIndex = event.index)
            is OXGameReducerEvent.AnswerSubmittedChanged -> state.copy(isAnswerSubmitted = event.isSubmitted)
            is OXGameReducerEvent.RewardAnimationChanged -> state.copy(showRewardAnimation = event.isVisible)
            is OXGameReducerEvent.AnimationTypeChanged -> state.copy(animationType = event.type)
            is OXGameReducerEvent.UserPositionChanged -> state.copy(userPosition = event.position)
            is OXGameReducerEvent.GameScoreChanged -> state.copy(gameScore = event.gameScore)
            is OXGameReducerEvent.QuizResultsChanged -> state.copy(quizResults = event.quizResults)
            is OXGameReducerEvent.FinalTop3Changed -> state.copy(finalTop3 = event.finalTop3)
            is OXGameReducerEvent.FinishProfilesChanged -> state.copy(finishProfiles = event.finishProfiles)
        }
    }

    private fun updateLoading(isLoading: Boolean) {
        dispatch(OXGameReducerEvent.LoadingChanged(isLoading))
    }

    private fun updateGameState(gameState: GameState) {
        dispatch(OXGameReducerEvent.GameStateChanged(gameState))
    }

    private fun updateQuizSession(quizSession: QuizSession?) {
        dispatch(OXGameReducerEvent.QuizSessionChanged(quizSession))
    }

    private fun updateQuizActive(isActive: Boolean) {
        dispatch(OXGameReducerEvent.QuizActiveChanged(isActive))
    }

    private fun updateTimeLeft(timeLeft: Int) {
        dispatch(OXGameReducerEvent.TimeLeftChanged(timeLeft))
    }

    private fun updateCurrentQuizIndex(index: Int) {
        dispatch(OXGameReducerEvent.CurrentQuizIndexChanged(index))
    }

    private fun updateAnswerSubmitted(isSubmitted: Boolean) {
        dispatch(OXGameReducerEvent.AnswerSubmittedChanged(isSubmitted))
    }

    private fun updateRewardAnimation(isVisible: Boolean) {
        dispatch(OXGameReducerEvent.RewardAnimationChanged(isVisible))
    }

    private fun updateAnimationType(type: String?) {
        dispatch(OXGameReducerEvent.AnimationTypeChanged(type))
    }

    private fun updateUserPosition(position: Pair<Double, Double>?) {
        dispatch(OXGameReducerEvent.UserPositionChanged(position))
    }

    private fun updateGameScore(gameScore: GameScore) {
        dispatch(OXGameReducerEvent.GameScoreChanged(gameScore))
    }

    private fun updateQuizResults(quizResults: List<QuizResult>) {
        dispatch(OXGameReducerEvent.QuizResultsChanged(quizResults.toPersistentList()))
    }

    private fun updateFinalTop3(finalTop3: List<Pair<Int, Int>>) {
        dispatch(OXGameReducerEvent.FinalTop3Changed(finalTop3.toPersistentList()))
    }

    private fun updateFinishProfiles(finishProfiles: Map<Int, String>) {
        dispatch(OXGameReducerEvent.FinishProfilesChanged(finishProfiles.toPersistentMap()))
    }

    private fun updateErrorMessage(errorMessage: String?) {
        dispatch(OXGameReducerEvent.ErrorMessageChanged(errorMessage))
    }

    private fun failWithMessage(errorMessage: String?) {
        dispatch(OXGameReducerEvent.Failed(errorMessage))
    }

    private fun observeConnectionState() {
        viewModelScope.launch {
            gameActionUseCase.connectionState.collect { connectionState ->
                dispatch(OXGameReducerEvent.ConnectionStateChanged(connectionState))
            }
        }
    }

    /**
     * 로컬 OX 게임 세션을 시작합니다.
     */
    private fun connectToGame() {
        viewModelScope.launch {
            updateLoading(true)
            updateErrorMessage(null)
            startGameUseCase.startGameSession()
                .onFailure { error ->
                    failWithMessage(error.message ?: "OX 게임을 시작할 수 없습니다.")
                }
        }
    }

    /**
     * 에러 메시지 클리어
     */
    private fun clearError() {
        updateErrorMessage(null)
    }

    /**
     * 퀴즈 시작 - 타이머 시작과 얼굴 위치 추적 초기화
     */
    private fun startCurrentQuiz() {
        val currentQuiz = currentQuizSnapshot() ?: return

        updateGameState(GameState(isGameActive = true)) // 게임 활성화
        updateQuizActive(true)
        updateTimeLeft(10) // 10초 제한시간

        latestTrackedFaces = emptyList()

        // 퀴즈 타이머 시작
        startQuizTimer()

    }

    private fun updateTrackedFaces(faces: List<OXTrackedFace>) {
        if (!currentState.isQuizActive || currentState.gameState.isGameFinished) return
        latestTrackedFaces = faces
        updateFinishProfiles(participantProfileCache.updateFrom(faces))
    }

    /**
     * 퀴즈 타이머 시작 (10초 카운트다운)
     */
    private fun startQuizTimer() {
        quizTimerJob?.cancel()
        quizTimerJob = viewModelScope.launch {
            while (currentState.timeLeft > 0 && currentState.isQuizActive) {
                delay(1000) // 1초 대기

                updateTimeLeft(currentState.timeLeft - 1)
            }

            // 시간 종료 시 자동으로 답변 제출
            if (currentState.timeLeft <= 0) {
                submitFinalAnswer()
            }
        }
    }

    /**
     * 최종 답변 제출 (시간 종료 시 자동 호출)
     */
    private fun submitFinalAnswer() {
        val currentQuiz = currentQuizSnapshot() ?: return
        val sessionKey = startGameUseCase.getSessionKey() ?: return

        quizTimerJob?.cancel()

        val trackedFaces = latestTrackedFaces
        val profiles = participantProfileCache.updateFrom(trackedFaces)
        updateFinishProfiles(profiles)
        val oAreaUserPositions = trackedFaces
            .filter { it.area == OXAnswerArea.O }
            .map { it.toUserPosition() }
        val xAreaUserPositions = trackedFaces
            .filter { it.area == OXAnswerArea.X }
            .map { it.toUserPosition() }

        viewModelScope.launch {
            gameActionUseCase.sendSubmitAnswer(
                sessionKey = sessionKey,
                quizId = currentQuiz.id,
                oAreaUserPositions = oAreaUserPositions,
                xAreaUserPositions = xAreaUserPositions
            ).onSuccess {
                // 답변 제출 상태 초기화
                updateAnswerSubmitted(false)
            }.onFailure { error ->
                failWithMessage(error.message ?: "OX 정답을 제출할 수 없습니다.")
            }
        }

        // isQuizActive는 SubmitResult 이벤트를 받은 후에 false로 설정됨
        // 여기서는 false로 설정하지 않음
    }

    /**
     * 성적 기록 처리 (SubmitResult 이벤트 받았을 때)
     */
    private fun recordQuizResult(
        quizId: Int,
        correctAnswer: String,
        correctUserPositions: List<UserPosition>
    ) {
        val session = currentState.quizSession ?: return
        val scoreUpdate = updateOXScoreUseCase(
            session = session,
            currentResults = currentState.quizResults,
            quizId = quizId,
            correctAnswer = correctAnswer,
            correctUserPositions = correctUserPositions,
            totalParticipants = latestTrackedFaces.size
        )
        updateQuizResults(scoreUpdate.quizResults)
        updateGameScore(scoreUpdate.gameScore)
    }

    /**
     * 보상 애니메이션 표시
     */
    private fun showRewardAnimation(correctUserPositions: List<UserPosition>) {
        // 정답자가 있으면 정답 애니메이션 표시
        if (correctUserPositions.isNotEmpty()) {
            updateUserPosition(Pair(correctUserPositions[0].x, correctUserPositions[0].y))
            updateRewardAnimation(true)
            // 현재 퀴즈의 정답을 전달 (O 또는 X)
            val currentQuiz = currentQuizSnapshot()
            updateAnimationType(currentQuiz?.answer ?: "O")
            // 애니메이션이 끝나면 해설 화면으로 전환 (화면에서 처리)
        } else {
            // 정답자가 없으면 오답 애니메이션 표시
            updateRewardAnimation(true)
            updateAnimationType("WRONG")
            // 애니메이션이 끝나면 해설 화면으로 전환 (화면에서 처리)
        }
    }

    /**
     * 해설 화면으로 전환
     */
    private fun showExplanation() {
        updateQuizActive(false)
        updateRewardAnimation(false)

    }

    /**
     * 순위 계산 (상위 3명의 사용자 ID)
     */
    fun getTop3Rankings(): List<Pair<Int, Int>> { // (userId, 맞은 문제 수)
        return calculateOXRankingsUseCase(currentState.quizResults)
    }

    /**
     * 다음 퀴즈로 이동
     */
    private fun nextQuiz() {
        val session = currentState.quizSession
        if (session != null && currentState.currentQuizIndex < session.quizzes.size - 1) {
            updateCurrentQuizIndex(currentState.currentQuizIndex + 1)
            updateAnswerSubmitted(false) // 답변 제출 상태 초기화
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

    private fun resetConnectionState() {
        updateGameState(GameState(isGameActive = false, isGameFinished = false))
        updateQuizSession(null)
        updateCurrentQuizIndex(0)
        updateErrorMessage(null)
        updateTimeLeft(10)
        updateQuizActive(false)
        updateGameScore(GameScore(0, 0, 0, persistentListOf()))
        updateQuizResults(emptyList())
        latestTrackedFaces = emptyList()
        participantProfileCache.clear()
        updateFinishProfiles(emptyMap())
        updateFinalTop3(emptyList())
        updateRewardAnimation(false)
        updateUserPosition(null)
        updateAnimationType(null)
        isGameFinishRequested = false
    }

    /**
     * 게임 재시작
     */
    private fun restartGame() {
        updateQuizSession(null)
        updateCurrentQuizIndex(0)
        updateGameState(GameState())
        updateErrorMessage(null)
        gameActionUseCase.endGameSession()
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
                    is GameStartEvent -> {
                        val session = QuizSession(
                            sessionKey = event.sessionKey,
                            quizzes = event.quizzes,
                        )

                        // 세션키 저장
                        startGameUseCase.saveSessionKey(session.sessionKey)
                        // 퀴즈 세션 저장
                        updateQuizSession(session)
                        updateCurrentQuizIndex(0)
                        updateLoading(false)
                    }

                    is SubmitResultEvent -> {
                        // 퀴즈 결과 이벤트 처리 - 성적 기록
                        recordQuizResult(
                            event.quizId,
                            event.correctAnswer,
                            event.correctUserPositions
                        )

                        // 보상 애니메이션 표시
                        showRewardAnimation(event.correctUserPositions)

                        // 답변 제출 결과를 받았으므로 다음 문제 버튼 활성화
                        updateAnswerSubmitted(true)
                    }

                    is GameErrorEvent -> {
                        handleGameError(event.message)
                    }

                    UnknownGameEvent -> {
                        // 알 수 없는 이벤트
                    }

                    is GameFinishEvent -> {
                        // 더 이상 얼굴 추적/타이머가 동작하지 않도록 즉시 중지
                        stopLiveStreaming()

                        // 프로필 맵 구성 및 최종 TOP3 계산
                        val profilesMap = buildProfilesMap(event)
                        updateFinishProfiles(profilesMap)

                        val top3 = computeFinalTop3WithProfiles()
                        updateFinalTop3(top3)

                        // 로딩 해제 및 게임 종료 처리
                        updateLoading(false)
                        updateGameState(GameState(isGameActive = false, isGameFinished = true))

                        // 게임 종료 기록 저장
                        viewModelScope.launch {
                            reportGameFinish()
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
        updateQuizActive(false)
        quizTimerJob?.cancel()
    }

    private fun finishGameLocally() {
        stopLiveStreaming()

        val profilesMap = participantProfileCache.snapshot()
        updateFinishProfiles(profilesMap)
        updateFinalTop3(computeFinalTop3WithProfiles())
        updateLoading(false)
        updateGameState(GameState(isGameActive = false, isGameFinished = true))

        viewModelScope.launch {
            reportGameFinish()
        }
    }

    private suspend fun reportGameFinish() {
        val sessionKey = startGameUseCase.getSessionKey()
        if (sessionKey == null) {
            failWithMessage("게임 종료 세션을 찾을 수 없습니다.")
            return
        }

        gameActionUseCase.reportGameFinish(sessionKey)
            .onFailure {
                failWithMessage("게임 종료 기록 저장에 실패했습니다.")
            }
    }

    /**
     * 서버 GameFinish 이벤트에서 프로필 맵을 구성합니다.
     */
    private fun buildProfilesMap(event: GameFinishEvent): Map<Int, String> {
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
        updateLoading(false)
        quizTimerJob?.cancel()

        // 게임 상태를 비활성화
        updateQuizActive(false)
        updateGameState(GameState(isGameActive = false))

        // 에러 메시지 설정
        failWithMessage(errorMessage)
    }

    private fun currentQuizSnapshot(): Quiz? {
        return currentState.currentQuiz
    }

    override fun onCleared() {
        super.onCleared()
        gameActionUseCase.endGameSession()
    }

    private fun OXTrackedFace.toUserPosition(): UserPosition {
        return UserPosition(
            userId = participantId,
            x = x,
            y = y
        )
    }
}
