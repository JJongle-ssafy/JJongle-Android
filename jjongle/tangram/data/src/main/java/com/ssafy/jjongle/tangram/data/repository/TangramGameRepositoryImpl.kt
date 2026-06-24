package com.ssafy.jjongle.tangram.data.repository

import com.ssafy.jjongle.common.entity.AnimalType
import com.ssafy.jjongle.tangram.entity.TangramDetail
import com.ssafy.jjongle.tangram.entity.TangramHistoriesPage
import com.ssafy.jjongle.tangram.domain.repository.TangramGameRepository
import com.ssafy.jjongle.tangram.data.remote.TangramGameRemoteDataSource
import com.ssafy.jjongle.tangram.data.remote.model.toVO
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class TangramGameRepositoryImpl @Inject constructor(
    private val tangramGameRemoteDataSource: TangramGameRemoteDataSource,
) : TangramGameRepository {
    
    override suspend fun getCurrentChallengeStageId(): Int =
        tangramGameRemoteDataSource.getSingleGame().toVO()

    override suspend fun getTangramHistories(page: Int, size: Int): TangramHistoriesPage =
        tangramGameRemoteDataSource.getTangramHistories(page, size).toVO()

    // API를 통해 칠교 상세 정보를 가져오는 메서드
    override suspend fun getTangramDetail(tangramId: Long, type: AnimalType): TangramDetail {
        val res = tangramGameRemoteDataSource.getTangramDetail(tangramId)
        return res.toVO(tangramId, type)
    }
}
