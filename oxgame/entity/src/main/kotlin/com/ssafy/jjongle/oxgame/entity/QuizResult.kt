package com.ssafy.jjongle.oxgame.entity

import kotlinx.collections.immutable.ImmutableList

/**
 * Quiz Result는 OX 게임 처리 결과를 다음 계층으로 전달하는 값입니다.
 *
 * 성공/실패나 점수 계산 결과를 명시적인 타입으로 남겨 호출부의 의미를 분명하게 합니다.
 */
data class QuizResult(
    val quizId: Int,
    val correctAnswer: String,
    val correctCount: Int,
    val totalParticipants: Int,
    val correctUserIds: ImmutableList<Int>
)
