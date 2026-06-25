package com.ssafy.jjongle.oxgame.domain.usecase

import com.ssafy.jjongle.oxgame.entity.QuizResult
import javax.inject.Inject

/**
 * Calculate OXRankings 시나리오를 실행하는 domain 계층 유스케이스입니다.
 *
 * ViewModel이 repository 세부 구현을 알지 않고 하나의 사용자 흐름이나 비즈니스 작업만 호출하도록 합니다.
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
