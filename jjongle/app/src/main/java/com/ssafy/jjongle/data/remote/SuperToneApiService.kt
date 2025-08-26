package com.ssafy.jjongle.data.remote

import com.ssafy.jjongle.data.model.TTSRequest
import okhttp3.ResponseBody
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.Headers
import retrofit2.http.POST
import retrofit2.http.Streaming

interface SuperToneApiService {

    // 0ba68a0776c8a0d922fa98는 se-a 보이스 아이디
    @Headers(
        "Accept: audio/mpeg"
    )
    @Streaming
    @POST("v1/text-to-speech/0ba68a0776c8a0d922fa98?output_format=mp3")
    suspend fun generateTTS(
        @Body request: TTSRequest
    ): Response<ResponseBody>
}