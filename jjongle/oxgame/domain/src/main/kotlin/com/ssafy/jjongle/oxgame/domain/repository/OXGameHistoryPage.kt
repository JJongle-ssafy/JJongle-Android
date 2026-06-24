package com.ssafy.jjongle.oxgame.domain.repository

import com.ssafy.jjongle.oxgame.entity.OXGameHistory
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentListOf

// totalPages 등은 사용하지 않으므로 content만 보유
data class OXGameHistoryPage(
    val totalPages: Int = 0,
    val content: ImmutableList<OXGameHistory> = persistentListOf(),
) {
    companion object {
        val empty = OXGameHistoryPage()
    }
}
