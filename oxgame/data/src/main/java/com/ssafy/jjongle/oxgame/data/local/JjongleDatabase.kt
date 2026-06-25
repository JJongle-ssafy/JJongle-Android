package com.ssafy.jjongle.oxgame.data.local

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(
    entities = [
        OXGameHistoryEntity::class,
        OXWrongAnswerNoteEntity::class
    ],
    version = 1,
    exportSchema = false
)

/**
 * JjongleDatabase Room 데이터베이스 구성을 정의합니다.
 *
 * - 계층: oxgame/data
 * - 책임: 로컬 entity와 DAO 구성을 data 계층에 묶어 둡니다.
 */
abstract class JjongleDatabase : RoomDatabase() {
    abstract fun oxGameHistoryDao(): OXGameHistoryDao
}
