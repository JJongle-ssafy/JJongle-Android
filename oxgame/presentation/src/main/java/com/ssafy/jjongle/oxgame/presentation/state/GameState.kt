package com.ssafy.jjongle.oxgame.presentation.state

/**
 * GameState 화면이 구독하는 상태 모델입니다.
 *
 * - 계층: oxgame/presentation
 * - 책임: 렌더링에 필요한 값을 한곳에 모아 UI와 상태 변경 로직을 분리합니다.
 */
data class GameState(
    val isLoading: Boolean = false,
    val error: String? = null,
    val capturedImageUri: String? = null,
    val isGameActive: Boolean = false,
    val isGameFinished: Boolean = false
) 
