package com.ssafy.jjongle.tangram.domain.repository

import com.ssafy.jjongle.common.entity.AnimalType
import com.ssafy.jjongle.tangram.entity.TangramDetail
import com.ssafy.jjongle.tangram.entity.TangramHistoriesPage

/**
 * TangramGameRepository domain 계층이 의존하는 저장소 계약입니다.
 *
 * - 계층: tangram/domain
 * - 책임: data 구현을 숨기고 유스케이스에 필요한 작업만 노출합니다.
 */
interface TangramGameRepository {
    suspend fun getCurrentChallengeStageId(): Int
    suspend fun getTangramHistories(page: Int, size: Int): TangramHistoriesPage
    suspend fun getTangramDetail(tangramId: Long, type: AnimalType): TangramDetail

}
