package com.ssafy.jjongle.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ssafy.jjongle.data.local.AuthDataSource
import com.ssafy.jjongle.domain.usecase.TangramGameUseCase
import com.ssafy.jjongle.presentation.state.TangramGameState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.util.*
import javax.inject.Inject

data class StagePosition(
    val stageId: Int,
    val x: Float,
    val y: Float
)

@HiltViewModel
class TangramStageViewModel @Inject constructor(
    private val tangramGameUseCase: TangramGameUseCase,
    private val authDataSource: AuthDataSource
) : ViewModel() {

    companion object {
        const val MOVEMENT_DURATION = 800L // 각 스테이지 이동시간 (ms)
        const val MOVEMENT_DELAY = 300L // 이동 완료 후 딜레이
    }

    // 스테이지 위치 데이터
    private val stagePositions = listOf(
        StagePosition(1, 420f, 500f),
        StagePosition(2, 590f, 420f),
        StagePosition(3, 440f, 320f),
        StagePosition(4, 260f, 350f),
        StagePosition(5, 140f, 250f),
        StagePosition(6, 160f, 130f),
        StagePosition(7, 360f, 60f),
        StagePosition(8, 520f, 130f),
        StagePosition(9, 700f, 120f)
    )

    // 스테이지 연결 관계 - 순차적으로 연결
    private val stageConnections = mapOf(
        1 to listOf(2),
        2 to listOf(1, 3),
        3 to listOf(2, 4),
        4 to listOf(3, 5),
        5 to listOf(4, 6),
        6 to listOf(5, 7),
        7 to listOf(6, 8),
        8 to listOf(7, 9),
        9 to listOf(8)
    )

    // State Flow들
    private val _gameState = MutableStateFlow(TangramGameState())
    val gameState: StateFlow<TangramGameState> = _gameState.asStateFlow()

    private val _characterX = MutableStateFlow(140f) // 스테이지 5 위치
    val characterX: StateFlow<Float> = _characterX.asStateFlow()

    private val _characterY = MutableStateFlow(250f) // 스테이지 5 위치
    val characterY: StateFlow<Float> = _characterY.asStateFlow()

    private val _isCharacterMoving = MutableStateFlow(false)
    val isCharacterMoving: StateFlow<Boolean> = _isCharacterMoving.asStateFlow()

    private val _currentStage = MutableStateFlow(5) // 기본값을 스테이지 5로 설정
    val currentStage: StateFlow<Int> = _currentStage.asStateFlow()

    private val _unlockedStages = MutableStateFlow(setOf(1, 2, 3)) // 처음에 1,2,3 스테이지만 열려있다고 가정
    val unlockedStages: StateFlow<Set<Int>> = _unlockedStages.asStateFlow()

    private val _completedStages = MutableStateFlow<Set<Int>>(emptySet())
    val completedStages: StateFlow<Set<Int>> = _completedStages.asStateFlow()

    private val _currentChallengeStageId = MutableStateFlow(1)
    val currentChallengeStageId: StateFlow<Int> = _currentChallengeStageId.asStateFlow()

    // 초기화
    init {
        initializeGame()
    }

    private fun initializeGame() {
        viewModelScope.launch {
            try {
                // API에서 현재 도전 가능한 스테이지 ID 가져오기
                val challengeStageId = tangramGameUseCase.getCurrentChallengeStageId()
                _currentChallengeStageId.value = challengeStageId
                
                // 캐릭터 시작 위치를 현재 도전 스테이지로 설정
                setCharacterToStage(challengeStageId)
                _currentStage.value = challengeStageId
                
                updateGameState()
            } catch (e: Exception) {
                println("게임 초기화 실패: ${e.message}")
            }
        }
    }
    
    private fun setCharacterToStage(stageId: Int) {
        val targetStage = stagePositions.find { it.stageId == stageId }
        if (targetStage != null) {
            _characterX.value = targetStage.x
            _characterY.value = targetStage.y
        }
    }

    // BFS를 사용한 최단 경로 찾기
    private fun findPath(start: Int, end: Int): List<Int> {
        if (start == end) return listOf(start)

        val queue = LinkedList<List<Int>>()
        val visited = mutableSetOf<Int>()

        queue.offer(listOf(start))
        visited.add(start)

        while (queue.isNotEmpty()) {
            val path = queue.poll()
            val current = path.last()

            stageConnections[current]?.forEach { neighbor ->
                if (neighbor == end) {
                    return path + neighbor
                }

                if (neighbor !in visited) {
                    visited.add(neighbor)
                    queue.offer(path + neighbor)
                }
            }
        }

        return emptyList() // 경로를 찾을 수 없음
    }

    // 스테이지로 이동
    fun moveToStage(targetStageId: Int) {
        if (_isCharacterMoving.value || targetStageId == _currentStage.value) {
            return
        }

        // 스테이지 접근 가능 여부 확인 (현재 도전 스테이지 ID 기반)
        if (targetStageId > _currentChallengeStageId.value) {
            // 접근 불가능한 스테이지
            return
        }

        viewModelScope.launch {
            _isCharacterMoving.value = true

            // 경로 찾기
            val path = findPath(_currentStage.value, targetStageId)

            if (path.isNotEmpty()) {
                // 경로를 따라 순차적으로 이동
                moveAlongPath(path)
                _currentStage.value = targetStageId

                // 상태 업데이트
                updateGameState()

                // 이동 완료 후 딜레이
                delay(MOVEMENT_DELAY)

                // 스테이지 진입
                enterStage(targetStageId)
            }

            _isCharacterMoving.value = false
        }
    }

    // 경로를 따라 순차적으로 이동
    private suspend fun moveAlongPath(path: List<Int>) {
        for (i in 1 until path.size) {
            val targetStage = stagePositions.find { it.stageId == path[i] } ?: continue

            // 위치 업데이트
            _characterX.value = targetStage.x
            _characterY.value = targetStage.y

            delay(MOVEMENT_DURATION)
        }
    }

    // 스테이지 진입
    private fun enterStage(stageId: Int) {
        viewModelScope.launch {
            // 스테이지 진입 로직
            // 예: 탐험 게임 시작, 다른 화면으로 이동 등
        }
    }


    // 게임 상태 업데이트
    private fun updateGameState() {
        _gameState.value = _gameState.value.copy(
            currentStageId = _currentStage.value,
            isCharacterMoving = _isCharacterMoving.value,
            characterX = _characterX.value,
            characterY = _characterY.value,
            unlockedStages = _unlockedStages.value,
            completedStages = _completedStages.value,
            currentChallengeStageId = _currentChallengeStageId.value
        )
    }



    // API에서 현재 도전 가능한 스테이지 ID 설정
    fun updateCurrentChallengeStageId(stageId: Int) {
        _currentChallengeStageId.value = stageId
        updateGameState()
    }

    // 토큰 가져오기 메소드들
    fun getAccessToken(): String? {
        return authDataSource.getAccessToken()
    }

    fun getRefreshToken(): String? {
        return authDataSource.getRefreshToken()
    }

    override fun onCleared() {
        super.onCleared()
        _isCharacterMoving.value = false
    }
}