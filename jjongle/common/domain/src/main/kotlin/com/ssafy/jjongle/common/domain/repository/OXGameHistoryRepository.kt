package com.ssafy.jjongle.common.domain.repository

import com.ssafy.jjongle.common.entity.OXGameWrongAnswerNote


interface OXGameHistoryRepository {
    suspend fun getHistories(page: Int): OXGameHistoryPage
    suspend fun getHistoryDetail(historyId: Long): List<OXGameWrongAnswerNote>
}