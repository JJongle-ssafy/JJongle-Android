package com.ssafy.jjongle.domain.entity

enum class OX { O, X }

data class OXGameWrongAnswerNote(
    val question: String,
    val answer: OX      // 정답이 O/X
)