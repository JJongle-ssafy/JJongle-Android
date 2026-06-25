package com.ssafy.jjongle.common.domain.error.auth

/**
 * Unknown Auth Error은 인증이나 네트워크 실패 원인을 구분하는 domain 오류 타입입니다.
 *
 * 문자열 메시지에 의존하지 않고 오류 종류별 복구 흐름과 사용자 메시지를 분리할 수 있게 합니다.
 */
data class UnknownAuthError(
    val detail: String?
) : AuthError
