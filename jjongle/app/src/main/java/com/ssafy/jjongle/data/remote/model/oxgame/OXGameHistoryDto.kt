package com.ssafy.jjongle.data.remote.model.oxgame

import com.ssafy.jjongle.data.model.missingServerDateTime
import com.ssafy.jjongle.data.model.orMissingServerLongId
import com.ssafy.jjongle.domain.entity.OXGameHistory
import java.time.LocalDateTime
import java.time.OffsetDateTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter

data class OXGameHistoryDto(
    val quizHistoryId: Long? = null,
    val playedAt: String? = null // "2025-08-12T05:27:43.025Z"
)

fun OXGameHistoryDto.toDomain(): OXGameHistory {
    return OXGameHistory(
        id = quizHistoryId.orMissingServerLongId(),
        playedAt = parsePlayedAt(playedAt)
    )
}

private fun parsePlayedAt(raw: String?): LocalDateTime {
    if (raw.isNullOrBlank()) return missingServerDateTime()

    return runCatching {
        OffsetDateTime.parse(raw).atZoneSameInstant(ZoneId.systemDefault()).toLocalDateTime()
    }.recoverCatching {
        LocalDateTime.parse(raw, DateTimeFormatter.ISO_LOCAL_DATE_TIME)
    }.getOrElse {
        missingServerDateTime()
    }
}
