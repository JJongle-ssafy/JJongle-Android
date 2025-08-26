package com.ssafy.jjongle.data.websocket

import android.util.Log
import com.google.gson.Gson
import com.ssafy.jjongle.data.local.AuthDataSource
import com.ssafy.jjongle.data.model.AnalysisResultResponse
import com.ssafy.jjongle.data.model.BaseRequest
import com.ssafy.jjongle.data.model.BaseResponse
import com.ssafy.jjongle.data.model.GameFinishData
import com.ssafy.jjongle.data.model.GameFinishProfile
import com.ssafy.jjongle.data.model.GameFinishResponse
import com.ssafy.jjongle.data.model.GameStartResponse
import com.ssafy.jjongle.data.model.ImageAnalysisData
import com.ssafy.jjongle.data.model.QuizResponse
import com.ssafy.jjongle.data.model.SubmitAnswerData
import com.ssafy.jjongle.data.model.SubmitResultResponse
import com.ssafy.jjongle.data.model.UserPositionDto
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import okhttp3.WebSocket
import okhttp3.WebSocketListener
import org.json.JSONArray
import org.json.JSONObject
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class GameWebSocketManager @Inject constructor(
    private val okHttpClient: OkHttpClient,
    private val authDataSource: AuthDataSource,
    private val gson: Gson // Gson 주입
) {
    private companion object {
        // TODO: 실제 WebSocket URL로 변경해야 합니다.
        private const val WS_BASE_URL = "ws://i13d106.p.ssafy.io:8080/ws/group-game"
        private const val TAG = "GameWebSocket" // 로그 태그 추가
    }

    private var webSocket: WebSocket? = null

    // 이 Manager의 생명주기와 함께할 코루틴 스코프
    private val scope = CoroutineScope(Dispatchers.IO + SupervisorJob())

    private val _connectionState = MutableStateFlow(ConnectionState.DISCONNECTED)
    val connectionState = _connectionState.asStateFlow()

    @Volatile
    private var isDisconnectingManually = false


    // 이벤트를 놓치지 않기 위해 SharedFlow 사용
    private val _gameEvents = MutableSharedFlow<GameEvent>()
    val gameEvents = _gameEvents.asSharedFlow()

    val accessToken =
        authDataSource.getAccessToken() ?: throw IllegalStateException("Access token is null")

    fun connect() {
        if (_connectionState.value == ConnectionState.CONNECTED || _connectionState.value == ConnectionState.CONNECTING) {
            Log.d(TAG, "이미 연결 중이거나 연결됨. 재연결 시도 중단.")
            return
        }
        isDisconnectingManually = false // 연결 시도 시 플래그 초기화
        _connectionState.value = ConnectionState.CONNECTING
        val request = Request.Builder()
            .url("$WS_BASE_URL?token=$accessToken") // 세션 키를 URL에 포함
            .build()
        webSocket = okHttpClient.newWebSocket(request, GameWebSocketListener())
    }

    fun disconnect() {
        if (_connectionState.value == ConnectionState.DISCONNECTED || _connectionState.value == ConnectionState.DISCONNECTING) {
            return
        }
        isDisconnectingManually = true // 수동 연결 해제 플래그 설정
        Log.d(TAG, "수동으로 WebSocket 연결 해제 시작.")
        webSocket?.close(1000, "User disconnected")
        webSocket = null
        _connectionState.value = ConnectionState.DISCONNECTED
        Log.d(TAG, "WebSocket 연결 상태를 DISCONNECTED로 설정함.")
    }

    /**
     * 이미지 분석 요청 전송
     */
    fun sendImageAnalysis(sessionKey: String, quizId: Int, base64Image: String) {
        val data = ImageAnalysisData(sessionKey, quizId, base64Image)
        val request = BaseRequest("IMAGE_ANALYSIS", data)
        val jsonMessage = gson.toJson(request)
        webSocket?.send(jsonMessage)
//        Log.d(TAG, "메시지 전송 (IMAGE_ANALYSIS): $jsonMessage")
    }

    /**
     * 최종 답변 제출 요청 전송
     */
    fun sendSubmitAnswer(sessionKey: String, quizId: Int, base64Image: String) {
        val data = SubmitAnswerData(sessionKey, quizId, base64Image)
        val request = BaseRequest("SUBMIT_ANSWER", data)
        val jsonMessage = gson.toJson(request)
        webSocket?.send(jsonMessage)
        Log.d(TAG, "메시지 전송 (SUBMIT_ANSWER): $jsonMessage")
    }

    /**
     * 게임 종료 결과 요청 전송 (프로필 이미지 수신을 위해)
     */
    fun sendGameFinish(sessionKey: String, quizId: Int, base64Image: String) {
        // Use the same payload shape as other requests to guarantee keys: sessionKey, quizId, imageBase64
        val data = GameFinishData(sessionKey, quizId, base64Image)
        val request = BaseRequest("GAME_FINISH", data)
        val jsonMessage = gson.toJson(request)
        webSocket?.send(jsonMessage)
        //request.
        Log.d(TAG, "메시지 전송 (GAME_FINISH): $jsonMessage")
    }

    private inner class GameWebSocketListener : WebSocketListener() {
        override fun onOpen(webSocket: WebSocket, response: Response) {
            _connectionState.value = ConnectionState.CONNECTED
            Log.d(TAG, "onOpen: WebSocket 연결 성공. 응답: ${response.code} ${response.message}")
        }

        override fun onMessage(webSocket: WebSocket, text: String) {
            logPrettyJson(text) // 예쁘게 포맷된 JSON 로그 출력

            // 코루틴을 시작하여 suspend 함수를 안전하게 호출
            scope.launch {
                parseAndEmitGameEvent(text)
            }
        }

        override fun onClosing(webSocket: WebSocket, code: Int, reason: String) {
            _connectionState.value = ConnectionState.DISCONNECTING
            Log.d(TAG, "onClosing: WebSocket 닫힘 요청. 코드: $code, 이유: $reason")
        }

        override fun onClosed(webSocket: WebSocket, code: Int, reason: String) {
            if (isDisconnectingManually) {
                Log.d(TAG, "onClosed: 수동 연결 해제 완료.")
            }
            _connectionState.value = ConnectionState.DISCONNECTED
            Log.d(TAG, "onClosed: WebSocket 연결 종료됨. 코드: $code, 이유: $reason")
        }

        override fun onFailure(webSocket: WebSocket, t: Throwable, response: Response?) {
            if (isDisconnectingManually) {
                Log.w(TAG, "onFailure: 수동 연결 해제 중 발생한 오류 (무시): ${t.message}")
                _connectionState.value = ConnectionState.DISCONNECTED // 에러 대신 DISCONNECTED로 처리
                return
            }
            _connectionState.value = ConnectionState.ERROR
            Log.e(TAG, "onFailure: WebSocket 오류 발생: ${t.message}", t)
            response?.let {
                Log.e(TAG, "onFailure: 오류 응답: ${it.code} ${it.message}")
            }
        }
    }

    /**
     * 받은 JSON 메시지를 파싱하여 적절한 GameEvent로 변환하고 emit
     */
    private suspend fun parseAndEmitGameEvent(json: String) {
        try {
            val baseResponse = gson.fromJson(json, BaseResponse::class.java)
            val event = when (baseResponse.type) {

                "GAME_START" -> {
                    val response = gson.fromJson(json, GameStartResponse::class.java)
                    GameEvent.GameStart(response.data.quizList, response.data.sessionKey)
                }

                "SUBMIT_RESULT" -> {
                    val response = gson.fromJson(json, SubmitResultResponse::class.java)
                    GameEvent.SubmitResult(
                        response.data.quizId,
                        response.data.correctAnswer,
                        response.data.correctUserPositions
                    )
                }

                "ANALYSIS_RESULT" -> {
                    val response = gson.fromJson(json, AnalysisResultResponse::class.java)
                    GameEvent.AnalysisResult(response.data)
                }

                "GAME_FINISH_RESULT" -> {
                    val response = gson.fromJson(json, GameFinishResponse::class.java)
                    GameEvent.GameFinish(response.data.userImages)
                }


                else -> {
                    Log.w(TAG, "파싱: 알 수 없는 이벤트 타입: ${baseResponse.type}")
                    GameEvent.Unknown // 알 수 없는 타입 처리
                }
            }
            _gameEvents.emit(event)
        } catch (e: Exception) {
            Log.e(TAG, "JSON 파싱 실패: ${e.message}, 원본 JSON: $json", e)
            _gameEvents.emit(GameEvent.Error("JSON 파싱 실패: ${e.message}"))
        }
    }

    /**
     * 수신된 WebSocket 메시지를 예쁘게 포맷하여 로그로 출력합니다.
     */
    private fun logPrettyJson(jsonString: String?) {
        val webSocketTag = "WebSocketMessage" // WebSocket 메시지 전용 태그
        if (jsonString.isNullOrEmpty()) {
            Log.d(webSocketTag, "Received empty or null message.")
            return
        }
        Log.d(
            webSocketTag,
            "┌─ WebSocket Message ────────────────────────────────────────────────────────"
        )
        try {
            val trimmed = jsonString.trim()
            val message = if (trimmed.startsWith("{")) {
                JSONObject(trimmed).toString(2)
            } else if (trimmed.startsWith("[")) {
                JSONArray(trimmed).toString(2)
            } else {
                jsonString
            }
            message.lines().forEach { line ->
                Log.d(webSocketTag, "│ $line")
            }
        } catch (e: Exception) {
            Log.d(webSocketTag, "│ $jsonString") // JSON 파싱 실패 시 원본 출력
        }
        Log.d(
            webSocketTag,
            "└─────────────────────────────────────────────────────────────────────────────"
        )
    }
}

// WebSocket 연결 상태
enum class ConnectionState {
    DISCONNECTED,
    CONNECTING,
    CONNECTED,
    DISCONNECTING,
    ERROR
}

// 게임 이벤트 종류
sealed class GameEvent {
    data class ConnectionEstablished(val message: String) : GameEvent()
    data class GameStart(val quizList: List<QuizResponse>, val sessionKey: String) : GameEvent()

    data class SubmitResult(
        val quizId: Int,
        val correctAnswer: String,
        val correctUserPositions: List<UserPositionDto>
    ) : GameEvent()

    data class AnalysisResult(val data: String) : GameEvent()

    // 게임 종료 시, 서버에서 보내주는 프로필(base64) 리스트
    data class GameFinish(val profiles: List<GameFinishProfile>) :
        GameEvent()

    data class Error(val message: String) : GameEvent()
    data object Unknown : GameEvent()
}
