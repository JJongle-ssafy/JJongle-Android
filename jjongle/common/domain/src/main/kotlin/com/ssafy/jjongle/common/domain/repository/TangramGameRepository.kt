package com.ssafy.jjongle.common.domain.repository

import com.ssafy.jjongle.common.entity.AnimalType
import com.ssafy.jjongle.common.entity.TangramDetail
import com.ssafy.jjongle.common.entity.TangramHistory

interface TangramGameRepository {
    suspend fun getCurrentChallengeStageId(): Int
    suspend fun getTangramHistories(page: Int, size: Int): List<TangramHistory>
    suspend fun getTangramDetail(tangramId: Long, type: AnimalType): TangramDetail

}