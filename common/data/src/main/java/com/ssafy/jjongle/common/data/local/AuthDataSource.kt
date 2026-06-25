package com.ssafy.jjongle.common.data.local

import android.content.SharedPreferences
import androidx.core.content.edit
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Auth 데이터를 외부 서비스나 로컬 저장소에서 읽고 쓰는 data 계층 경계입니다.
 *
 * Repository가 세부 API, SDK, 저장 방식에 직접 묶이지 않도록 데이터 접근 작업을 캡슐화합니다.
 */
@Singleton
class AuthDataSource @Inject constructor(
    private val prefs: SharedPreferences, // Hilt 모듈로부터 주입
) {

    /**
     * Firebase Auth와 Firestore 프로필 복원을 위한 로컬 사용자 캐시.
     */

    // 사용자 ID를 저장하는 메소드
    fun saveUserId(userId: String) {
        prefs.edit { putString(KEY_USER_ID, userId) }
    }

    // 사용자 ID를 가져오는 메소드
    fun getUserId(): String? {
        return prefs.getString(KEY_USER_ID, null)
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
            remove(KEY_USER_ID)
            remove(KEY_NICKNAME)
            remove(KEY_PROFILE_IMAGE)
        }
    }

    companion object {
        private const val KEY_USER_ID = "user_id"
        private const val KEY_NICKNAME = "nickname"
        private const val KEY_PROFILE_IMAGE = "profile_image"
    }
}
