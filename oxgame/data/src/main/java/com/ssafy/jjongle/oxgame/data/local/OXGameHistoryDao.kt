package com.ssafy.jjongle.oxgame.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Transaction

/**
 * OXGameHistoryDao Room 데이터 접근 계약을 정의합니다.
 *
 * - 계층: oxgame/data
 * - 책임: 로컬 저장소 쿼리와 변경 작업을 타입 안전하게 제공합니다.
 */
@Dao
interface OXGameHistoryDao {
    @Insert
    suspend fun insertHistory(history: OXGameHistoryEntity): Long

    @Insert
    suspend fun insertWrongAnswerNotes(notes: List<OXWrongAnswerNoteEntity>)

    @Query("SELECT COUNT(*) FROM ox_game_histories")
    suspend fun countHistories(): Int

    @Transaction
    @Query(
        """
        SELECT * FROM ox_game_histories
        ORDER BY playedAtEpochMillis DESC
        LIMIT :limit OFFSET :offset
        """
    )
    suspend fun getHistories(limit: Int, offset: Int): List<OXGameHistoryWithNotes>

    @Transaction
    @Query("SELECT * FROM ox_game_histories WHERE id = :historyId")
    suspend fun getHistory(historyId: Long): OXGameHistoryWithNotes?
}
