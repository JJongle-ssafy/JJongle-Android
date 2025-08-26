package com.ssafy.jjongle.domain.entity

data class TangramHistory(
    val stage: Int, val tangramId: Long, val animal: AnimalType
)

data class TangramDetail(
    val tangramId: Long, val animal: AnimalType, val story: String
)