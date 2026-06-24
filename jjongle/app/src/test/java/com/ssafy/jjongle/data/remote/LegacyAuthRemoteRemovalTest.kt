package com.ssafy.jjongle.data.remote

import org.junit.Test

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
