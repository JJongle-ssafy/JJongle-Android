package com.ssafy.jjongle.data.local.oxgame

import androidx.room.Embedded
import androidx.room.Relation

data class OXGameHistoryWithNotes(
    @Embedded
    val history: OXGameHistoryEntity,
    @Relation(
        parentColumn = "id",
        entityColumn = "historyId"
    )
    val notes: List<OXWrongAnswerNoteEntity>
)
