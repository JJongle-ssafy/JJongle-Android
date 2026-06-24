package com.ssafy.jjongle.oxgame.domain.repository

import com.ssafy.jjongle.oxgame.entity.OXGameWrongAnswerNote
import kotlinx.collections.immutable.ImmutableList


interface OXGameHistoryRepository {
    suspend fun getHistories(page: Int): OXGameHistoryPage
    suspend fun getHistoryDetail(historyId: Long): ImmutableList<OXGameWrongAnswerNote>
}
