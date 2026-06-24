package com.ssafy.jjongle.oxgame.domain.repository

import com.ssafy.jjongle.oxgame.entity.Quiz

interface OXQuizRepository {
    suspend fun getQuizzes(): List<Quiz>
}
