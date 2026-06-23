package com.ssafy.jjongle.common.domain.usecase

import com.ssafy.jjongle.common.entity.QuizResult
import org.junit.Assert.assertEquals
import org.junit.Test

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
        correctUserIds = correctUserIds
    )
}
