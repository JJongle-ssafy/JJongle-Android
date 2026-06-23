package com.ssafy.jjongle.data.repository

import com.ssafy.jjongle.data.local.oxgame.OXGameHistoryDao
import com.ssafy.jjongle.data.local.oxgame.toDomain
import com.ssafy.jjongle.common.entity.OXGameWrongAnswerNote
import com.ssafy.jjongle.common.domain.repository.OXGameHistoryPage
import com.ssafy.jjongle.common.domain.repository.OXGameHistoryRepository
import javax.inject.Inject

class OXGameHistoryRepositoryImpl @Inject constructor(
    private val historyDao: OXGameHistoryDao
) : OXGameHistoryRepository {

    override suspend fun getHistories(page: Int): OXGameHistoryPage {
        val safePage = page.coerceAtLeast(0)
        val totalCount = historyDao.countHistories()
        val totalPages = if (totalCount == 0) {
            0
        } else {
            (totalCount + PAGE_SIZE - 1) / PAGE_SIZE
        }
        val histories = historyDao.getHistories(
            limit = PAGE_SIZE,
            offset = safePage * PAGE_SIZE
        )
        return OXGameHistoryPage(
            totalPages = totalPages,
            content = histories.map { it.history.toDomain() }
        )
    }

    override suspend fun getHistoryDetail(historyId: Long): List<OXGameWrongAnswerNote> {
        return historyDao.getHistory(historyId)
            ?.notes
            .orEmpty()
            .map { it.toDomain() }
    }

    private companion object {
        const val PAGE_SIZE = 3
    }
}
