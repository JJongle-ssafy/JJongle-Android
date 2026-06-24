package com.ssafy.jjongle.oxgame.domain.usecase

import com.ssafy.jjongle.oxgame.entity.QuizResult
import javax.inject.Inject

class CalculateOXRankingsUseCase @Inject constructor() {
    operator fun invoke(results: List<QuizResult>, limit: Int = 3): List<Pair<Int, Int>> {
        if (limit <= 0) return emptyList()

        return results
            .flatMap { it.correctUserIds }
            .groupingBy { it }
            .eachCount()
            .toList()
            .sortedByDescending { it.second }
            .take(limit)
    }
}
