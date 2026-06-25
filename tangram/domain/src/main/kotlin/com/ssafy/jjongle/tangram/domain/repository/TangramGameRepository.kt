package com.ssafy.jjongle.tangram.domain.repository

import com.ssafy.jjongle.common.entity.AnimalType
import com.ssafy.jjongle.tangram.entity.TangramDetail
import com.ssafy.jjongle.tangram.entity.TangramHistoriesPage

/**
 * Tangram Game 기능이 domain 계층에서 기대하는 저장소 계약입니다.
 *
 * UseCase는 이 계약에만 의존하고, Firebase, Room, Retrofit 같은 실제 데이터 구현은 data 계층에 숨깁니다.
 */
interface TangramGameRepository {
    suspend fun getCurrentChallengeStageId(): Int
    suspend fun getTangramHistories(page: Int, size: Int): TangramHistoriesPage
    suspend fun getTangramDetail(tangramId: Long, type: AnimalType): TangramDetail

}
