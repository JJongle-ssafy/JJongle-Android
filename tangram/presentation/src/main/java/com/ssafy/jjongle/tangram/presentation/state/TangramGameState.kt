package com.ssafy.jjongle.tangram.presentation.state

import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.ImmutableSet
import kotlinx.collections.immutable.persistentListOf
import kotlinx.collections.immutable.persistentSetOf

/**
 * 탱그램 화면을 렌더링하는 데 필요한 값을 담는 상태 스냅샷입니다.
 *
 * 여러 값을 화면에서 따로 수집하지 않도록 한 모델로 묶어, 상태 변경 지점을 ViewModel 안에서 추적할 수 있게 합니다.
 */
data class TangramGameState(
    val isGameActive: Boolean = false,
    val isGameFinished: Boolean = false,
    val currentStageId: Int = 1, // 기본값 (initializeGame에서 실제 도전 스테이지로 설정됨)
    val isCharacterMoving: Boolean = false,
    val characterX: Float = 140f, // 기본값 (스테이지 5 위치)
    val characterY: Float = 250f, // 기본값 (스테이지 5 위치)
    val targetStageId: Int = 0,
    val movementPath: ImmutableList<Int> = persistentListOf(),
    val unlockedStages: ImmutableSet<Int> = persistentSetOf(1), // 잠금 해제된 스테이지들
    val completedStages: ImmutableSet<Int> = persistentSetOf(), // 완료한 스테이지들
    val currentChallengeStageId: Int = 1 // API에서 받을 현재 도전 가능한 스테이지 ID
)
