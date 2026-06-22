package com.ssafy.jjongle.data.repository

import android.content.SharedPreferences
import com.ssafy.jjongle.data.game.LocalOXGameEngine
import com.ssafy.jjongle.data.local.SessionDataSource
import com.ssafy.jjongle.data.local.oxgame.OXGameHistoryDao
import com.ssafy.jjongle.data.local.oxgame.OXGameHistoryEntity
import com.ssafy.jjongle.data.local.oxgame.OXGameHistoryWithNotes
import com.ssafy.jjongle.data.local.oxgame.OXWrongAnswerNoteEntity
import com.ssafy.jjongle.domain.entity.GameConnectionState
import com.ssafy.jjongle.domain.entity.GameStartEvent
import com.ssafy.jjongle.domain.entity.Quiz
import com.ssafy.jjongle.domain.entity.SubmitResultEvent
import com.ssafy.jjongle.domain.entity.UserPosition
import com.ssafy.jjongle.domain.repository.OXQuizRepository
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.CoroutineStart
import kotlinx.coroutines.flow.filterIsInstance
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withTimeout
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class OXGameRepositoryImplTest {

    @Test
    fun connectWebSocket_startsLocalGameAndSavesSession() = runBlocking {
        val sessionDataSource = SessionDataSource(InMemorySharedPreferences())
        val repository = createRepository(sessionDataSource)
        val eventDeferred = CompletableDeferred<GameStartEvent>()
        val job = launch(start = CoroutineStart.UNDISPATCHED) {
            repository.gameEvents.filterIsInstance<GameStartEvent>().collect { event ->
                eventDeferred.complete(event)
            }
        }

        repository.connectWebSocket()
        val event = withTimeout(2_000) { eventDeferred.await() }

        assertTrue(event.sessionKey.startsWith("local-"))
        assertEquals(event.sessionKey, sessionDataSource.getSessionKey())
        assertEquals(GameConnectionState.CONNECTED, repository.connectionState.value)
        job.cancel()
    }

    @Test
    fun sendSubmitAnswer_emitsLocalSubmitResult() = runBlocking {
        val repository = createRepository()
        val startDeferred = CompletableDeferred<GameStartEvent>()
        val startJob = launch(start = CoroutineStart.UNDISPATCHED) {
            repository.gameEvents.filterIsInstance<GameStartEvent>().collect { event ->
                startDeferred.complete(event)
            }
        }
        repository.connectWebSocket()
        val start = withTimeout(2_000) { startDeferred.await() }
        val resultDeferred = CompletableDeferred<SubmitResultEvent>()
        val resultJob = launch(start = CoroutineStart.UNDISPATCHED) {
            repository.gameEvents.filterIsInstance<SubmitResultEvent>().collect { event ->
                resultDeferred.complete(event)
            }
        }

        repository.sendSubmitAnswer(
            sessionKey = start.sessionKey,
            quizId = 1,
            oAreaUserPositions = listOf(UserPosition(userId = 1, x = 0.2, y = 0.3)),
            xAreaUserPositions = listOf(UserPosition(userId = 2, x = 0.8, y = 0.3))
        )

        val result = withTimeout(2_000) { resultDeferred.await() }
        assertEquals("O", result.correctAnswer)
        assertEquals(listOf(1), result.correctUserPositions.map { it.userId })
        startJob.cancel()
        resultJob.cancel()
    }

    @Test
    fun finishOXGame_savesLocalHistoryWithWrongAnswerNotes() = runBlocking {
        val historyDao = FakeOXGameHistoryDao()
        val repository = createRepository(historyDao = historyDao)
        val startDeferred = CompletableDeferred<GameStartEvent>()
        val startJob = launch(start = CoroutineStart.UNDISPATCHED) {
            repository.gameEvents.filterIsInstance<GameStartEvent>().collect { event ->
                startDeferred.complete(event)
            }
        }
        repository.connectWebSocket()
        val start = withTimeout(2_000) { startDeferred.await() }
        val resultDeferred = CompletableDeferred<SubmitResultEvent>()
        val resultJob = launch(start = CoroutineStart.UNDISPATCHED) {
            repository.gameEvents.filterIsInstance<SubmitResultEvent>().collect { event ->
                resultDeferred.complete(event)
            }
        }
        repository.sendSubmitAnswer(
            sessionKey = start.sessionKey,
            quizId = 1,
            oAreaUserPositions = listOf(UserPosition(userId = 1, x = 0.2, y = 0.3)),
            xAreaUserPositions = listOf(UserPosition(userId = 2, x = 0.8, y = 0.3))
        )
        withTimeout(2_000) { resultDeferred.await() }
        repository.finishOXGame(start.sessionKey)

        assertEquals(1, historyDao.histories.size)
        assertEquals(1, historyDao.notes.size)
        assertEquals("하늘은 파란색이다", historyDao.notes.first().question)
        assertEquals("O", historyDao.notes.first().answer)
        startJob.cancel()
        resultJob.cancel()
    }

    private fun createRepository(
        sessionDataSource: SessionDataSource = SessionDataSource(InMemorySharedPreferences()),
        historyDao: OXGameHistoryDao = FakeOXGameHistoryDao()
    ): OXGameRepositoryImpl {
        return OXGameRepositoryImpl(
            sessionDataSource = sessionDataSource,
            localGameEngine = LocalOXGameEngine(FakeOXQuizRepository()),
            historyDao = historyDao
        )
    }

    private class FakeOXQuizRepository : OXQuizRepository {
        override suspend fun getQuizzes(): List<Quiz> = listOf(
            Quiz(
                id = 1,
                question = "하늘은 파란색이다",
                answer = "O",
                description = "맑은 날 하늘은 파랗게 보입니다."
            )
        )
    }

    private class FakeOXGameHistoryDao : OXGameHistoryDao {
        val histories = mutableListOf<OXGameHistoryEntity>()
        val notes = mutableListOf<OXWrongAnswerNoteEntity>()
        private var nextHistoryId = 1L

        override suspend fun insertHistory(history: OXGameHistoryEntity): Long {
            val id = nextHistoryId++
            histories += history.copy(id = id)
            return id
        }

        override suspend fun insertWrongAnswerNotes(notes: List<OXWrongAnswerNoteEntity>) {
            this.notes += notes
        }

        override suspend fun countHistories(): Int = histories.size

        override suspend fun getHistories(limit: Int, offset: Int): List<OXGameHistoryWithNotes> {
            return histories.drop(offset).take(limit).map { history ->
                OXGameHistoryWithNotes(
                    history = history,
                    notes = notes.filter { it.historyId == history.id }
                )
            }
        }

        override suspend fun getHistory(historyId: Long): OXGameHistoryWithNotes? {
            val history = histories.firstOrNull { it.id == historyId } ?: return null
            return OXGameHistoryWithNotes(
                history = history,
                notes = notes.filter { it.historyId == historyId }
            )
        }
    }

    private class InMemorySharedPreferences : SharedPreferences {
        private val values = linkedMapOf<String, Any?>()

        override fun getAll(): MutableMap<String, *> = values.toMutableMap()

        override fun getString(key: String, defValue: String?): String? =
            values[key] as? String ?: defValue

        @Suppress("UNCHECKED_CAST")
        override fun getStringSet(key: String, defValues: MutableSet<String>?): MutableSet<String>? =
            (values[key] as? Set<String>)?.toMutableSet() ?: defValues

        override fun getInt(key: String, defValue: Int): Int = values[key] as? Int ?: defValue

        override fun getLong(key: String, defValue: Long): Long = values[key] as? Long ?: defValue

        override fun getFloat(key: String, defValue: Float): Float = values[key] as? Float ?: defValue

        override fun getBoolean(key: String, defValue: Boolean): Boolean =
            values[key] as? Boolean ?: defValue

        override fun contains(key: String): Boolean = values.containsKey(key)

        override fun edit(): SharedPreferences.Editor = Editor()

        override fun registerOnSharedPreferenceChangeListener(
            listener: SharedPreferences.OnSharedPreferenceChangeListener?
        ) = Unit

        override fun unregisterOnSharedPreferenceChangeListener(
            listener: SharedPreferences.OnSharedPreferenceChangeListener?
        ) = Unit

        private inner class Editor : SharedPreferences.Editor {
            private val pending = linkedMapOf<String, Any?>()
            private val removals = mutableSetOf<String>()
            private var clearRequested = false

            override fun putString(key: String, value: String?): SharedPreferences.Editor =
                apply { pending[key] = value }

            override fun putStringSet(
                key: String,
                values: MutableSet<String>?
            ): SharedPreferences.Editor = apply {
                pending[key] = values?.toSet()
            }

            override fun putInt(key: String, value: Int): SharedPreferences.Editor =
                apply { pending[key] = value }

            override fun putLong(key: String, value: Long): SharedPreferences.Editor =
                apply { pending[key] = value }

            override fun putFloat(key: String, value: Float): SharedPreferences.Editor =
                apply { pending[key] = value }

            override fun putBoolean(key: String, value: Boolean): SharedPreferences.Editor =
                apply { pending[key] = value }

            override fun remove(key: String): SharedPreferences.Editor =
                apply { removals += key }

            override fun clear(): SharedPreferences.Editor =
                apply { clearRequested = true }

            override fun commit(): Boolean {
                applyChanges()
                return true
            }

            override fun apply() {
                applyChanges()
            }

            private fun applyChanges() {
                if (clearRequested) values.clear()
                removals.forEach(values::remove)
                pending.forEach { (key, value) ->
                    if (value == null) values.remove(key) else values[key] = value
                }
            }
        }
    }
}
