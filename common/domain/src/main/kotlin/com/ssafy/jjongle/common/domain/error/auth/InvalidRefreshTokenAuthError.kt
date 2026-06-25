package com.ssafy.jjongle.common.domain.error.auth

/**
 * 공통 기반 UI에서 공유하는 Invalid Refresh Token Auth Error 디자인 기준입니다.
 *
 * 화면별 색상, 타이포그래피, 크기 값을 직접 흩뿌리지 않고 공통 토큰을 통해 일관되게 사용합니다.
 */
data object InvalidRefreshTokenAuthError : AuthError
