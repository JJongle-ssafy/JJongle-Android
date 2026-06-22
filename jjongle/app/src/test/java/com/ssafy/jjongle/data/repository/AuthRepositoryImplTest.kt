package com.ssafy.jjongle.data.repository

import android.content.SharedPreferences
import com.ssafy.jjongle.data.local.AuthDataSource
import com.ssafy.jjongle.data.remote.AuthRemoteDataSource
import com.ssafy.jjongle.data.remote.model.AuthTokenResponse
import com.ssafy.jjongle.data.remote.model.LogInRequest
import com.ssafy.jjongle.data.remote.model.SignUpRequest
import com.ssafy.jjongle.data.remote.model.UserUpdateRequest
import okhttp3.Headers
import okhttp3.ResponseBody.Companion.toResponseBody
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import retrofit2.Response

class AuthRepositoryImplTest {
    @Test
    fun login_persistsTokensFromRemoteResponseHeaders() = kotlinx.coroutines.test.runTest {
        val authDataSource = AuthDataSource(InMemorySharedPreferences())
        val repository = AuthRepositoryImpl(
            authRemoteDataSource = FakeAuthRemoteDataSource(
                loginResponse = Response.success(
                    AuthTokenResponse(nickname = "몽이", profileImage = "MONGI"),
                    authHeaders(accessToken = "access-token", refreshToken = "refresh-token")
                )
            ),
            authDataSource = authDataSource
        )

        val result = repository.login("firebase-token")

        assertTrue(result.isSuccess)
        assertEquals("access-token", authDataSource.getAccessToken())
        assertEquals("refresh-token", authDataSource.getRefreshToken())
        assertEquals("몽이", authDataSource.getNickname())
        assertEquals("MONGI", authDataSource.getProfileImage())
        assertTrue(result.getOrThrow().isAuthenticated)
    }

    @Test
    fun reissue_persistsTokensFromRemoteResponseHeaders() = kotlinx.coroutines.test.runTest {
        val authDataSource = AuthDataSource(InMemorySharedPreferences())
        authDataSource.saveTokens("old-access", "old-refresh")
        val repository = AuthRepositoryImpl(
            authRemoteDataSource = FakeAuthRemoteDataSource(
                reissueResponse = Response.success(
                    Unit,
                    authHeaders(accessToken = "new-access", refreshToken = "new-refresh")
                )
            ),
            authDataSource = authDataSource
        )

        val result = repository.reissue("old-refresh")

        assertTrue(result.isSuccess)
        assertEquals("new-access", authDataSource.getAccessToken())
        assertEquals("new-refresh", authDataSource.getRefreshToken())
        assertEquals("new-access", result.getOrThrow().accessToken)
        assertEquals("new-refresh", result.getOrThrow().refreshToken)
    }

    @Test
    fun login_failsWithoutPersistingTokens_whenHeadersHaveNoTokens() = kotlinx.coroutines.test.runTest {
        val authDataSource = AuthDataSource(InMemorySharedPreferences())
        val repository = AuthRepositoryImpl(
            authRemoteDataSource = FakeAuthRemoteDataSource(
                loginResponse = Response.success(
                    AuthTokenResponse(nickname = "몽이", profileImage = "MONGI"),
                    Headers.headersOf()
                )
            ),
            authDataSource = authDataSource
        )

        val result = repository.login("firebase-token")

        assertTrue(result.isFailure)
        assertEquals(null, authDataSource.getAccessToken())
        assertEquals(null, authDataSource.getRefreshToken())
    }

    private fun authHeaders(accessToken: String, refreshToken: String): Headers =
        Headers.headersOf(
            "Authorization",
            accessToken,
            "Set-Cookie",
            "refreshToken=$refreshToken; Path=/; HttpOnly"
        )

    private class FakeAuthRemoteDataSource(
        private val loginResponse: Response<AuthTokenResponse> =
            Response.error(500, "".toResponseBody()),
        private val signupResponse: Response<AuthTokenResponse> =
            Response.error(500, "".toResponseBody()),
        private val reissueResponse: Response<Unit> =
            Response.error(500, "".toResponseBody())
    ) : AuthRemoteDataSource {
        override suspend fun login(request: LogInRequest): Response<AuthTokenResponse> = loginResponse

        override suspend fun signup(request: SignUpRequest): Response<AuthTokenResponse> = signupResponse

        override suspend fun reissue(refreshToken: String): Response<Unit> = reissueResponse

        override suspend fun updateUser(body: UserUpdateRequest): Response<Unit> =
            Response.success(Unit)

        override suspend fun deleteUser(): Response<Unit> = Response.success(Unit)
    }

    private class InMemorySharedPreferences : SharedPreferences {
        private val values = linkedMapOf<String, Any?>()

        override fun getAll(): MutableMap<String, *> = values.toMutableMap()

        override fun getString(key: String, defValue: String?): String? =
            values[key] as? String ?: defValue

        @Suppress("UNCHECKED_CAST")
        override fun getStringSet(key: String, defValues: MutableSet<String>?): MutableSet<String>? =
            (values[key] as? Set<String>)?.toMutableSet() ?: defValues

        override fun getInt(key: String, defValue: Int): Int = values[key] as? Int ?: defValue

        override fun getLong(key: String, defValue: Long): Long = values[key] as? Long ?: defValue

        override fun getFloat(key: String, defValue: Float): Float = values[key] as? Float ?: defValue

        override fun getBoolean(key: String, defValue: Boolean): Boolean =
            values[key] as? Boolean ?: defValue

        override fun contains(key: String): Boolean = values.containsKey(key)

        override fun edit(): SharedPreferences.Editor = Editor()

        override fun registerOnSharedPreferenceChangeListener(
            listener: SharedPreferences.OnSharedPreferenceChangeListener?
        ) = Unit

        override fun unregisterOnSharedPreferenceChangeListener(
            listener: SharedPreferences.OnSharedPreferenceChangeListener?
        ) = Unit

        private inner class Editor : SharedPreferences.Editor {
            private val pending = linkedMapOf<String, Any?>()
            private val removals = mutableSetOf<String>()
            private var clearRequested = false

            override fun putString(key: String, value: String?): SharedPreferences.Editor =
                apply { pending[key] = value }

            override fun putStringSet(
                key: String,
                values: MutableSet<String>?
            ): SharedPreferences.Editor = apply {
                pending[key] = values?.toSet()
            }

            override fun putInt(key: String, value: Int): SharedPreferences.Editor =
                apply { pending[key] = value }

            override fun putLong(key: String, value: Long): SharedPreferences.Editor =
                apply { pending[key] = value }

            override fun putFloat(key: String, value: Float): SharedPreferences.Editor =
                apply { pending[key] = value }

            override fun putBoolean(key: String, value: Boolean): SharedPreferences.Editor =
                apply { pending[key] = value }

            override fun remove(key: String): SharedPreferences.Editor =
                apply { removals += key }

            override fun clear(): SharedPreferences.Editor =
                apply { clearRequested = true }

            override fun commit(): Boolean {
                applyChanges()
                return true
            }

            override fun apply() {
                applyChanges()
            }

            private fun applyChanges() {
                if (clearRequested) values.clear()
                removals.forEach(values::remove)
                pending.forEach { (key, value) ->
                    if (value == null) values.remove(key) else values[key] = value
                }
            }
        }
    }
}
