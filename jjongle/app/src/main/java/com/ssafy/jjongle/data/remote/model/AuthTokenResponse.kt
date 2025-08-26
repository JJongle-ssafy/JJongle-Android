package com.ssafy.jjongle.data.remote.model

// /auth/login 응답 모델
// 로그인 성공 시 서버에서 반환하는 데이터 구조
// accessToken: JWT 토큰
// refreshToken: 갱신 토큰
// userId: 사용자 ID (백엔드에서 관리하는 고유 ID)

data class AuthTokenResponse(
    val nickname: String,
    val profileImage: String
)