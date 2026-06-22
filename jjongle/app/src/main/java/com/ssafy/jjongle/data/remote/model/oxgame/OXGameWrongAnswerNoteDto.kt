package com.ssafy.jjongle.data.remote.model.oxgame

import com.ssafy.jjongle.data.mapping.orMissingServerField
import com.ssafy.jjongle.domain.entity.OX
import com.ssafy.jjongle.domain.entity.OXGameWrongAnswerNote

data class OXGameWrongAnswerNoteDto(
    val question: String? = null,
    val answer: String? = null // "O" or "X"
)

fun OXGameWrongAnswerNoteDto.toDomain(): OXGameWrongAnswerNote {
    val ox = if (answer.equals("O", ignoreCase = true)) OX.O else OX.X
    return OXGameWrongAnswerNote(
        question = question.orMissingServerField("oxWrongAnswer.question"),
        answer = ox
    )
}
