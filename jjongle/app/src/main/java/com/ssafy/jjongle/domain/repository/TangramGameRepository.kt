package com.ssafy.jjongle.domain.repository

import com.ssafy.jjongle.domain.entity.AnimalType
import com.ssafy.jjongle.domain.entity.TangramDetail
import com.ssafy.jjongle.domain.entity.TangramHistory

interface TangramGameRepository {
    suspend fun getCurrentChallengeStageId(): Int
    suspend fun getTangramHistories(page: Int, size: Int): List<TangramHistory>
    suspend fun getTangramDetail(tangramId: Long, type: AnimalType): TangramDetail

}