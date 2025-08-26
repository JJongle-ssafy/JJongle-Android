package com.ssafy.jjongle.data.local

import android.content.SharedPreferences
import androidx.core.content.edit
import javax.inject.Inject
import javax.inject.Singleton

/**
 * 게임 세션 관련 SharedPreferences 관리
 */
@Singleton
class SessionDataSource @Inject constructor(
    private val sharedPreferences: SharedPreferences
) {
    companion object {
        private const val KEY_SESSION_KEY = "session_key"
        private const val KEY_USER_ID = "user_id"
        private const val KEY_GAME_START_TIME = "game_start_time"
    }

    /**
     * 세션 키 저장
     */
    fun saveSessionKey(sessionKey: String) {
        sharedPreferences.edit {
            putString(KEY_SESSION_KEY, sessionKey)
                .putLong(KEY_GAME_START_TIME, System.currentTimeMillis())
        }
    }

    /**
     * 세션 키 조회
     */
    fun getSessionKey(): String? {
        return sharedPreferences.getString(KEY_SESSION_KEY, null)
    }

    /**
     * 유저 ID 저장
     */
    fun saveUserId(userId: Int) {
        sharedPreferences.edit {
            putInt(KEY_USER_ID, userId)
        }
    }

    /**
     * 유저 ID 조회
     */
    fun getUserId(): Int {
        return sharedPreferences.getInt(KEY_USER_ID, -1)
    }

    /**
     * 게임 시작 시간 조회
     */
    fun getGameStartTime(): Long {
        return sharedPreferences.getLong(KEY_GAME_START_TIME, 0L)
    }

    /**
     * 세션 정보 모두 삭제
     */
    fun clearSession() {
        sharedPreferences.edit { clear() }
    }

    /**
     * 유효한 세션인지 확인 (30분 내)
     */
    fun isSessionValid(): Boolean {
        val sessionKey = getSessionKey()
        val startTime = getGameStartTime()
        val currentTime = System.currentTimeMillis()
        val thirtyMinutes = 30 * 60 * 1000L // 30분을 밀리초로

        return sessionKey != null &&
                startTime > 0 &&
                (currentTime - startTime) < thirtyMinutes
    }

}