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
    version = 2,
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
                                SongEntity(title = "\u6b8b\u9177\u306a\u5929\u4f7f\u306e\u30c6\u30fc\u30bc", artist = "\u9ad8\u6a4b\u6d0b\u5b50", duration = "4:05", requestNumber = "2891-01", isDamAvailable = true, isJoysoundAvailable = true, defaultKey = 0),
                                SongEntity(title = "\u30de\u30ea\u30fc\u30b4\u30fc\u30eb\u30c9", artist = "\u3042\u3044\u307f\u3087\u3093", duration = "5:06", requestNumber = "4321-89", isDamAvailable = true, isJoysoundAvailable = true, defaultKey = 0),
                                SongEntity(title = "\u30c9\u30e9\u30a4\u30d5\u30e9\u30ef\u30fc", artist = "\u512a\u91cc", duration = "4:46", requestNumber = "6541-20", isDamAvailable = true, isJoysoundAvailable = true, defaultKey = 0),
                                SongEntity(title = "\u602a\u7363\u306e\u82b1\u5504", artist = "Vaundy", duration = "3:44", requestNumber = "5092-11", isDamAvailable = true, isJoysoundAvailable = true, defaultKey = 0),
                                SongEntity(title = "\u30a2\u30a4\u30c9\u30eb", artist = "YOASOBI", duration = "3:33", requestNumber = "7820-45", isDamAvailable = true, isJoysoundAvailable = true, defaultKey = 0),
                                SongEntity(title = "\u5929\u4f53\u89b3\u6e2c", artist = "BUMP OF CHICKEN", duration = "4:45", requestNumber = "1203-44", isDamAvailable = true, isJoysoundAvailable = true, defaultKey = 0),
                                SongEntity(title = "\u4e38\u30ce\u5185\u30b5\u30c7\u30a3\u30b9\u30c6\u30a3\u30c3\u30af", artist = "\u690e\u540d\u6797\u6a8e", duration = "3:55", requestNumber = "3194-02", isDamAvailable = true, isJoysoundAvailable = true, defaultKey = 0),
                                SongEntity(title = "Subtitle", artist = "Official\u9a鬮ｭ\u7537dism", duration = "5:05", requestNumber = "8902-33", isDamAvailable = true, isJoysoundAvailable = true, defaultKey = 0),
                                SongEntity(title = "\u6c34\u5e73\u7dda", artist = "back number", duration = "4:47", requestNumber = "6102-18", isDamAvailable = true, isJoysoundAvailable = true, defaultKey = 0)
                            )
                            INSTANCE?.songDao()?.insertSongs(initialSongs)
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