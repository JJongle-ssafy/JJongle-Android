package com.ssafy.jjongle.data.model

import com.google.gson.annotations.SerializedName
import com.ssafy.jjongle.data.mapping.orMissingServerField
import com.ssafy.jjongle.data.mapping.orMissingServerId
import com.ssafy.jjongle.common.entity.GameProfileImage

/**
 * GAME_FINISH 응답으로 전달되는 유저 프로필 이미지 정보
 */
data class GameFinishProfile(
    @SerializedName("userId") val userId: Int? = null,
    @SerializedName("base64") val base64: String? = null
)

fun GameFinishProfile.toDomain(): GameProfileImage {
    return GameProfileImage(
        userId = userId.orMissingServerId(),
        base64 = base64.orMissingServerField("gameFinish.base64")
    )
}
