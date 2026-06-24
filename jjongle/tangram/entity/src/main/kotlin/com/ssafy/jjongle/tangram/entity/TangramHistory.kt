package com.ssafy.jjongle.tangram.entity

import com.ssafy.jjongle.common.entity.AnimalType

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
