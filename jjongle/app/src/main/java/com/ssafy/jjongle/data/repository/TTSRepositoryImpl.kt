package com.ssafy.jjongle.data.repository
import com.ssafy.jjongle.data.model.TTSRequest
import com.ssafy.jjongle.data.model.TTSResponse
import com.ssafy.jjongle.data.model.TTSResponseWrapper
import com.ssafy.jjongle.data.remote.SuperToneApiService
import com.ssafy.jjongle.domain.repository.TTSRepository
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
    ): Result<TTSResponseWrapper> = withContext(Dispatchers.IO) {
        try {
            val request = TTSRequest(text = text)

            // no-op

            val response = superToneApiService.generateTTS(
                request = request
            )

            if (response.isSuccessful) {
                val responseBody = response.body()
                if (responseBody != null) {
                    // 헤더에서 오디오 길이 추출
                    val audioLength = extractAudioLengthFromHeaders(response)

                    // 실제 바이트 추출
                    val audioBytes = try { responseBody.bytes() } catch (e: Exception) { null }

                     val wrapper = TTSResponseWrapper(
                         responseBody = responseBody,
                         audioLength = audioLength,
                         audioBytes = audioBytes
                     )

                    Result.success(wrapper)
                } else Result.failure(Exception("응답 본문이 null입니다"))
            } else Result.failure(Exception("API 호출 실패: ${response.code()}"))
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun generateTTSResponse(
        text: String
    ): Result<TTSResponse> = withContext(Dispatchers.IO) {
        try {
            val result = generateTTS(text)
            result.mapCatching { wrapper ->
                val bytes = wrapper.audioBytes ?: return@mapCatching wrapper.responseBody
                // bytes로 새로운 ResponseBody 생성 (재사용 가능)
                okhttp3.ResponseBody.create(wrapper.responseBody.contentType(), bytes)
            }
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
