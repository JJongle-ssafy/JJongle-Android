package com.ssafy.jjongle.data.firebase

import com.ssafy.jjongle.data.firebase.model.UserProfileDto

interface UserProfileDataSource {
    suspend fun getProfile(uid: String): UserProfileDto?
    suspend fun saveProfile(uid: String, profile: UserProfileDto)
    suspend fun deleteProfile(uid: String)
}
