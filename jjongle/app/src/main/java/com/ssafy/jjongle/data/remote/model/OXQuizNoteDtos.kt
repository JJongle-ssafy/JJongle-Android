package com.ssafy.jjongle.data.remote.model.oxgame

data class OXGameHistoryDto(
    val quizHistoryId: Long? = null,
    val playedAt: String? = null // "2025-08-12T05:27:43.025Z"
)

data class OXGameHistoriesPageDto(
    val totalPages: Int? = null,
    val content: List<OXGameHistoryDto?>? = null
)

data class OXGameWrongAnswerNoteDto(
    val question: String? = null,
    val answer: String? = null // "O" or "X"
)
