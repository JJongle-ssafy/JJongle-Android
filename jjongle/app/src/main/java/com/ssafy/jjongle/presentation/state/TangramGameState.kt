package com.ssafy.jjongle.presentation.state

data class TangramGameState(
    val isGameActive: Boolean = false,
    val isGameFinished: Boolean = false,
    val currentStageId: Int = 1, // 기본값 (initializeGame에서 실제 도전 스테이지로 설정됨)
    val isCharacterMoving: Boolean = false,
    val characterX: Float = 140f, // 기본값 (스테이지 5 위치)
    val characterY: Float = 250f, // 기본값 (스테이지 5 위치)
    val targetStageId: Int = 0,
    val movementPath: List<Int> = emptyList(),
    val unlockedStages: Set<Int> = setOf(1), // 잠금 해제된 스테이지들
    val completedStages: Set<Int> = emptySet(), // 완료한 스테이지들
    val currentChallengeStageId: Int = 1 // API에서 받을 현재 도전 가능한 스테이지 ID
)