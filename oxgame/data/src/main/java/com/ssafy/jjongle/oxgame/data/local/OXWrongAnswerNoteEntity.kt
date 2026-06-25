package com.ssafy.jjongle.oxgame.data.local

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import com.ssafy.jjongle.oxgame.entity.OX
import com.ssafy.jjongle.oxgame.entity.OXGameWrongAnswerNote

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

/**
 * OXWrong Answer Note 값을 로컬 DB에 저장하기 위한 Room Entity입니다.
 *
 * DB 컬럼 구조와 앱 내부 모델 사이의 차이를 data 계층 안에서 관리할 수 있게 합니다.
 */
data class OXWrongAnswerNoteEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0L,
    val historyId: Long,
    val question: String,
    val answer: String
)

fun OXWrongAnswerNoteEntity.toVO(): OXGameWrongAnswerNote {
    return OXGameWrongAnswerNote(
        question = question.takeIf { it.isNotBlank() } ?: MISSING_QUESTION,
        answer = answer.toOxOrFallback()
    )
}

fun OXGameWrongAnswerNote.toEntity(historyId: Long): OXWrongAnswerNoteEntity {
    return OXWrongAnswerNoteEntity(
        historyId = historyId,
        question = question,
        answer = answer.name
    )
}

private fun String.toOxOrFallback(): OX {
    return OX.entries.firstOrNull { it.name.equals(this, ignoreCase = true) } ?: OX.X
}

private const val MISSING_QUESTION = "[MISSING_LOCAL_FIELD:ox.question]"
