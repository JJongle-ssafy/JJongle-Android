package com.ssafy.jjongle.data.remote

import android.util.Log
import com.ssafy.jjongle.data.local.AuthDataSource
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.sync.withLock
import okhttp3.HttpUrl
import okhttp3.Interceptor
import okhttp3.Response
import javax.inject.Inject
import javax.inject.Named

// 오래 방치 시 첫 요청이 동시에 여러 개 날아가면, 재발급도 동시에 여러 번 발생
// → 서버가 refreshToken을 회전시키며 둘 중 하나는 INVALID_REFRESH_TOKEN이 됨.
// 이걸 막으려면 재발급은 한 번만 수행하고, 나머지는 결과를 공유해야 한다
private val refreshMutex = kotlinx.coroutines.sync.Mutex()

class AuthInterceptor @Inject constructor(
    private val authDataSource: AuthDataSource,
    @Named("refresh") private val api: AuthApiService
) : Interceptor {

    private fun stripBearer(raw: String?): String =
        raw?.replaceFirst(Regex("^Bearer\\s+", RegexOption.IGNORE_CASE), "")?.trim().orEmpty()

    private fun isAuthPath(url: HttpUrl) = url.encodedPath.startsWith("/auth/")

    override fun intercept(chain: Interceptor.Chain): Response {
        val original = chain.request()
        if (isAuthPath(original.url)) return chain.proceed(original)

        // 1) Authorization 부착 (항상 Bearer 1회)
        val access = stripBearer(authDataSource.getAccessToken())
        val req = original.newBuilder()
            .removeHeader("Authorization")
            .apply { if (access.isNotBlank()) header("Authorization", "Bearer $access") }
            .build()

        var res = chain.proceed(req)
        if (res.code != 401) return res

        // 2) 401 → 재발급 (단일 비행)
        val refreshed = runBlocking {
            refreshMutex.withLock {
                // 더블체크: 누군가 이미 갱신했는지 확인
                val latest = stripBearer(authDataSource.getAccessToken())
                if (latest.isNotBlank() && latest != access) return@withLock true

                val refresh = stripBearer(authDataSource.getRefreshToken())
                if (refresh.isBlank()) return@withLock false

                val cookieHeader = authDataSource.buildCookieHeaderForReissue(refresh)
                Log.d("Reissue", "➡️ Cookie header(for reissue)=${cookieHeader}")

                val r = api.reissueTokenByCookie(cookie = cookieHeader)
                if (!r.isSuccessful) {
                    if (r.code() == 401) {
                        // INVALID_REFRESH_TOKEN 등: 회복 불가 → 세션 정리
                        authDataSource.clearAuthData()
                    }
                    return@withLock false
                }

                val newAccess = stripBearer(r.headers()["Authorization"])
                val setCookies = r.headers().values("Set-Cookie")
                authDataSource.saveSetCookies(setCookies)

                val cookieRefresh = setCookies.firstOrNull { it.startsWith("refreshToken=") }
                    ?.substringAfter("refreshToken=")?.substringBefore(";")

                if (newAccess.isBlank() || cookieRefresh.isNullOrBlank()) return@withLock false
                authDataSource.saveTokens(newAccess, cookieRefresh)
                true
            }
        }

        if (!refreshed) {
            // 재발급 실패: 기존 401을 그대로 반환 (닫지 말고 반환)
            return res
        }

        // 재발급 성공: 이제 원 응답 닫고 재시도
        res.close()
        val latest = stripBearer(authDataSource.getAccessToken())
        val retry = original.newBuilder()
            .removeHeader("Authorization")
            .header("Authorization", "Bearer $latest")
            .build()
        return chain.proceed(retry)
    }
}

