package com.ssafy.jjongle.tangram.entity

import com.ssafy.jjongle.common.entity.AnimalType

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
