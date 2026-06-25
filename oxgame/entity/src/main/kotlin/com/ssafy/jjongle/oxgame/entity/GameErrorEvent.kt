package com.ssafy.jjongle.oxgame.entity

/**
 * Game Error Event는 OX 게임 진행 중 발생한 도메인 이벤트입니다.
 *
 * 이벤트 종류를 타입으로 나눠 ViewModel이나 엔진이 문자열 분기 없이 게임 흐름을 처리하게 합니다.
 */
data class GameErrorEvent(
    val message: String
) : GameEvent
