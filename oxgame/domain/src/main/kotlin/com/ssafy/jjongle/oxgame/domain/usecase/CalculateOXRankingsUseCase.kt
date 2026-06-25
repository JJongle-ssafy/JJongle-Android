package com.ssafy.jjongle.oxgame.domain.usecase

import com.ssafy.jjongle.oxgame.entity.QuizResult
import javax.inject.Inject

/**
 * CalculateOXRankingsUseCase 비즈니스 시나리오를 실행하는 유스케이스입니다.
 *
 * - 계층: oxgame/domain
 * - 책임: ViewModel이 필요한 domain 작업을 단일 진입점으로 제공합니다.
 */
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
