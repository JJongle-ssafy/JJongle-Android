package com.ssafy.jjongle.oxgame.data.repository

import com.ssafy.jjongle.oxgame.entity.Quiz
import com.ssafy.jjongle.oxgame.domain.repository.OXQuizRepository
import javax.inject.Inject
import javax.inject.Singleton

/**
 * LocalOXQuizRepositoryImpl 저장소 계약의 data 계층 구현입니다.
 *
 * - 계층: oxgame/data
 * - 책임: 데이터 원본을 조합하고 domain 계층이 기대하는 모델로 반환합니다.
 */
@Singleton
class LocalOXQuizRepositoryImpl @Inject constructor() : OXQuizRepository {
    override suspend fun getQuizzes(): List<Quiz> = DEFAULT_QUIZZES

    private companion object {
        val DEFAULT_QUIZZES = listOf(
            Quiz(
                id = 1,
                question = "바다거북은 알을 낳기 위해 모래사장으로 올라와요.",
                answer = "O",
                description = "바다거북은 바다에 살지만 알은 따뜻한 모래 속에 낳아요."
            ),
            Quiz(
                id = 2,
                question = "펭귄은 하늘을 날 수 있어요.",
                answer = "X",
                description = "펭귄은 날개가 있지만 하늘을 날지 못하고, 물속에서 헤엄을 잘 쳐요."
            ),
            Quiz(
                id = 3,
                question = "식물은 자라기 위해 햇빛과 물이 필요해요.",
                answer = "O",
                description = "식물은 햇빛과 물을 이용해 스스로 양분을 만들며 자라요."
            ),
            Quiz(
                id = 4,
                question = "달은 스스로 빛을 내는 별이에요.",
                answer = "X",
                description = "달은 스스로 빛을 내지 않고 태양빛을 반사해서 밝게 보여요."
            ),
            Quiz(
                id = 5,
                question = "손을 씻으면 눈에 보이지 않는 세균을 줄일 수 있어요.",
                answer = "O",
                description = "비누로 손을 깨끗이 씻으면 세균이 줄어들어 건강을 지킬 수 있어요."
            )
        )
    }
}
