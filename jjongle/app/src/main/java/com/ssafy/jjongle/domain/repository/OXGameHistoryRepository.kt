package com.ssafy.jjongle.domain.repository

import com.ssafy.jjongle.domain.entity.OXGameWrongAnswerNote


interface OXGameHistoryRepository {
    suspend fun getHistories(page: Int): OXGameHistoryPage
    suspend fun getHistoryDetail(historyId: Long): List<OXGameWrongAnswerNote>
}