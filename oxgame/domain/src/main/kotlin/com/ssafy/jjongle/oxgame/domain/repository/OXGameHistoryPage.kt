package com.ssafy.jjongle.oxgame.domain.repository

import com.ssafy.jjongle.oxgame.entity.OXGameHistory
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentListOf

// totalPages 등은 사용하지 않으므로 content만 보유

/**
 * OXGame History 화면을 타입 안전하게 표현하는 라우팅 값입니다.
 *
 * 문자열 경로와 인자를 객체 안에 모아 Navigation host와 기능 모듈이 같은 이동 계약을 공유하게 합니다.
 */
data class OXGameHistoryPage(
    val totalPages: Int = 0,
    val content: ImmutableList<OXGameHistory> = persistentListOf(),
) {
    companion object {
        val empty = OXGameHistoryPage()
    }
}
