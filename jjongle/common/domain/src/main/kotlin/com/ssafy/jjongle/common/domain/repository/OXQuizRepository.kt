package com.ssafy.jjongle.common.domain.repository

import com.ssafy.jjongle.common.entity.Quiz

interface OXQuizRepository {
    suspend fun getQuizzes(): List<Quiz>
}
