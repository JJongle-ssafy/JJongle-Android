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
 * OX 게임 기록과 오답 노트를 저장하는 Room 데이터베이스입니다.
 *
 * 게임 플레이 결과를 로컬에 남기고 히스토리/오답 노트 화면에서 다시 조회할 수 있게 합니다.
 */
abstract class JjongleDatabase : RoomDatabase() {
    abstract fun oxGameHistoryDao(): OXGameHistoryDao
}
