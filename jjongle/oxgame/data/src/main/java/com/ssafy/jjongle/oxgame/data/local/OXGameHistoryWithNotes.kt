package com.ssafy.jjongle.oxgame.data.local

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
