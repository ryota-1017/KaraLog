package com.karalog.app

import android.content.SharedPreferences
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.karalog.app.data.local.entity.SingingRecordEntity
import com.karalog.app.data.local.entity.SongEntity
import com.karalog.app.data.model.SingingRecordWithSong
import com.karalog.app.data.model.SongWithStats
import com.karalog.app.data.repository.KaraLogRepository
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.util.Calendar

class MainViewModel(
    private val repository: KaraLogRepository,
    private val prefs: SharedPreferences? = null
) : ViewModel() {

    companion object {
        const val PREFS_NAME = "karalog_session_prefs"
        const val KEY_IS_SESSION_ACTIVE = "key_is_session_active"
        const val KEY_SESSION_START_TIME = "key_session_start_time"
        const val KEY_SELECTED_MACHINE = "key_selected_machine"
        const val KEY_AUTO_RESET_3AM = "key_auto_reset_3am"
        const val KEY_THEME_MODE = "key_theme_mode"
    }

    val songsWithStats = repository.songsWithStatsFlow.stateIn(
        viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList()
    )

    val keptSongs = repository.keptSongsFlow.stateIn(
        viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList()
    )

    val recordsWithSong = repository.recordsWithSongFlow.stateIn(
        viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList()
    )

    private val _isSessionActive = MutableStateFlow(
        prefs?.getBoolean(KEY_IS_SESSION_ACTIVE, false) ?: false
    )
    val isSessionActive: StateFlow<Boolean> = _isSessionActive.asStateFlow()

    private val _selectedMachine = MutableStateFlow(
        prefs?.getString(KEY_SELECTED_MACHINE, "DAM") ?: "DAM"
    )
    val selectedMachine: StateFlow<String> = _selectedMachine.asStateFlow()

    private val _autoResetAt3Am = MutableStateFlow(
        prefs?.getBoolean(KEY_AUTO_RESET_3AM, true) ?: true
    )
    val autoResetAt3Am: StateFlow<Boolean> = _autoResetAt3Am.asStateFlow()

    private val _sessionStartTime = MutableStateFlow(
        prefs?.getLong(KEY_SESSION_START_TIME, 0L)?.takeIf { it > 0 } ?: System.currentTimeMillis()
    )
    val sessionStartTime: StateFlow<Long> = _sessionStartTime.asStateFlow()

    private val _sessionRecords = MutableStateFlow<List<Pair<String, SingingRecordEntity>>>(emptyList())
    val sessionRecords: StateFlow<List<Pair<String, SingingRecordEntity>>> = _sessionRecords.asStateFlow()

    private val _themeMode = MutableStateFlow(
        prefs?.getString(KEY_THEME_MODE, "SYSTEM") ?: "SYSTEM"
    )
    val themeMode: StateFlow<String> = _themeMode.asStateFlow()

    init {
        // 深夜3時自動リセットのチェック
        check3AmAutoReset()

        // カラオケ人気定番曲の自動シード
        viewModelScope.launch {
            repository.seedPopularSongsIfNeeded(force = false)
        }
    }

    fun setThemeMode(mode: String) {
        _themeMode.value = mode
        prefs?.edit()?.putString(KEY_THEME_MODE, mode)?.apply()
    }

    fun setAutoResetAt3Am(enabled: Boolean) {
        _autoResetAt3Am.value = enabled
        prefs?.edit()?.putBoolean(KEY_AUTO_RESET_3AM, enabled)?.apply()
    }

    fun setSelectedMachine(machine: String) {
        _selectedMachine.value = machine
        prefs?.edit()?.putString(KEY_SELECTED_MACHINE, machine)?.apply()
    }

    fun startSession(machine: String) {
        val now = System.currentTimeMillis()
        _selectedMachine.value = machine
        _sessionStartTime.value = now
        _isSessionActive.value = true
        _sessionRecords.value = emptyList()
        prefs?.edit()
            ?.putBoolean(KEY_IS_SESSION_ACTIVE, true)
            ?.putLong(KEY_SESSION_START_TIME, now)
            ?.putString(KEY_SELECTED_MACHINE, machine)
            ?.apply()
    }

    fun finishSession() {
        _isSessionActive.value = false
        _sessionRecords.value = emptyList()
        prefs?.edit()
            ?.putBoolean(KEY_IS_SESSION_ACTIVE, false)
            ?.apply()
    }

    /**
     * 深夜3時（3:00 AM）を跨いだ場合にセッションを自動リセットする
     * @return リセットが実行された場合 true
     */
    fun check3AmAutoReset(): Boolean {
        if (!_autoResetAt3Am.value || !_isSessionActive.value) return false
        val now = System.currentTimeMillis()
        val startTime = _sessionStartTime.value
        if (isPast3AmBoundary(startTime, now)) {
            finishSession()
            return true
        }
        return false
    }

    private fun isPast3AmBoundary(startTime: Long, currentTime: Long): Boolean {
        if (startTime <= 0L || currentTime <= startTime) return false
        // 24時間以上経過していればリセット対象
        if (currentTime - startTime >= 24 * 60 * 60 * 1000L) return true

        val calStart = Calendar.getInstance().apply { timeInMillis = startTime }
        val calNow = Calendar.getInstance().apply { timeInMillis = currentTime }

        // 3時間引いて「仮想カラオケ日」で比較
        calStart.add(Calendar.HOUR_OF_DAY, -3)
        calNow.add(Calendar.HOUR_OF_DAY, -3)

        val startYear = calStart.get(Calendar.YEAR)
        val startDay = calStart.get(Calendar.DAY_OF_YEAR)
        val nowYear = calNow.get(Calendar.YEAR)
        val nowDay = calNow.get(Calendar.DAY_OF_YEAR)

        return startYear != nowYear || startDay != nowDay
    }

    fun seedPopularRankingSongs(onDone: (Int) -> Unit = {}) {
        viewModelScope.launch {
            val count = repository.seedPopularSongsIfNeeded(force = true)
            onDone(count)
        }
    }

    fun recordScore(
        song: SongEntity,
        score: Double,
        keyChange: Int,
        machineType: String,
        memo: String = "",
        pitchAccuracy: Double? = null,
        expressionScore: Double? = null,
        techniqueNotes: String = ""
    ) {
        viewModelScope.launch {
            val record = SingingRecordEntity(
                songId = song.id,
                score = score,
                pitchAccuracy = pitchAccuracy,
                expressionScore = expressionScore,
                keyChange = keyChange,
                machineType = machineType,
                memo = memo,
                techniqueNotes = techniqueNotes
            )
            repository.insertRecord(record)
            _sessionRecords.value = _sessionRecords.value + (song.title to record)
        }
    }

    fun quickAddAndGetSong(
        title: String,
        artist: String = "",
        requestNumber: String = "",
        defaultKey: Int = 0,
        isDam: Boolean = true,
        isJoy: Boolean = true,
        stageTag: String = "",
        spotifyUrl: String = "",
        youtubeMusicUrl: String = "",
        onSongReady: (SongEntity) -> Unit = {}
    ) {
        viewModelScope.launch {
            val trimmedTitle = title.trim()
            val trimmedArtist = artist.trim()
            val existing = repository.findOrCreateSong(
                title = trimmedTitle,
                artist = trimmedArtist,
                spotifyUrl = spotifyUrl,
                youtubeMusicUrl = youtubeMusicUrl
            )
            val updated = existing.copy(
                requestNumber = if (requestNumber.isNotEmpty()) requestNumber else existing.requestNumber,
                defaultKey = if (defaultKey != 0) defaultKey else existing.defaultKey,
                isDamAvailable = isDam,
                isJoysoundAvailable = isJoy,
                stageTag = if (stageTag.isNotEmpty()) stageTag else existing.stageTag,
                isKept = true
            )
            repository.updateSong(updated)
            onSongReady(updated)
        }
    }

    fun saveSong(song: SongEntity) {
        viewModelScope.launch {
            repository.saveOrMergeSong(song)
        }
    }

    fun deleteSong(songId: Long) {
        viewModelScope.launch {
            repository.deleteSong(songId)
        }
    }

    fun deleteRecord(recordId: Long) {
        viewModelScope.launch {
            repository.deleteRecord(recordId)
            _sessionRecords.value = _sessionRecords.value.filter { it.second.id != recordId }
        }
    }

    fun updateSongKey(songId: Long, key: Int) {
        viewModelScope.launch {
            repository.updateDefaultKey(songId, key)
        }
    }

    fun toggleKept(songId: Long, currentKept: Boolean) {
        viewModelScope.launch {
            repository.toggleKeptStatus(songId, currentKept)
        }
    }

    fun resetSession() {
        val now = System.currentTimeMillis()
        _sessionStartTime.value = now
        _sessionRecords.value = emptyList()
        prefs?.edit()
            ?.putLong(KEY_SESSION_START_TIME, now)
            ?.apply()
    }

    fun resetAllData() {
        viewModelScope.launch {
            repository.deleteAllData()
            _sessionRecords.value = emptyList()
            finishSession()
        }
    }

    suspend fun exportJson(): String = repository.exportJson()
    suspend fun importJson(json: String): Boolean = repository.importJson(json)
}

class MainViewModelFactory(
    private val repository: KaraLogRepository,
    private val prefs: SharedPreferences? = null
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(MainViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return MainViewModel(repository, prefs) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
