package com.ssafy.jjongle.data.repository

import com.ssafy.jjongle.data.model.missingServerDateTime
import com.ssafy.jjongle.data.model.orMissingServerField
import com.ssafy.jjongle.data.model.orMissingServerLongId
import com.ssafy.jjongle.data.remote.OXGameRemoteDataSource
import com.ssafy.jjongle.data.remote.model.oxgame.OXGameHistoryDto
import com.ssafy.jjongle.data.remote.model.oxgame.OXGameWrongAnswerNoteDto
import com.ssafy.jjongle.domain.entity.OX
import com.ssafy.jjongle.domain.entity.OXGameHistory
import com.ssafy.jjongle.domain.entity.OXGameWrongAnswerNote
import com.ssafy.jjongle.domain.repository.OXGameHistoryPage
import com.ssafy.jjongle.domain.repository.OXGameHistoryRepository
import java.time.LocalDateTime
import java.time.OffsetDateTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import javax.inject.Inject

class OXGameHistoryRepositoryImpl @Inject constructor(
    private val remote: OXGameRemoteDataSource
) : OXGameHistoryRepository {

    override suspend fun getHistories(page: Int): OXGameHistoryPage {
        val dto = remote.getHistories(page)
        return OXGameHistoryPage(
            totalPages = dto.totalPages ?: 0,
            content = dto.content.orEmpty().mapNotNull { it?.toDomain() }
        )
    }

    override suspend fun getHistoryDetail(historyId: Long): List<OXGameWrongAnswerNote> {
        return remote.getHistoryDetail(historyId).mapNotNull { it?.toDomain() }
    }

    private fun OXGameHistoryDto.toDomain(): OXGameHistory {
        val ldt = parsePlayedAt(playedAt)
        return OXGameHistory(id = quizHistoryId.orMissingServerLongId(), playedAt = ldt)
    }

    private fun OXGameWrongAnswerNoteDto.toDomain(): OXGameWrongAnswerNote {
        val ox = if (answer.equals("O", ignoreCase = true)) OX.O else OX.X
        return OXGameWrongAnswerNote(
            question = question.orMissingServerField("oxWrongAnswer.question"),
            answer = ox
        )
    }

    // "2024-01-24T09:55:00" (local) 또는 "2025-08-12T05:27:43.025Z"(UTC)을 모두 처리
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
}
