package com.karalog.app

import android.app.Application
import com.karalog.app.data.local.KaraLogDatabase
import com.karalog.app.data.repository.KaraLogRepository

class KaraLogApplication : Application() {
    val database by lazy { KaraLogDatabase.getDatabase(this) }
    val repository by lazy { KaraLogRepository(database.songDao(), database.singingRecordDao()) }
}