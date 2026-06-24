package com.ssafy.jjongle.tangram.data.remote

import com.ssafy.jjongle.common.data.BaseRemoteDataSource
import com.ssafy.jjongle.tangram.data.remote.model.SingleGameDTO
import com.ssafy.jjongle.tangram.data.remote.model.TangramDetailDTO
import com.ssafy.jjongle.tangram.data.remote.model.TangramHistoriesPageDTO
import javax.inject.Inject

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
