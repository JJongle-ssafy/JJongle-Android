package com.ssafy.jjongle.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import com.ssafy.jjongle.data.local.oxgame.OXGameHistoryDao
import com.ssafy.jjongle.data.local.oxgame.OXGameHistoryEntity
import com.ssafy.jjongle.data.local.oxgame.OXWrongAnswerNoteEntity

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
