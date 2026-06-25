package com.ssafy.jjongle.oxgame.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Transaction

/**
 * OX 게임 기록과 오답 노트 테이블을 조회하고 저장하는 Room DAO입니다.
 *
 * Repository가 SQL 쿼리 세부사항을 알지 않고 히스토리 목록과 상세 데이터를 가져오도록 합니다.
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
