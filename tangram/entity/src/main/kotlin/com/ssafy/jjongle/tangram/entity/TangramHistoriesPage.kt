package com.ssafy.jjongle.tangram.entity

import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentListOf

/**
 * Tangram Histories 화면을 타입 안전하게 표현하는 라우팅 값입니다.
 *
 * 문자열 경로와 인자를 객체 안에 모아 Navigation host와 기능 모듈이 같은 이동 계약을 공유하게 합니다.
 */
data class TangramHistoriesPage(
    val content: ImmutableList<TangramHistory> = persistentListOf(),
    val isEnd: Boolean = true,
) {
    companion object {
        val empty = TangramHistoriesPage()
    }
}
