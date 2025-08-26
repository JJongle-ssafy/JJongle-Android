package com.ssafy.jjongle.data.repository

import android.os.Build
import androidx.annotation.RequiresApi
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
import java.time.format.DateTimeParseException
import javax.inject.Inject

class OXGameHistoryRepositoryImpl @Inject constructor(
    private val remote: OXGameRemoteDataSource
) : OXGameHistoryRepository {

    @RequiresApi(Build.VERSION_CODES.O)
    override suspend fun getHistories(page: Int): OXGameHistoryPage {
        val dto = remote.getHistories(page)
        return OXGameHistoryPage(
            totalPages = dto.totalPages,
            content = dto.content.map { it.toDomain() }
        )
    }

    override suspend fun getHistoryDetail(historyId: Long): List<OXGameWrongAnswerNote> {
        return remote.getHistoryDetail(historyId).map { it.toDomain() }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun OXGameHistoryDto.toDomain(): OXGameHistory {
        val ldt = parsePlayedAt(playedAt)
        return OXGameHistory(id = quizHistoryId, playedAt = ldt)
    }

    private fun OXGameWrongAnswerNoteDto.toDomain(): OXGameWrongAnswerNote {
        val ox = if (answer.equals("O", ignoreCase = true)) OX.O else OX.X
        return OXGameWrongAnswerNote(question = question, answer = ox)
    }

    // "2024-01-24T09:55:00" (local) 또는 "2025-08-12T05:27:43.025Z"(UTC)을 모두 처리
    @RequiresApi(Build.VERSION_CODES.O)
    private fun parsePlayedAt(raw: String): LocalDateTime {
        return try {
            OffsetDateTime.parse(raw).atZoneSameInstant(ZoneId.systemDefault()).toLocalDateTime()
        } catch (_: DateTimeParseException) {
            LocalDateTime.parse(raw, DateTimeFormatter.ISO_LOCAL_DATE_TIME)
        }
    }
}
