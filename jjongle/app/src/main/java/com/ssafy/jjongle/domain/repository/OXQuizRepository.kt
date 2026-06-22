package com.ssafy.jjongle.domain.repository

import com.ssafy.jjongle.domain.entity.Quiz

interface OXQuizRepository {
    suspend fun getQuizzes(): List<Quiz>
}
