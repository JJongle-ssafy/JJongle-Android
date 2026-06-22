package com.ssafy.jjongle.data.game

import com.ssafy.jjongle.domain.entity.GameStartEvent
import com.ssafy.jjongle.domain.entity.OX
import com.ssafy.jjongle.domain.entity.OXGameWrongAnswerNote
import com.ssafy.jjongle.domain.entity.Quiz
import com.ssafy.jjongle.domain.entity.QuizSession
import com.ssafy.jjongle.domain.entity.SubmitResultEvent
import com.ssafy.jjongle.domain.entity.UserPosition
import com.ssafy.jjongle.domain.repository.OXQuizRepository
import java.util.UUID
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class LocalOXGameEngine @Inject constructor(
    private val quizRepository: OXQuizRepository
) {
    private var currentSession: QuizSession? = null
    private val submissions = linkedMapOf<Int, OXSubmission>()

    suspend fun startGame(): GameStartEvent {
        val quizzes = quizRepository.getQuizzes()
        require(quizzes.isNotEmpty()) { "OX 퀴즈가 비어 있습니다." }

        val session = QuizSession(
            sessionKey = "local-${UUID.randomUUID()}",
            quizzes = quizzes
        )
        currentSession = session
        submissions.clear()
        return GameStartEvent(
            quizzes = session.quizzes,
            sessionKey = session.sessionKey
        )
    }

    fun submitAnswer(
        sessionKey: String,
        quizId: Int,
        oAreaUserPositions: List<UserPosition>,
        xAreaUserPositions: List<UserPosition>
    ): SubmitResultEvent {
        val session = requireNotNull(currentSession) { "진행 중인 OX 게임 세션이 없습니다." }
        require(session.sessionKey == sessionKey) { "OX 게임 세션이 일치하지 않습니다." }

        val quiz = requireNotNull(session.quizzes.firstOrNull { it.id == quizId }) {
            "OX 퀴즈를 찾을 수 없습니다. quizId=$quizId"
        }
        val normalizedAnswer = quiz.answer.uppercase()
        val correctUserPositions = if (normalizedAnswer == OX.O.name) {
            oAreaUserPositions
        } else {
            xAreaUserPositions
        }.distinctBy { it.userId }
        val totalParticipantCount = (oAreaUserPositions + xAreaUserPositions)
            .distinctBy { it.userId }
            .size

        submissions[quiz.id] = OXSubmission(
            quiz = quiz,
            correctUserPositions = correctUserPositions,
            totalParticipantCount = totalParticipantCount
        )

        return SubmitResultEvent(
            quizId = quiz.id,
            correctAnswer = normalizedAnswer,
            correctUserPositions = correctUserPositions
        )
    }

    fun buildWrongAnswerNotes(): List<OXGameWrongAnswerNote> {
        return submissions.values
            .filter { it.totalParticipantCount > it.correctUserPositions.size }
            .map { submission ->
                OXGameWrongAnswerNote(
                    question = submission.quiz.question,
                    answer = submission.quiz.answer.toOX()
                )
            }
    }

    fun clear() {
        currentSession = null
        submissions.clear()
    }

    private fun String.toOX(): OX {
        return if (equals(OX.O.name, ignoreCase = true)) OX.O else OX.X
    }

    private data class OXSubmission(
        val quiz: Quiz,
        val correctUserPositions: List<UserPosition>,
        val totalParticipantCount: Int
    )
}
