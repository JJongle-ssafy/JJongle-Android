package com.ssafy.jjongle.data.model

import com.google.gson.annotations.SerializedName
import com.ssafy.jjongle.domain.entity.Quiz

data class QuizResponse(
    @SerializedName("quizId")
    val quizId: Int? = null,

    @SerializedName("question")
    val question: String? = null,

    @SerializedName("answer")
    val answer: String? = null,

    @SerializedName("description")
    val description: String? = null
)

fun QuizResponse.toDomain(): Quiz {
    return Quiz(
        id = quizId.orMissingServerId(),
        question = question.orMissingServerField("quiz.question"),
        answer = answer.orMissingServerField("quiz.answer"),
        description = description.orMissingServerField("quiz.description")
    )
}
