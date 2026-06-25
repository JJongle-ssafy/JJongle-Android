package com.ssafy.jjongle.tangram.presentation.state

import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.ImmutableSet
import kotlinx.collections.immutable.persistentListOf
import kotlinx.collections.immutable.persistentSetOf

/**
 * TangramGameState 화면이 구독하는 상태 모델입니다.
 *
 * - 계층: tangram/presentation
 * - 책임: 렌더링에 필요한 값을 한곳에 모아 UI와 상태 변경 로직을 분리합니다.
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
