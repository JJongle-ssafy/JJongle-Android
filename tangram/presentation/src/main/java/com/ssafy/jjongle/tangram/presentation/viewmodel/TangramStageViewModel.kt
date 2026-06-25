package com.ssafy.jjongle.tangram.presentation.viewmodel

import androidx.lifecycle.viewModelScope
import com.ssafy.jjongle.tangram.domain.usecase.TangramGameUseCase
import com.ssafy.jjongle.common.presentation.mvi.MviIntent
import com.ssafy.jjongle.common.presentation.mvi.MviViewModel
import com.ssafy.jjongle.common.presentation.mvi.ReducerEvent
import com.ssafy.jjongle.common.presentation.mvi.UiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.collections.immutable.ImmutableSet
import kotlinx.collections.immutable.persistentSetOf
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.util.*
import javax.inject.Inject

/**
 * Stage Position는 탱그램에서 사용하는 위치 정보를 담는 값입니다.
 *
 * 좌표를 Pair나 원시 숫자로 흩뿌리지 않고 의미 있는 도메인 값으로 전달합니다.
 */
data class StagePosition(
    val stageId: Int,
    val x: Float,
    val y: Float
)

/**
 * 탱그램 화면을 렌더링하는 데 필요한 값을 담는 상태 스냅샷입니다.
 *
 * 여러 값을 화면에서 따로 수집하지 않도록 한 모델로 묶어, 상태 변경 지점을 ViewModel 안에서 추적할 수 있게 합니다.
 */
data class TangramStageUiState(
    val currentStage: Int = 5,
    val currentChallengeStageId: Int = 1,
    val characterX: Float = 140f,
    val characterY: Float = 250f,
    val isCharacterMoving: Boolean = false,
    val unlockedStages: ImmutableSet<Int> = persistentSetOf(1, 2, 3),
    val completedStages: ImmutableSet<Int> = persistentSetOf(),
) : UiState {
    companion object {
        val empty = TangramStageUiState()
    }
}

/**
 * 탱그램 화면에서 ViewModel로 전달되는 사용자 입력과 화면 이벤트입니다.
 *
 * 버튼 클릭, 화면 진입, 선택 변경 같은 입력을 타입으로 분리해 상태 변경의 시작점을 명확히 남깁니다.
 */
sealed interface TangramStageIntent : MviIntent {
    data class MoveToStage(val stageId: Int) : TangramStageIntent
}

/**
 * Tangram Stage Reducer Event는 탱그램 진행 중 발생한 도메인 이벤트입니다.
 *
 * 이벤트 종류를 타입으로 나눠 ViewModel이나 엔진이 문자열 분기 없이 게임 흐름을 처리하게 합니다.
 */
sealed interface TangramStageReducerEvent : ReducerEvent {
    data class ChallengeStageLoaded(
        val stageId: Int,
        val x: Float,
        val y: Float,
    ) : TangramStageReducerEvent

    data object MovementStarted : TangramStageReducerEvent

    data class CharacterPositionChanged(
        val x: Float,
        val y: Float,
    ) : TangramStageReducerEvent

    data class CurrentStageChanged(
        val stageId: Int,
    ) : TangramStageReducerEvent

    data object MovementFinished : TangramStageReducerEvent
}

/**
 * Tangram Stage 화면의 사용자 입력과 비동기 결과를 UI 상태로 변환하는 ViewModel입니다.
 *
 * UseCase 호출, 오류 처리, 상태 전이를 한곳에 모아 Compose 화면은 상태 구독과 Intent 전달에 집중하도록 합니다.
 */
@HiltViewModel
class TangramStageViewModel @Inject constructor(
    private val tangramGameUseCase: TangramGameUseCase,
) : MviViewModel<TangramStageIntent, TangramStageUiState, TangramStageReducerEvent>(
    initialState = TangramStageUiState.empty
) {

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

    // 초기화
    init {
        initializeGame()
    }

    override fun onIntent(intent: TangramStageIntent) {
        when (intent) {
            is TangramStageIntent.MoveToStage -> moveToStage(intent.stageId)
        }
    }

    override fun reduce(
        state: TangramStageUiState,
        event: TangramStageReducerEvent,
    ): TangramStageUiState {
        return when (event) {
            is TangramStageReducerEvent.ChallengeStageLoaded -> state.copy(
                currentStage = event.stageId,
                currentChallengeStageId = event.stageId,
                characterX = event.x,
                characterY = event.y,
            )

            TangramStageReducerEvent.MovementStarted -> state.copy(
                isCharacterMoving = true,
            )

            is TangramStageReducerEvent.CharacterPositionChanged -> state.copy(
                characterX = event.x,
                characterY = event.y,
            )

            is TangramStageReducerEvent.CurrentStageChanged -> state.copy(
                currentStage = event.stageId,
            )

            TangramStageReducerEvent.MovementFinished -> state.copy(
                isCharacterMoving = false,
            )
        }
    }

    private fun initializeGame() {
        viewModelScope.launch {
            tangramGameUseCase.getCurrentChallengeStageId()
                .onSuccess { challengeStageId ->
                    val targetStage = stagePositions.find { it.stageId == challengeStageId } ?: return@onSuccess
                    dispatch(
                        TangramStageReducerEvent.ChallengeStageLoaded(
                            stageId = challengeStageId,
                            x = targetStage.x,
                            y = targetStage.y,
                        )
                    )
                }
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
            val path = queue.removeFirst()
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
    private fun moveToStage(targetStageId: Int) {
        if (currentState.isCharacterMoving || targetStageId == currentState.currentStage) {
            return
        }

        // 스테이지 접근 가능 여부 확인 (현재 도전 스테이지 ID 기반)
        if (targetStageId > currentState.currentChallengeStageId) {
            // 접근 불가능한 스테이지
            return
        }

        viewModelScope.launch {
            dispatch(TangramStageReducerEvent.MovementStarted)

            // 경로 찾기
            val path = findPath(currentState.currentStage, targetStageId)

            if (path.isNotEmpty()) {
                // 경로를 따라 순차적으로 이동
                moveAlongPath(path)
                dispatch(TangramStageReducerEvent.CurrentStageChanged(targetStageId))

                // 이동 완료 후 딜레이
                delay(MOVEMENT_DELAY)

                // 스테이지 진입
                enterStage(targetStageId)
            }

            dispatch(TangramStageReducerEvent.MovementFinished)
        }
    }

    // 경로를 따라 순차적으로 이동
    private suspend fun moveAlongPath(path: List<Int>) {
        for (i in 1 until path.size) {
            val targetStage = stagePositions.find { it.stageId == path[i] } ?: continue

            // 위치 업데이트
            dispatch(
                TangramStageReducerEvent.CharacterPositionChanged(
                    x = targetStage.x,
                    y = targetStage.y,
                )
            )

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

    override fun onCleared() {
        super.onCleared()
        dispatch(TangramStageReducerEvent.MovementFinished)
    }
}
