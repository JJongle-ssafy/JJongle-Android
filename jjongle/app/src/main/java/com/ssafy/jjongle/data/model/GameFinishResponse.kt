package com.ssafy.jjongle.data.model

import com.google.gson.annotations.SerializedName

/**
 * 신버전: type = "GAME_FINISH_RESULT", data.userImages = [ { userId, base64 } ]
 */
data class GameFinishResponse(
    @SerializedName("data") val data: GameFinishResultData
) : BaseResponse("GAME_FINISH_RESULT")

data class GameFinishResultData(
    @SerializedName("userImages") val userImages: List<GameFinishProfile>
)