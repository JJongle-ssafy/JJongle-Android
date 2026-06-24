package com.ssafy.jjongle.oxgame.domain.usecase

import com.ssafy.jjongle.oxgame.entity.GameScore
import com.ssafy.jjongle.oxgame.entity.OXScoreUpdate
import com.ssafy.jjongle.oxgame.entity.QuizResult
import com.ssafy.jjongle.oxgame.entity.QuizSession
import com.ssafy.jjongle.oxgame.entity.UserPosition
import javax.inject.Inject
import kotlinx.collections.immutable.toPersistentList

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
            correctUserIds = correctUserPositions.map { it.userId }.toPersistentList()
        )

        val totalCorrect = nextResults.sumOf { it.correctCount }
        val gameScore = GameScore(
            totalQuizzes = session.quizzes.size,
            completedQuizzes = nextResults.size,
            totalCorrectAnswers = totalCorrect,
            quizResults = nextResults.toPersistentList()
        )

        return OXScoreUpdate(
            quizResults = nextResults.toPersistentList(),
            gameScore = gameScore
        )
    }
}
