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
abstract class JjongleDatabase : RoomDatabase() {
    abstract fun oxGameHistoryDao(): OXGameHistoryDao
}
