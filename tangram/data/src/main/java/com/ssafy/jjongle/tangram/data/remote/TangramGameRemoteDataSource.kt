package com.ssafy.jjongle.tangram.data.remote

import com.ssafy.jjongle.common.data.BaseRemoteDataSource
import com.ssafy.jjongle.tangram.data.remote.model.SingleGameDTO
import com.ssafy.jjongle.tangram.data.remote.model.TangramDetailDTO
import com.ssafy.jjongle.tangram.data.remote.model.TangramHistoriesPageDTO
import javax.inject.Inject

/**
 * TangramGameRemoteDataSource 데이터 원본 접근을 담당합니다.
 *
 * - 계층: tangram/data
 * - 책임: 저장소 구현이 사용할 원격 또는 로컬 데이터 작업을 캡슐화합니다.
 */
class TangramGameRemoteDataSource @Inject constructor(
    private val api: TangramGameApiService,
) : BaseRemoteDataSource() {

    suspend fun getSingleGame(): SingleGameDTO =
        checkResponse(api.getSingleGame())

    suspend fun getTangramHistories(page: Int, size: Int): TangramHistoriesPageDTO =
        checkResponse(api.getTangramHistories(page, size))

    suspend fun getTangramDetail(id: Long): TangramDetailDTO =
        checkResponse(api.getTangramDetail(id))
}
