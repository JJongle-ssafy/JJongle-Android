package com.ssafy.jjongle.data.remote

import javax.inject.Inject

@Deprecated("Legacy backend OX game data source retained during serverless renewal. Use Room-backed local OX history.")
class OXGameRemoteDataSource @Inject constructor(
    private val api: OXGameApiService
) {
    suspend fun getHistories(page: Int) = api.getHistories(page)
    suspend fun getHistoryDetail(historyId: Long) = api.getHistoryDetail(historyId)
}
