package com.ssafy.jjongle.tangram.domain.repository

import com.ssafy.jjongle.common.entity.AnimalType
import com.ssafy.jjongle.tangram.entity.TangramDetail
import com.ssafy.jjongle.tangram.entity.TangramHistoriesPage

interface TangramGameRepository {
    suspend fun getCurrentChallengeStageId(): Int
    suspend fun getTangramHistories(page: Int, size: Int): TangramHistoriesPage
    suspend fun getTangramDetail(tangramId: Long, type: AnimalType): TangramDetail

}
