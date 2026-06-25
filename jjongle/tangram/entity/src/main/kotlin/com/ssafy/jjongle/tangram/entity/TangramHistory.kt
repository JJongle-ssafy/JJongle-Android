package com.ssafy.jjongle.tangram.entity

import com.ssafy.jjongle.common.entity.AnimalType

/**
 * TangramHistory 앱 내부에서 공유하는 도메인 값을 표현합니다.
 *
 * - 계층: tangram/entity
 * - 책임: 불변 값과 도메인 의미를 계층 사이에 전달합니다.
 */
data class TangramHistory(
    val stage: Int = MISSING_SERVER_ID,
    val tangramId: Long = MISSING_SERVER_LONG_ID,
    val animal: AnimalType = AnimalType.TURTLE,
) {
    companion object {
        const val MISSING_SERVER_ID = -1
        const val MISSING_SERVER_LONG_ID = -1L
        val empty = TangramHistory()
    }
}
