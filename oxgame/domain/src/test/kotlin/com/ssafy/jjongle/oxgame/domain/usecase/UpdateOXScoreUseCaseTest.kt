package com.ssafy.jjongle.oxgame.domain.usecase

import com.ssafy.jjongle.oxgame.entity.Quiz
import com.ssafy.jjongle.oxgame.entity.QuizResult
import com.ssafy.jjongle.oxgame.entity.QuizSession
import com.ssafy.jjongle.oxgame.entity.UserPosition
import kotlinx.collections.immutable.toPersistentList
import org.junit.Assert.assertEquals
import org.junit.Test

/**
 * UpdateOXScore의 아키텍처 또는 동작 계약을 검증하는 테스트입니다.
 *
 * - 계층: test
 * - 책임: 회귀를 막는 대표 시나리오와 모듈 경계 조건을 실행합니다.
 */
class UpdateOXScoreUseCaseTest {
    private val useCase = UpdateOXScoreUseCase()

    @Test
    fun invoke_appendsQuizResultAndRecalculatesGameScore() {
        val session = QuizSession(
            sessionKey = "session",
            quizzes = listOf(
                Quiz(1, "q1", "O", "d1"),
                Quiz(2, "q2", "X", "d2")
            ).toPersistentList()
        )
        val previous = listOf(
            QuizResult(
                quizId = 1,
                correctAnswer = "O",
                correctCount = 1,
                totalParticipants = 2,
                correctUserIds = listOf(1).toPersistentList()
            )
        )

        val update = useCase(
            session = session,
            currentResults = previous,
            quizId = 2,
            correctAnswer = "X",
            correctUserPositions = listOf(
                UserPosition(userId = 1, x = 10.0, y = 20.0),
                UserPosition(userId = 3, x = 30.0, y = 40.0)
            ),
            totalParticipants = 3
        )

        assertEquals(2, update.quizResults.size)
        assertEquals(listOf(1, 3), update.quizResults.last().correctUserIds)
        assertEquals(2, update.gameScore.totalQuizzes)
        assertEquals(2, update.gameScore.completedQuizzes)
        assertEquals(3, update.gameScore.totalCorrectAnswers)
    }
}
