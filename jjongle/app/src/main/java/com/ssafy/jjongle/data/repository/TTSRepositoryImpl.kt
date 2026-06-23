package com.ssafy.jjongle.data.repository
import com.ssafy.jjongle.data.model.TTSRequest
import com.ssafy.jjongle.data.remote.SuperToneApiService
import com.ssafy.jjongle.common.entity.TtsAudio
import com.ssafy.jjongle.common.domain.repository.TTSRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class TTSRepositoryImpl @Inject constructor(
    private val superToneApiService: SuperToneApiService
) : TTSRepository {

    // no-op

    override suspend fun generateTTS(
        text: String
    ): Result<TtsAudio> = withContext(Dispatchers.IO) {
        try {
            val request = TTSRequest(text = text)

            // no-op

            val response = superToneApiService.generateTTS(
                request = request
            )

            if (response.isSuccessful) {
                val responseBody = response.body()
                if (responseBody != null) {
                    val audioLength = extractAudioLengthFromHeaders(response)
                    val contentType = responseBody.contentType()?.toString()
                    val audioBytes = responseBody.bytes()

                    Result.success(
                        TtsAudio(
                            bytes = audioBytes,
                            audioLength = audioLength,
                            contentType = contentType
                        )
                    )
                } else Result.failure(Exception("응답 본문이 null입니다"))
            } else Result.failure(Exception("API 호출 실패: ${response.code()}"))
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    /**
     * 응답 헤더에서 오디오 길이를 추출
     */
    private fun extractAudioLengthFromHeaders(response: retrofit2.Response<okhttp3.ResponseBody>): Double? {
        return try {
            // X-Audio-Length 헤더에서 오디오 길이 추출
            val audioLengthHeader = response.headers()["X-Audio-Length"]
            audioLengthHeader?.toDoubleOrNull()
        } catch (e: Exception) {
            null
        }
    }
}
