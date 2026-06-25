package com.ssafy.jjongle.tangram.entity

import com.ssafy.jjongle.common.entity.AnimalType

/**
 * Tangram History는 탱그램 흐름에서 계층 사이로 전달되는 도메인 값입니다.
 *
 * 원시 값 여러 개를 그대로 넘기지 않고 이름 있는 타입으로 묶어 호출 의도를 명확히 합니다.
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
