package com.ssafy.jjongle.data.remote

import org.junit.Test

/**
 * LegacyAuthRemoteRemoval의 아키텍처 또는 동작 계약을 검증하는 테스트입니다.
 *
 * - 계층: test
 * - 책임: 회귀를 막는 대표 시나리오와 모듈 경계 조건을 실행합니다.
 */
class LegacyAuthRemoteRemovalTest {

    @Test
    fun legacy_backend_auth_remote_stack_is_not_on_classpath() {
        listOf(
            "com.ssafy.jjongle.data.remote.AuthApiService",
            "com.ssafy.jjongle.data.remote.AuthRemoteDataSource",
            "com.ssafy.jjongle.data.remote.AuthRemoteDataSourceImpl",
            "com.ssafy.jjongle.data.remote.AuthInterceptor",
            "com.ssafy.jjongle.data.remote.model.AuthTokenResponse",
            "com.ssafy.jjongle.data.remote.model.LogInRequest",
            "com.ssafy.jjongle.data.remote.model.SignUpRequest",
            "com.ssafy.jjongle.data.remote.model.UserUpdateRequest",
            "com.ssafy.jjongle.di.RemoteModule",
        ).forEach { className ->
            assertClassIsAbsent(className)
        }
    }

    private fun assertClassIsAbsent(className: String) {
        try {
            Class.forName(className)
            throw AssertionError("$className should have been removed")
        } catch (_: ClassNotFoundException) {
        }
    }
}
