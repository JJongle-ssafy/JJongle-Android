package com.ssafy.jjongle.tangram.entity

import com.ssafy.jjongle.common.entity.AnimalType

/**
 * Tangram Detail는 탱그램 흐름에서 계층 사이로 전달되는 도메인 값입니다.
 *
 * 원시 값 여러 개를 그대로 넘기지 않고 이름 있는 타입으로 묶어 호출 의도를 명확히 합니다.
 */
data class TangramDetail(
    val tangramId: Long = TangramHistory.MISSING_SERVER_LONG_ID,
    val animal: AnimalType = AnimalType.TURTLE,
    val story: String = MISSING_STORY,
) {
    companion object {
        const val MISSING_STORY = "[MISSING_SERVER_FIELD:tangram.story]"
        val empty = TangramDetail()
    }
}
