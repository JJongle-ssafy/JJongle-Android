package com.ssafy.jjongle.data.repository

import com.ssafy.jjongle.data.remote.OXGameRemoteDataSource
import com.ssafy.jjongle.data.remote.model.oxgame.toDomain
import com.ssafy.jjongle.domain.entity.OXGameWrongAnswerNote
import com.ssafy.jjongle.domain.repository.OXGameHistoryPage
import com.ssafy.jjongle.domain.repository.OXGameHistoryRepository
import javax.inject.Inject

class OXGameHistoryRepositoryImpl @Inject constructor(
    private val remote: OXGameRemoteDataSource
) : OXGameHistoryRepository {

    override suspend fun getHistories(page: Int): OXGameHistoryPage {
        return remote.getHistories(page).toDomain()
    }

    override suspend fun getHistoryDetail(historyId: Long): List<OXGameWrongAnswerNote> {
        return remote.getHistoryDetail(historyId).mapNotNull { it?.toDomain() }
    }
}
