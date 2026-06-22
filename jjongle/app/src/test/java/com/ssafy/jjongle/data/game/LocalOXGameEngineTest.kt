package com.ssafy.jjongle.data.game

import com.ssafy.jjongle.domain.entity.OX
import com.ssafy.jjongle.domain.entity.Quiz
import com.ssafy.jjongle.domain.entity.UserPosition
import com.ssafy.jjongle.domain.repository.OXQuizRepository
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class LocalOXGameEngineTest {

    @Test
    fun startGame_returnsLocalSessionWithQuizzes() = runTest {
        val engine = LocalOXGameEngine(FakeOXQuizRepository())

        val event = engine.startGame()

        assertTrue(event.sessionKey.startsWith("local-"))
        assertEquals(2, event.quizzes.size)
    }

    @Test
    fun submitAnswer_returnsCorrectPositionsByQuizAnswer() = runTest {
        val engine = LocalOXGameEngine(FakeOXQuizRepository())
        val session = engine.startGame()

        val event = engine.submitAnswer(
            sessionKey = session.sessionKey,
            quizId = 1,
            oAreaUserPositions = listOf(UserPosition(userId = 1, x = 0.2, y = 0.4)),
            xAreaUserPositions = listOf(UserPosition(userId = 2, x = 0.8, y = 0.4))
        )

        assertEquals("O", event.correctAnswer)
        assertEquals(listOf(1), event.correctUserPositions.map { it.userId })
    }

    @Test
    fun buildWrongAnswerNotes_containsQuizWhenAnyParticipantIsWrong() = runTest {
        val engine = LocalOXGameEngine(FakeOXQuizRepository())
        val session = engine.startGame()
        engine.submitAnswer(
            sessionKey = session.sessionKey,
            quizId = 1,
            oAreaUserPositions = listOf(UserPosition(userId = 1, x = 0.2, y = 0.4)),
            xAreaUserPositions = listOf(UserPosition(userId = 2, x = 0.8, y = 0.4))
        )
        engine.submitAnswer(
            sessionKey = session.sessionKey,
            quizId = 2,
            oAreaUserPositions = emptyList(),
            xAreaUserPositions = listOf(UserPosition(userId = 1, x = 0.8, y = 0.4))
        )

        val notes = engine.buildWrongAnswerNotes()

        assertEquals(1, notes.size)
        assertEquals("하늘은 파란색이다", notes.first().question)
        assertEquals(OX.O, notes.first().answer)
    }

    private class FakeOXQuizRepository : OXQuizRepository {
        override suspend fun getQuizzes(): List<Quiz> = listOf(
            Quiz(
                id = 1,
                question = "하늘은 파란색이다",
                answer = "O",
                description = "맑은 날 하늘은 파랗게 보입니다."
            ),
            Quiz(
                id = 2,
                question = "펭귄은 하늘을 난다",
                answer = "X",
                description = "펭귄은 헤엄을 잘 칩니다."
            )
        )
    }
}
