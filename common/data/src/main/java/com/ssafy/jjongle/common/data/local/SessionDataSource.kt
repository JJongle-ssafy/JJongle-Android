package com.ssafy.jjongle.common.data.local

import android.content.SharedPreferences
import androidx.core.content.edit
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Session 데이터를 외부 서비스나 로컬 저장소에서 읽고 쓰는 data 계층 경계입니다.
 *
 * Repository가 세부 API, SDK, 저장 방식에 직접 묶이지 않도록 데이터 접근 작업을 캡슐화합니다.
 */
@Singleton
class SessionDataSource @Inject constructor(
    private val sharedPreferences: SharedPreferences
) {
    companion object {
        private const val KEY_SESSION_KEY = "session_key"
        private const val KEY_SESSION_USER_ID = "session_user_id"
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
            putInt(KEY_SESSION_USER_ID, userId)
        }
    }

    /**
     * 유저 ID 조회
     */
    fun getUserId(): Int {
        return sharedPreferences.getInt(KEY_SESSION_USER_ID, -1)
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
        sharedPreferences.edit {
            remove(KEY_SESSION_KEY)
            remove(KEY_SESSION_USER_ID)
            remove(KEY_GAME_START_TIME)
        }
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
