package com.ssafy.jjongle.tangram.entity

import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentListOf

/**
 * TangramHistoriesPage 앱 내부에서 공유하는 도메인 값을 표현합니다.
 *
 * - 계층: tangram/entity
 * - 책임: 불변 값과 도메인 의미를 계층 사이에 전달합니다.
 */
data class TangramHistoriesPage(
    val content: ImmutableList<TangramHistory> = persistentListOf(),
    val isEnd: Boolean = true,
) {
    companion object {
        val empty = TangramHistoriesPage()
    }
}
