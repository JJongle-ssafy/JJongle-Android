package com.ssafy.jjongle.data.websocket

import com.google.gson.Gson
import com.ssafy.jjongle.domain.entity.GameEvent
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class GameWebSocketEventParserTest {
    private val parser = GameWebSocketEventParser(Gson())

    @Test
    fun parse_gameStartMapsToDomainEvent() {
        val json = """
            {
              "type": "GAME_START",
              "data": {
                "message": "start",
                "sessionKey": "session-1",
                "quizList": [
                  {
                    "quizId": 10,
                    "question": "하늘은 파란색이다",
                    "answer": "O",
                    "description": "맑은 날에는 파란색으로 보입니다."
                  }
                ]
              }
            }
        """.trimIndent()

        val event = parser.parse(json)

        assertTrue(event is GameEvent.GameStart)
        event as GameEvent.GameStart
        assertEquals("session-1", event.sessionKey)
        assertEquals(1, event.quizzes.size)
        assertEquals(10, event.quizzes.first().id)
    }

    @Test
    fun parse_submitResultMapsPositionsToDomainEvent() {
        val json = """
            {
              "type": "SUBMIT_RESULT",
              "data": {
                "quizId": 10,
                "correctAnswer": "X",
                "correctUserPositions": [
                  { "userId": 7, "x": 12.5, "y": 20.0 }
                ]
              }
            }
        """.trimIndent()

        val event = parser.parse(json)

        assertTrue(event is GameEvent.SubmitResult)
        event as GameEvent.SubmitResult
        assertEquals(10, event.quizId)
        assertEquals("X", event.correctAnswer)
        assertEquals(7, event.correctUserPositions.first().userId)
    }

    @Test
    fun parse_unknownTypeReturnsUnknownEvent() {
        val event = parser.parse("""{"type":"NEW_EVENT","data":{}}""")

        assertEquals(GameEvent.Unknown, event)
    }

    @Test
    fun parse_missingTypeReturnsUnknownEvent() {
        val event = parser.parse("""{"data":{}}""")

        assertEquals(GameEvent.Unknown, event)
    }

    @Test
    fun parse_gameStartWithMissingDataUsesFallbackSession() {
        val event = parser.parse("""{"type":"GAME_START"}""")

        assertTrue(event is GameEvent.GameStart)
        event as GameEvent.GameStart
        assertEquals("[MISSING_SERVER_FIELD:gameStart.sessionKey]", event.sessionKey)
        assertEquals(emptyList<Any>(), event.quizzes)
    }

    @Test
    fun parse_submitResultWithMissingDataUsesFallbacks() {
        val event = parser.parse("""{"type":"SUBMIT_RESULT"}""")

        assertTrue(event is GameEvent.SubmitResult)
        event as GameEvent.SubmitResult
        assertEquals(-1, event.quizId)
        assertEquals("[MISSING_SERVER_FIELD:submitResult.correctAnswer]", event.correctAnswer)
        assertEquals(emptyList<Any>(), event.correctUserPositions)
    }

    @Test
    fun parse_gameFinishWithNullProfileFieldsUsesFallbackProfile() {
        val json = """
            {
              "type": "GAME_FINISH_RESULT",
              "data": {
                "userImages": [
                  { "userId": null, "base64": null },
                  null
                ]
              }
            }
        """.trimIndent()

        val event = parser.parse(json)

        assertTrue(event is GameEvent.GameFinish)
        event as GameEvent.GameFinish
        assertEquals(1, event.profiles.size)
        assertEquals(-1, event.profiles.first().userId)
        assertEquals("[MISSING_SERVER_FIELD:gameFinish.base64]", event.profiles.first().base64)
    }
}
