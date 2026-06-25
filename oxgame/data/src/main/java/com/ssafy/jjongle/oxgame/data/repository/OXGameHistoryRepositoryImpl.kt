package com.ssafy.jjongle.oxgame.data.repository

import com.ssafy.jjongle.oxgame.data.local.OXGameHistoryDao
import com.ssafy.jjongle.oxgame.data.local.toVO
import com.ssafy.jjongle.oxgame.entity.OXGameWrongAnswerNote
import com.ssafy.jjongle.oxgame.domain.repository.OXGameHistoryPage
import com.ssafy.jjongle.oxgame.domain.repository.OXGameHistoryRepository
import javax.inject.Inject
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.toPersistentList

/**
 * OXGameHistoryRepositoryImpl 저장소 계약의 data 계층 구현입니다.
 *
 * - 계층: oxgame/data
 * - 책임: 데이터 원본을 조합하고 domain 계층이 기대하는 모델로 반환합니다.
 */
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
            content = histories.map { it.history.toVO() }.toPersistentList()
        )
    }

    override suspend fun getHistoryDetail(historyId: Long): ImmutableList<OXGameWrongAnswerNote> {
        return historyDao.getHistory(historyId)
            ?.notes
            .orEmpty()
            .map { it.toVO() }
            .toPersistentList()
    }

    private companion object {
        const val PAGE_SIZE = 3
    }
}
