package com.ssafy.jjongle.oxgame.data.game

import com.ssafy.jjongle.oxgame.entity.GameStartEvent
import com.ssafy.jjongle.oxgame.entity.GameScore
import com.ssafy.jjongle.oxgame.entity.OX
import com.ssafy.jjongle.oxgame.entity.OXGameWrongAnswerNote
import com.ssafy.jjongle.oxgame.entity.QuizResult
import com.ssafy.jjongle.oxgame.entity.Quiz
import com.ssafy.jjongle.oxgame.entity.QuizSession
import com.ssafy.jjongle.oxgame.entity.SubmitResultEvent
import com.ssafy.jjongle.oxgame.entity.UserPosition
import com.ssafy.jjongle.oxgame.domain.repository.OXQuizRepository
import java.util.UUID
import javax.inject.Inject
import javax.inject.Singleton
import kotlinx.collections.immutable.toPersistentList

/**
 * LocalOXGameEngine 관련 도메인 작업을 보조하는 컴포넌트입니다.
 *
 * - 계층: oxgame/data
 * - 책임: 반복되는 판단, 변환, 계산 로직을 별도 책임으로 분리합니다.
 */
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
            quizzes = quizzes.toPersistentList()
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
            correctUserPositions = correctUserPositions.toPersistentList()
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

    fun buildGameScore(): GameScore {
        val session = currentSession
        val quizResults = submissions.values.map { submission ->
            QuizResult(
                quizId = submission.quiz.id,
                correctAnswer = submission.quiz.answer.uppercase(),
                correctCount = submission.correctUserPositions.size,
                totalParticipants = submission.totalParticipantCount,
                correctUserIds = submission.correctUserPositions.map { it.userId }.toPersistentList()
            )
        }
        return GameScore(
            totalQuizzes = session?.quizzes?.size ?: 0,
            completedQuizzes = quizResults.size,
            totalCorrectAnswers = quizResults.sumOf { it.correctCount },
            quizResults = quizResults.toPersistentList()
        )
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
