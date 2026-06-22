package com.ssafy.jjongle.domain.usecase

import com.ssafy.jjongle.domain.entity.GameScore
import com.ssafy.jjongle.domain.entity.OXScoreUpdate
import com.ssafy.jjongle.domain.entity.QuizResult
import com.ssafy.jjongle.domain.entity.QuizSession
import com.ssafy.jjongle.domain.entity.UserPosition
import javax.inject.Inject

class UpdateOXScoreUseCase @Inject constructor() {
    operator fun invoke(
        session: QuizSession,
        currentResults: List<QuizResult>,
        quizId: Int,
        correctAnswer: String,
        correctUserPositions: List<UserPosition>,
        totalParticipants: Int
    ): OXScoreUpdate {
        val nextResults = currentResults + QuizResult(
            quizId = quizId,
            correctAnswer = correctAnswer,
            correctCount = correctUserPositions.size,
            totalParticipants = totalParticipants,
            correctUserIds = correctUserPositions.map { it.userId }
        )

        val totalCorrect = nextResults.sumOf { it.correctCount }
        val gameScore = GameScore(
            totalQuizzes = session.quizzes.size,
            completedQuizzes = nextResults.size,
            totalCorrectAnswers = totalCorrect,
            quizResults = nextResults
        )

        return OXScoreUpdate(
            quizResults = nextResults,
            gameScore = gameScore
        )
    }
}
