package com.ssafy.jjongle.data.local.oxgame

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.ssafy.jjongle.domain.entity.OXGameHistory
import java.time.Instant
import java.time.ZoneId

@Entity(tableName = "ox_game_histories")
data class OXGameHistoryEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0L,
    val playedAtEpochMillis: Long,
    val totalQuizzes: Int,
    val completedQuizzes: Int,
    val totalCorrectAnswers: Int
)

fun OXGameHistoryEntity.toDomain(): OXGameHistory {
    return OXGameHistory(
        id = id,
        playedAt = Instant.ofEpochMilli(playedAtEpochMillis)
            .atZone(ZoneId.systemDefault())
            .toLocalDateTime()
    )
}
