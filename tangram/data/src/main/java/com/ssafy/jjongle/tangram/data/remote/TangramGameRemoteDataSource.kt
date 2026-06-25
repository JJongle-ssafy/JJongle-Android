package com.ssafy.jjongle.tangram.data.remote

import com.ssafy.jjongle.common.data.BaseRemoteDataSource
import com.ssafy.jjongle.tangram.data.remote.model.SingleGameDTO
import com.ssafy.jjongle.tangram.data.remote.model.TangramDetailDTO
import com.ssafy.jjongle.tangram.data.remote.model.TangramHistoriesPageDTO
import javax.inject.Inject

/**
 * Tangram Game 데이터를 외부 서비스나 로컬 저장소에서 읽고 쓰는 data 계층 경계입니다.
 *
 * Repository가 세부 API, SDK, 저장 방식에 직접 묶이지 않도록 데이터 접근 작업을 캡슐화합니다.
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
