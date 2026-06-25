package com.ssafy.jjongle.oxgame.domain.usecase

import com.ssafy.jjongle.oxgame.entity.QuizResult
import kotlinx.collections.immutable.toPersistentList
import org.junit.Assert.assertEquals
import org.junit.Test

/**
 * Calculate OXRankings Use Case Test 회귀를 막기 위한 테스트입니다.
 *
 * 리팩터링이나 모듈 이동 중에도 기존 동작, 아키텍처 경계, 오류 처리 계약이 유지되는지 확인합니다.
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
