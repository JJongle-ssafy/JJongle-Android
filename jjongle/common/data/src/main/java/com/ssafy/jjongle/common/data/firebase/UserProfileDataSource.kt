package com.ssafy.jjongle.common.data.firebase

import com.ssafy.jjongle.common.data.firebase.model.UserProfileDto

/**
 * UserProfileDataSource 데이터 원본 접근을 담당합니다.
 *
 * - 계층: common/data
 * - 책임: 저장소 구현이 사용할 원격 또는 로컬 데이터 작업을 캡슐화합니다.
 */
interface UserProfileDataSource {
    suspend fun getProfile(uid: String): UserProfileDto?
    suspend fun saveProfile(uid: String, profile: UserProfileDto)
    suspend fun deleteProfile(uid: String)
}
