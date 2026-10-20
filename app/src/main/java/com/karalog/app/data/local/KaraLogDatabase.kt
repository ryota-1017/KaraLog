package com.karalog.app.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import com.karalog.app.data.local.dao.SingingRecordDao
import com.karalog.app.data.local.dao.SongDao
import com.karalog.app.data.local.entity.SingingRecordEntity
import com.karalog.app.data.local.entity.SongEntity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Database(
    entities = [SongEntity::class, SingingRecordEntity::class],
    version = 4,
    exportSchema = false
)
abstract class KaraLogDatabase : RoomDatabase() {
    abstract fun songDao(): SongDao
    abstract fun singingRecordDao(): SingingRecordDao

    companion object {
        @Volatile
        private var INSTANCE: KaraLogDatabase? = null

        fun getDatabase(context: Context): KaraLogDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    KaraLogDatabase::class.java,
                    "karalog_m3_database"
                )
                .fallbackToDestructiveMigration()
                .addCallback(object : Callback() {
                    override fun onCreate(db: SupportSQLiteDatabase) {
                        super.onCreate(db)
                        CoroutineScope(Dispatchers.IO).launch {
                            val initialSongs = listOf(
                                SongEntity(title = "How Many Boogie", artist = "SKRYU, WAZ", duration = "3:10", requestNumber = "4901-22", isDamAvailable = true, isJoysoundAvailable = true, defaultKey = 0),
                                SongEntity(title = "みどり", artist = "CHEHON", duration = "4:15", requestNumber = "1284-90", isDamAvailable = true, isJoysoundAvailable = true, defaultKey = 0),
                                SongEntity(title = "脳内麻薬", artist = "RAY", duration = "3:42", requestNumber = "3029-14", isDamAvailable = true, isJoysoundAvailable = true, defaultKey = 0),
                                SongEntity(title = "聞いてま。", artist = "初音ミク", duration = "3:30", requestNumber = "8821-05", isDamAvailable = true, isJoysoundAvailable = true, defaultKey = 0),
                                SongEntity(title = "残酷な天使のテーゼ", artist = "高橋洋子", duration = "4:05", requestNumber = "2891-01", isDamAvailable = true, isJoysoundAvailable = true, defaultKey = 0)
                            )
                            INSTANCE?.songDao()?.insertSongs(initialSongs)

                            // Initial ranking record from sketch
                            val rankingRecord = SingingRecordEntity(
                                songId = 4,
                                date = 1719097200000L, // 2024.6.23
                                score = 98.1,
                                keyChange = 0,
                                machineType = "精密採点AI",
                                memo = ""
                            )
                            INSTANCE?.singingRecordDao()?.insertRecord(rankingRecord)
                        }
                    }
                })
                .build()
                INSTANCE = instance
                instance
            }
        }
    }
}
