package com.ssafy.jjongle.data.local.oxgame

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import com.ssafy.jjongle.common.entity.OX
import com.ssafy.jjongle.common.entity.OXGameWrongAnswerNote

@Entity(
    tableName = "ox_wrong_answer_notes",
    foreignKeys = [
        ForeignKey(
            entity = OXGameHistoryEntity::class,
            parentColumns = ["id"],
            childColumns = ["historyId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index("historyId")]
)
data class OXWrongAnswerNoteEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0L,
    val historyId: Long,
    val question: String,
    val answer: String
)

fun OXWrongAnswerNoteEntity.toDomain(): OXGameWrongAnswerNote {
    return OXGameWrongAnswerNote(
        question = question,
        answer = if (answer.equals(OX.O.name, ignoreCase = true)) OX.O else OX.X
    )
}

fun OXGameWrongAnswerNote.toEntity(historyId: Long): OXWrongAnswerNoteEntity {
    return OXWrongAnswerNoteEntity(
        historyId = historyId,
        question = question,
        answer = answer.name
    )
}
