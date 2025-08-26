package com.ssafy.jjongle.data.model

import com.google.gson.annotations.SerializedName

/**
 * GAME_FINISH 응답으로 전달되는 유저 프로필 이미지 정보
 */
data class GameFinishProfile(
    @SerializedName("userId") val userId: Int,
    @SerializedName("base64") val base64: String
)


