package com.ssafy.jjongle.oxgame.domain.usecase

import com.ssafy.jjongle.oxgame.entity.QuizResult
import kotlinx.collections.immutable.toPersistentList
import org.junit.Assert.assertEquals
import org.junit.Test

/**
 * CalculateOXRankings의 아키텍처 또는 동작 계약을 검증하는 테스트입니다.
 *
 * - 계층: test
 * - 책임: 회귀를 막는 대표 시나리오와 모듈 경계 조건을 실행합니다.
 */
class CalculateOXRankingsUseCaseTest {
    private val useCase = CalculateOXRankingsUseCase()

    @Test
    fun invoke_countsCorrectUserIdsAndReturnsTop3() {
        val results = listOf(
            quizResult(correctUserIds = listOf(1, 2, 3)),
            quizResult(correctUserIds = listOf(1, 2)),
            quizResult(correctUserIds = listOf(1, 4))
        )

        val rankings = useCase(results)

        assertEquals(listOf(1 to 3, 2 to 2, 3 to 1), rankings)
    }

    @Test
    fun invoke_returnsEmptyListForNonPositiveLimit() {
        val rankings = useCase(listOf(quizResult(correctUserIds = listOf(1))), limit = 0)

        assertEquals(emptyList<Pair<Int, Int>>(), rankings)
    }

    private fun quizResult(correctUserIds: List<Int>) = QuizResult(
        quizId = 1,
        correctAnswer = "O",
        correctCount = correctUserIds.size,
        totalParticipants = 4,
        correctUserIds = correctUserIds.toPersistentList()
    )
}
