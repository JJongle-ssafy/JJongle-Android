package com.ssafy.jjongle.oxgame.entity

import kotlinx.collections.immutable.ImmutableList

data class OXScoreUpdate(
    val quizResults: ImmutableList<QuizResult>,
    val gameScore: GameScore
)
