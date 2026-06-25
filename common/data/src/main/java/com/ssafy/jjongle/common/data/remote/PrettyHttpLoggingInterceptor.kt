package com.ssafy.jjongle.common.data.remote

import okhttp3.Interceptor
import okhttp3.MediaType
import okhttp3.Response
import okhttp3.ResponseBody
import okhttp3.ResponseBody.Companion.toResponseBody
import okio.Buffer
import org.json.JSONArray
import org.json.JSONObject
import java.nio.charset.Charset
import java.util.concurrent.TimeUnit

/**
 * 공통 기반 흐름에서 허용되는 Http Log Level 값의 집합입니다.
 *
 * 분기 가능한 상태나 이벤트를 타입으로 제한해 잘못된 문자열/숫자 값이 계층 사이로 전달되지 않게 합니다.
 */
enum class HttpLogLevel {
    VERBOSE,
    INFO,
    ERROR,
}

fun interface HttpLogger {
    fun log(level: HttpLogLevel, tag: String, message: String)

    companion object {
        val NoOp = HttpLogger { _, _, _ -> }
    }
}

/**
 * 개발 중 HTTP 요청과 응답 본문을 읽기 쉬운 형태로 출력하는 OkHttp Interceptor입니다.
 *
 * 운영 로직과 분리된 네트워크 진단 도구로, API 계약 확인과 디버깅에만 사용합니다.
 */
class PrettyHttpLoggingInterceptor(
    private val logger: HttpLogger = HttpLogger.NoOp,
) : Interceptor {

    companion object {
        private const val TAG = "OkHttp"
        private val UTF8 = Charset.forName("UTF-8")
    }

    override fun intercept(chain: Interceptor.Chain): Response {
        val request = chain.request()
        val requestBody = request.body

        var requestBodyString: String? = null
        if (requestBody != null) {
            val buffer = Buffer()
            requestBody.writeTo(buffer)
            requestBodyString = buffer.readString(UTF8)
        }

        // ┌─────── Request ───────
        logger.info("┌─ Request ───────────────────────────────────────────────────────────────────")
        logger.info("│ ${request.method} ${request.url}")

        if (request.headers.size > 0) {
            logger.verbose("│ Headers:")
            request.headers.forEach { header ->
                logger.verbose("│  ${header.first}: ${header.second}")
            }
        }

        if (requestBodyString != null) {
            logger.verbose("│ Body:")
            logger.verbose("│  ${getPrettyJson(requestBodyString)}")
        }
        logger.info("└─────────────────────────────────────────────────────────────────────────────")
        // └───────────────────────

        val startNs = System.nanoTime()
        val response: Response
        try {
            response = chain.proceed(request)
        } catch (e: Exception) {
            logger.error("┌─ Error ─────────────────────────────────────────────────────────────────────")
            logger.error("│ HTTP FAILED: $e")
            logger.error("└─────────────────────────────────────────────────────────────────────────────")
            throw e
        }
        val tookMs = TimeUnit.NANOSECONDS.toMillis(System.nanoTime() - startNs)

        val responseBody = response.body
        val mediaType: MediaType? = responseBody?.contentType()
        val isTextBody = isTextLike(mediaType)
        var bodyString: String? = null
        if (isTextBody && responseBody != null) {
            // 텍스트/JSON만 안전하게 본문 로깅
            bodyString = responseBody.string()
        }

        val logLevel = if (response.isSuccessful) HttpLogLevel.INFO else HttpLogLevel.ERROR

        // ┌─────── Response ───────
        logger.log(logLevel, TAG, "┌─ Response ──────────────────────────────────────────────────────────────────")
        logger.log(logLevel, TAG, "│ ${response.code} ${response.message} ${response.request.url} (${tookMs}ms)")

        if (response.headers.size > 0) {
            logger.verbose("│ Headers:")
            response.headers.forEach { header ->
                logger.verbose("│  ${header.first}: ${header.second}")
            }
        }
        
        if (!bodyString.isNullOrEmpty()) {
            logger.verbose("│ Body:")
            logger.verbose("│  ${getPrettyJson(bodyString)}")
        } else if (responseBody != null && !isTextBody) {
            logger.verbose("│ Body: <binary ${mediaType ?: "unknown"}; length=${responseBody.contentLength()}> (omitted)")
        }

        logger.log(logLevel, TAG, "└─────────────────────────────────────────────────────────────────────────────")
        // └────────────────────────

        // 본문을 읽은 경우에만 교체. 바이너리는 절대 건드리지 않음.
        return if (!bodyString.isNullOrEmpty() && responseBody != null && isTextBody) {
            response.newBuilder()
                .body(bodyString.toResponseBody(responseBody.contentType()))
                .build()
        } else {
            response
        }
    }

    /**
     * 문자열이 JSON 형태이면 예쁘게 포맷팅하고, 아니면 그대로 반환합니다.
     */
    private fun getPrettyJson(jsonString: String?): String {
        if (jsonString.isNullOrEmpty()) {
            return "Empty/Null json content"
        }
        return try {
            val trimmed = jsonString.trim()
            if (trimmed.startsWith("{")) {
                JSONObject(trimmed).toString(2)
            } else if (trimmed.startsWith("[")) {
                JSONArray(trimmed).toString(2)
            } else {
                jsonString
            }
        } catch (e: Exception) {
            jsonString // JSON 파싱 실패 시 원본 문자열 반환
        }
    }

    private fun isTextLike(mediaType: MediaType?): Boolean {
        if (mediaType == null) return false
        val type = mediaType.type
        val subtype = mediaType.subtype.lowercase()
        if (type == "text") return true
        if (type == "application") {
            return subtype.contains("json") ||
                subtype.contains("xml") ||
                subtype.contains("x-www-form-urlencoded") ||
                subtype.contains("javascript")
        }
        return false
    }

    private fun HttpLogger.verbose(message: String) = log(HttpLogLevel.VERBOSE, TAG, message)

    private fun HttpLogger.info(message: String) = log(HttpLogLevel.INFO, TAG, message)

    private fun HttpLogger.error(message: String) = log(HttpLogLevel.ERROR, TAG, message)
}
