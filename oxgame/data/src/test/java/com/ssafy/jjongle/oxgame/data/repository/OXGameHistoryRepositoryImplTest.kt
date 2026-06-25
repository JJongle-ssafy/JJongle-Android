package com.ssafy.jjongle.oxgame.data.repository

import com.ssafy.jjongle.oxgame.data.local.OXGameHistoryDao
import com.ssafy.jjongle.oxgame.data.local.OXGameHistoryEntity
import com.ssafy.jjongle.oxgame.data.local.OXGameHistoryWithNotes
import com.ssafy.jjongle.oxgame.data.local.OXWrongAnswerNoteEntity
import com.ssafy.jjongle.oxgame.entity.OX
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Test

/**
 * OXGameHistory의 아키텍처 또는 동작 계약을 검증하는 테스트입니다.
 *
 * - 계층: test
 * - 책임: 회귀를 막는 대표 시나리오와 모듈 경계 조건을 실행합니다.
 */
class OXGameHistoryRepositoryImplTest {

    @Test
    fun getHistories_returnsRoomBackedPage() = runTest {
        val dao = FakeOXGameHistoryDao(
            histories = (1L..4L).map { id ->
                OXGameHistoryEntity(
                    id = id,
                    playedAtEpochMillis = id * 1_000L,
                    totalQuizzes = 5,
                    completedQuizzes = 5,
                    totalCorrectAnswers = id.toInt()
                )
            }.toMutableList()
        )
        val repository = OXGameHistoryRepositoryImpl(dao)

        val page = repository.getHistories(page = 0)

        assertEquals(2, page.totalPages)
        assertEquals(listOf(1L, 2L, 3L), page.content.map { it.id })
    }

    @Test
    fun getHistoryDetail_mapsWrongAnswerNotesFromRoom() = runTest {
        val dao = FakeOXGameHistoryDao(
            histories = mutableListOf(
                OXGameHistoryEntity(
                    id = 1L,
                    playedAtEpochMillis = 1_000L,
                    totalQuizzes = 5,
                    completedQuizzes = 5,
                    totalCorrectAnswers = 3
                )
            ),
            notes = mutableListOf(
                OXWrongAnswerNoteEntity(
                    id = 1L,
                    historyId = 1L,
                    question = "펭귄은 하늘을 난다",
                    answer = "X"
                )
            )
        )
        val repository = OXGameHistoryRepositoryImpl(dao)

        val detail = repository.getHistoryDetail(1L)

        assertEquals(1, detail.size)
        assertEquals("펭귄은 하늘을 난다", detail.first().question)
        assertEquals(OX.X, detail.first().answer)
    }

    @Test
    fun getHistoryDetail_usesFallbacksForMalformedWrongAnswerNotesFromRoom() = runTest {
        val dao = FakeOXGameHistoryDao(
            histories = mutableListOf(
                OXGameHistoryEntity(
                    id = 1L,
                    playedAtEpochMillis = 1_000L,
                    totalQuizzes = 5,
                    completedQuizzes = 5,
                    totalCorrectAnswers = 3
                )
            ),
            notes = mutableListOf(
                OXWrongAnswerNoteEntity(
                    id = 1L,
                    historyId = 1L,
                    question = " ",
                    answer = "unknown"
                )
            )
        )
        val repository = OXGameHistoryRepositoryImpl(dao)

        val detail = repository.getHistoryDetail(1L)

        assertEquals("[MISSING_LOCAL_FIELD:ox.question]", detail.first().question)
        assertEquals(OX.X, detail.first().answer)
    }

    private class FakeOXGameHistoryDao(
        private val histories: MutableList<OXGameHistoryEntity> = mutableListOf(),
        private val notes: MutableList<OXWrongAnswerNoteEntity> = mutableListOf()
    ) : OXGameHistoryDao {
        override suspend fun insertHistory(history: OXGameHistoryEntity): Long {
            histories += history
            return history.id
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
}
