package com.ssafy.jjongle.data.model

import com.google.gson.annotations.SerializedName
import com.ssafy.jjongle.common.entity.GameProfileImage

/**
 * 신버전: type = "GAME_FINISH_RESULT", data.userImages = [ { userId, base64 } ]
 */
data class GameFinishResponse(
    @SerializedName("data") val data: GameFinishResultData? = null
) : BaseResponse("GAME_FINISH_RESULT")

fun GameFinishResponse.toDomainProfiles(): List<GameProfileImage> {
    return data?.userImages
        .orEmpty()
        .mapNotNull { it?.toDomain() }
}
