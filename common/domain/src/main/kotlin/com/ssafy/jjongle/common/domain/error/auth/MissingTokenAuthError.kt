package com.ssafy.jjongle.common.domain.error.auth

/**
 * MissingTokenAuthError 모듈 기능을 표현하는 object 선언입니다.
 *
 * - 계층: common/domain
 * - 책임: 소속 계층의 역할을 타입으로 분리해 호출 경계를 명확히 합니다.
 */
data object MissingTokenAuthError : AuthError
