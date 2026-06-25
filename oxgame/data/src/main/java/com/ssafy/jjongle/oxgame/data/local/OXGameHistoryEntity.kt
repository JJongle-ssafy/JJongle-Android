package com.ssafy.jjongle.oxgame.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.ssafy.jjongle.oxgame.entity.OXGameHistory
import java.time.Instant
import java.time.ZoneId

/**
 * OXGame History 값을 로컬 DB에 저장하기 위한 Room Entity입니다.
 *
 * DB 컬럼 구조와 앱 내부 모델 사이의 차이를 data 계층 안에서 관리할 수 있게 합니다.
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
