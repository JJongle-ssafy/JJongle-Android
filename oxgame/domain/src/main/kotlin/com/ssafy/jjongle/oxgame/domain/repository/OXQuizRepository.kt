package com.ssafy.jjongle.oxgame.domain.repository

import com.ssafy.jjongle.oxgame.entity.Quiz

/**
 * OXQuizRepository domain 계층이 의존하는 저장소 계약입니다.
 *
 * - 계층: oxgame/domain
 * - 책임: data 구현을 숨기고 유스케이스에 필요한 작업만 노출합니다.
 */
interface OXQuizRepository {
    suspend fun getQuizzes(): List<Quiz>
}
