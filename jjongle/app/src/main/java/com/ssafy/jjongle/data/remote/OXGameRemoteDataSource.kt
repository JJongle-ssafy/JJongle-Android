package com.ssafy.jjongle.data.remote

import javax.inject.Inject

class OXGameRemoteDataSource @Inject constructor(
    private val api: OXGameApiService
) {
    suspend fun getHistories(page: Int) = api.getHistories(page)
    suspend fun getHistoryDetail(historyId: Long) = api.getHistoryDetail(historyId)
}
