package com.ssafy.jjongle.data.model

import com.ssafy.jjongle.domain.entity.GameProfileImage
import com.ssafy.jjongle.domain.entity.Quiz
import com.ssafy.jjongle.domain.entity.QuizSession
import com.ssafy.jjongle.domain.entity.UserPosition

/**
 * Data 레이어의 DTO를 Domain 레이어의 Entity로 변환하는 확장 함수들을 정의합니다.
 */

fun UserPositionDto.toDomain(): UserPosition {
    return UserPosition(
        userId = this.userId.orMissingServerId(),
        x = this.x.orMissingServerCoordinate(),
        y = this.y.orMissingServerCoordinate()
    )
}

fun UserPosition.toDto(): UserPositionDto {
    return UserPositionDto(
        userId = this.userId,
        x = this.x,
        y = this.y
    )
}

fun GameFinishProfile.toDomain(): GameProfileImage {
    return GameProfileImage(
        userId = this.userId.orMissingServerId(),
        base64 = this.base64.orMissingServerField("gameFinish.base64")
    )
}

fun GameStartResponse.toDomain(): QuizSession {
    return this.data.toDomain()
}

fun GameFinishResponse.toDomainProfiles(): List<GameProfileImage> {
    return this.data?.userImages
        .orEmpty()
        .mapNotNull { it?.toDomain() }
}

fun GameStartData?.toDomain(): QuizSession {
    return QuizSession(
        quizzes = this?.quizList.orEmpty().mapNotNull { it?.toDomain() },
        sessionKey = this?.sessionKey.orMissingServerField("gameStart.sessionKey")
    )
}

fun QuizResponse.toDomain(): Quiz {
    return Quiz(
        id = this.quizId.orMissingServerId(),
        question = this.question.orMissingServerField("quiz.question"),
        answer = this.answer.orMissingServerField("quiz.answer"),
        description = this.description.orMissingServerField("quiz.description")
    )
}
