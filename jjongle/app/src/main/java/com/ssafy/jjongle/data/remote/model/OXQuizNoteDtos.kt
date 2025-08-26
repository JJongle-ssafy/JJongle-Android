package com.ssafy.jjongle.data.remote.model.oxgame

data class OXGameHistoryDto(
    val quizHistoryId: Long,
    val playedAt: String // "2025-08-12T05:27:43.025Z"
)

data class OXGameHistoriesPageDto(
    val totalPages: Int,
    val content: List<OXGameHistoryDto>
)

data class OXGameWrongAnswerNoteDto(
    val question: String,
    val answer: String // "O" or "X"
)
