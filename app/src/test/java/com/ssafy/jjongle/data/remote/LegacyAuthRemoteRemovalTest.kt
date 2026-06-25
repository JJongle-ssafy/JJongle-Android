package com.ssafy.jjongle.data.remote

import org.junit.Test

/**
 * Legacy Auth Remote Removal Test 회귀를 막기 위한 테스트입니다.
 *
 * 리팩터링이나 모듈 이동 중에도 기존 동작, 아키텍처 경계, 오류 처리 계약이 유지되는지 확인합니다.
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
