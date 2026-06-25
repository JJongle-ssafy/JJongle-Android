package com.ssafy.jjongle.oxgame.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.ssafy.jjongle.oxgame.entity.OXGameHistory
import java.time.Instant
import java.time.ZoneId

/**
 * OXGameHistoryEntity 로컬 저장소 레코드를 표현하는 entity입니다.
 *
 * - 계층: oxgame/data
 * - 책임: 영속화 구조를 data 계층 안에 격리하고 domain 모델과 분리합니다.
 */
@Entity(tableName = "ox_game_histories")
data class OXGameHistoryEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0L,
    val playedAtEpochMillis: Long,
    val totalQuizzes: Int,
    val completedQuizzes: Int,
    val totalCorrectAnswers: Int
)

fun OXGameHistoryEntity.toVO(): OXGameHistory {
    return OXGameHistory(
        id = id,
        playedAt = Instant.ofEpochMilli(playedAtEpochMillis)
            .atZone(ZoneId.systemDefault())
            .toLocalDateTime()
    )
}
