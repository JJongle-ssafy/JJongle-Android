package com.ssafy.jjongle.data.model

import com.ssafy.jjongle.domain.entity.Quiz
import com.ssafy.jjongle.domain.entity.QuizSession
import com.ssafy.jjongle.domain.entity.UserPosition

/**
 * Data 레이어의 DTO를 Domain 레이어의 Entity로 변환하는 확장 함수들을 정의합니다.
 */

fun UserPositionDto.toDomain(): UserPosition {
    return UserPosition(
        userId = this.userId,
        x = this.x,
        y = this.y
    )
}

fun GameStartResponse.toDomain(): QuizSession {
    return QuizSession(
        quizzes = this.data.quizList.map { it.toDomain() },
        sessionKey = this.data.sessionKey
    )
}

fun GameStartData.toDomain(): QuizSession {
    return QuizSession(
        quizzes = this.quizList.map { it.toDomain() },
        sessionKey = this.sessionKey
    )
}

fun QuizResponse.toDomain(): Quiz {
    return Quiz(
        id = this.quizId,
        question = this.question,
        answer = this.answer,
        description = this.description
    )
}
