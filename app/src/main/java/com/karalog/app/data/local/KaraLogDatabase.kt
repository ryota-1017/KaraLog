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
    version = 7,
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
                            // Seed popular ranking songs
                            INSTANCE?.songDao()?.insertSongs(PopularSongsSeed.POPULAR_RANKING_SONGS)

                            // Initial sample score record
                            val rankingRecord = SingingRecordEntity(
                                songId = 1,
                                date = 1719097200000L,
                                score = 98.1,
                                pitchAccuracy = 92.0,
                                keyChange = 0,
                                machineType = "精密採点Ai",
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
