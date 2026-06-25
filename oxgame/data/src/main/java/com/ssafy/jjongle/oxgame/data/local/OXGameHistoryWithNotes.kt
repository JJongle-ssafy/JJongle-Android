package com.ssafy.jjongle.oxgame.data.local

import androidx.room.Embedded
import androidx.room.Relation

/**
 * OXGame History With Notes는 OX 게임 흐름에서 계층 사이로 전달되는 도메인 값입니다.
 *
 * 원시 값 여러 개를 그대로 넘기지 않고 이름 있는 타입으로 묶어 호출 의도를 명확히 합니다.
 */
data class OXGameHistoryWithNotes(
    @Embedded
    val history: OXGameHistoryEntity,
    @Relation(
        parentColumn = "id",
        entityColumn = "historyId"
    )
    val notes: List<OXWrongAnswerNoteEntity>
)
