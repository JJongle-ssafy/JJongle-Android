package com.ssafy.jjongle.oxgame.domain.repository

import com.ssafy.jjongle.oxgame.entity.OXGameHistory
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentListOf

// totalPages 등은 사용하지 않으므로 content만 보유

/**
 * OXGameHistoryPage Navigation3에서 사용하는 페이지 계약입니다.
 *
 * - 계층: oxgame/domain
 * - 책임: 기능 모듈의 화면 목적지를 타입 안전한 라우팅 값으로 표현합니다.
 */
data class OXGameHistoryPage(
    val totalPages: Int = 0,
    val content: ImmutableList<OXGameHistory> = persistentListOf(),
) {
    companion object {
        val empty = OXGameHistoryPage()
    }
}
