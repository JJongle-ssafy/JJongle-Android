package com.ssafy.jjongle.oxgame.data.local

import androidx.room.Embedded
import androidx.room.Relation

/**
 * OXGameHistoryWithNotes 모듈 기능을 표현하는 class 선언입니다.
 *
 * - 계층: oxgame/data
 * - 책임: 소속 계층의 역할을 타입으로 분리해 호출 경계를 명확히 합니다.
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
