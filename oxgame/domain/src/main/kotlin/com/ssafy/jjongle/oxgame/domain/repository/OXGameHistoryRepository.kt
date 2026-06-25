package com.ssafy.jjongle.oxgame.domain.repository

import com.ssafy.jjongle.oxgame.entity.OXGameWrongAnswerNote
import kotlinx.collections.immutable.ImmutableList


/**
 * OXGame History 기능이 domain 계층에서 기대하는 저장소 계약입니다.
 *
 * UseCase는 이 계약에만 의존하고, Firebase, Room, Retrofit 같은 실제 데이터 구현은 data 계층에 숨깁니다.
 */
interface OXGameHistoryRepository {
    suspend fun getHistories(page: Int): OXGameHistoryPage
    suspend fun getHistoryDetail(historyId: Long): ImmutableList<OXGameWrongAnswerNote>
}
