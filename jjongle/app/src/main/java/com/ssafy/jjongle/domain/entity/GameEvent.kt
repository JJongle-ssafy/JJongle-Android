package com.ssafy.jjongle.domain.entity

sealed class GameEvent {
    data class GameStart(val quizzes: List<Quiz>, val sessionKey: String) : GameEvent()

    data class SubmitResult(
        val quizId: Int,
        val correctAnswer: String,
        val correctUserPositions: List<UserPosition>
    ) : GameEvent()

    data class GameFinish(val profiles: List<GameProfileImage>) : GameEvent()
    data class Error(val message: String) : GameEvent()
    data object Unknown : GameEvent()
}
