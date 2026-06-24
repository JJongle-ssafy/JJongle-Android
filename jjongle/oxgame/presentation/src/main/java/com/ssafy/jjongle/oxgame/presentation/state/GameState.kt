package com.ssafy.jjongle.oxgame.presentation.state

data class GameState(
    val isLoading: Boolean = false,
    val error: String? = null,
    val capturedImageUri: String? = null,
    val isGameActive: Boolean = false,
    val isGameFinished: Boolean = false
) 
