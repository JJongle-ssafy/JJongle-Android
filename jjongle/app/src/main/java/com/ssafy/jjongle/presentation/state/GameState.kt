package com.ssafy.jjongle.presentation.state

import com.ssafy.jjongle.domain.entity.GameResult

data class GameState(
    val isLoading: Boolean = false,
    val gameResult: GameResult? = null,
    val error: String? = null,
    val capturedImageUri: String? = null,
    val isGameActive: Boolean = false,
    val isGameFinished: Boolean = false
) 