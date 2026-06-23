package com.ssafy.jjongle.data.repository

import com.ssafy.jjongle.data.remote.TangramGameApiService
import com.ssafy.jjongle.data.remote.model.toDomain
import com.ssafy.jjongle.common.entity.AnimalType
import com.ssafy.jjongle.common.entity.TangramDetail
import com.ssafy.jjongle.common.entity.TangramHistory
import com.ssafy.jjongle.common.domain.repository.TangramGameRepository
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class TangramGameRepositoryImpl @Inject constructor(
    private val tangramGameApiService: TangramGameApiService
) : TangramGameRepository {
    
    override suspend fun getCurrentChallengeStageId(): Int {
        return try {
            val response = tangramGameApiService.getSingleGame()
            if (response.isSuccessful) {
                response.body()?.stage ?: 1
            } else {
                1 // API 실패 시 기본값
            }
        } catch (e: Exception) {
            1 // 예외 발생 시 기본값
        }
    }

    // API를 통해 칠교 히스토리를 가져오는 메서드
    override suspend fun getTangramHistories(page: Int, size: Int): List<TangramHistory> =
        tangramGameApiService.getTangramHistories(page, size).content
            .orEmpty()
            .mapNotNull { it?.toDomain() }

    // API를 통해 칠교 상세 정보를 가져오는 메서드
    override suspend fun getTangramDetail(tangramId: Long, type: AnimalType): TangramDetail {
        val res = tangramGameApiService.getTangramDetail(tangramId)
        return res.toDomain(tangramId, type)
    }
}
