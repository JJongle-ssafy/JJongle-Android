package com.ssafy.jjongle.data.local

import android.content.SharedPreferences
import android.util.Log
import androidx.core.content.edit
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AuthDataSource @Inject constructor(
    private val prefs: SharedPreferences, // Hilt 모듈로부터 주입
) {

    /**
     * Token 관리 클래스
     * - 로그인 시 저장되는 accessToken, refreshToken, userId 등을 관리
     * - 앱 종료 후에도 유지되는 데이터로, 앱 재시작 시 자동 로그인 기능에 사용
     */

    // SharedPreferences에 저장된 인증 토큰을 관리
    fun saveTokens(accessToken: String, refreshToken: String) {
        prefs.edit {
            putString(KEY_ACCESS_TOKEN, accessToken)
            putString(KEY_REFRESH_TOKEN, refreshToken)
        }
        Log.d("AuthLocalDataSource", "✅ accessToken 저장됨: $accessToken")
        Log.d("AuthLocalDataSource", "✅ refreshToken 저장됨: $refreshToken")
    }

    // 토큰을 가져오는 메소드
    fun getAccessToken(): String? {
        return prefs.getString(KEY_ACCESS_TOKEN, null)
    }

    // 리프레시 토큰을 가져오는 메소드
    fun getRefreshToken(): String? {
        return prefs.getString(KEY_REFRESH_TOKEN, null)
    }

    // 사용자 ID를 저장하는 메소드
    fun saveUserId(userId: String) {
        prefs.edit { putString(KEY_USER_ID, userId) }
    }

    // 사용자 ID를 가져오는 메소드
    fun getUserId(): String? {
        return prefs.getString(KEY_USER_ID, null)
    }


    /**
     * 쿠키 관리 클래스
     * - 서버와의 통신에서 필요한 쿠키들을 관리
     * - 재발급 시 필요한 Cookie 헤더 문자열을 구성
     */

    // ▼ 모든 Set-Cookie 저장
    fun saveSetCookies(cookies: List<String>) {
        prefs.edit { putStringSet(KEY_SET_COOKIES, cookies.toSet()) }
    }

    // ▼ 재발급용 Cookie 헤더 조립 (JSESSIONID 등 + refreshToken)
    fun buildCookieHeaderForReissue(refreshToken: String): String {
        val raw = prefs.getStringSet(KEY_SET_COOKIES, emptySet())!!.toList()
        val pairs = raw.map { it.substringBefore(";") } // name=value
        return (pairs + "refreshToken=$refreshToken")
            .distinctBy { it.substringBefore("=") }
            .joinToString("; ")
    }


    /**
     * 유저 프로필 관리 클래스
     */

    // SharedPreferences에 유저 프로필 저장
    fun saveUserProfile(nickname: String, profileImage: String) {
        prefs.edit {
            putString(KEY_NICKNAME, nickname)
            putString(KEY_PROFILE_IMAGE, profileImage)
        }
    }

    // 유저 정보 조회
    fun getNickname(): String? = prefs.getString(KEY_NICKNAME, null)
    fun getProfileImage(): String? = prefs.getString(KEY_PROFILE_IMAGE, null)


    /**
     * 인증 데이터 관리 메소드
     */

    // 인증 데이터를 모두 지우는 메소드
    fun clearAuthData() {
        prefs.edit {
            remove(KEY_ACCESS_TOKEN)
            remove(KEY_REFRESH_TOKEN)
            remove(KEY_USER_ID)
            remove(KEY_NICKNAME)
            remove(KEY_PROFILE_IMAGE)
            remove(KEY_SET_COOKIES)
        }
    }

    // 로그인 상태 확인 메소드
    fun isLoggedIn(): Boolean {
        return getAccessToken() != null
    }


    companion object {
        private const val KEY_USER_ID = "user_id"
        private const val KEY_ACCESS_TOKEN = "access_token"
        private const val KEY_REFRESH_TOKEN = "refresh_token"
        private const val KEY_NICKNAME = "nickname"
        private const val KEY_PROFILE_IMAGE = "profile_image"
        private const val KEY_SET_COOKIES = "set_cookies" // 쿠키 저장 키
    }
}