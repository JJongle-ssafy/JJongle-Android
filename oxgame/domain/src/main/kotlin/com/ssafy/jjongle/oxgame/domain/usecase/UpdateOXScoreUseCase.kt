package com.ssafy.jjongle.oxgame.domain.usecase

import com.ssafy.jjongle.oxgame.entity.GameScore
import com.ssafy.jjongle.oxgame.entity.OXScoreUpdate
import com.ssafy.jjongle.oxgame.entity.QuizResult
import com.ssafy.jjongle.oxgame.entity.QuizSession
import com.ssafy.jjongle.oxgame.entity.UserPosition
import javax.inject.Inject
import kotlinx.collections.immutable.toPersistentList

/**
 * Update OXScore 시나리오를 실행하는 domain 계층 유스케이스입니다.
 *
 * ViewModel이 repository 세부 구현을 알지 않고 하나의 사용자 흐름이나 비즈니스 작업만 호출하도록 합니다.
 */
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
