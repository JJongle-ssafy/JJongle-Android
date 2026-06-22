package com.ssafy.jjongle.data.local.oxgame

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Transaction

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
