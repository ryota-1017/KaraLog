package com.karalog.app

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

class MainViewModel(private val repository: KaraLogRepository) : ViewModel() {

    val songsWithStats = repository.songsWithStatsFlow.stateIn(
        viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList()
    )

    val keptSongs = repository.keptSongsFlow.stateIn(
        viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList()
    )

    val recordsWithSong = repository.recordsWithSongFlow.stateIn(
        viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList()
    )

    private val _sessionStartTime = MutableStateFlow(System.currentTimeMillis())
    val sessionStartTime: StateFlow<Long> = _sessionStartTime.asStateFlow()

    private val _sessionRecords = MutableStateFlow<List<Pair<String, SingingRecordEntity>>>(emptyList())
    val sessionRecords: StateFlow<List<Pair<String, SingingRecordEntity>>> = _sessionRecords.asStateFlow()

    private val _themeMode = MutableStateFlow("SYSTEM") // "SYSTEM", "DARK", "LIGHT"
    val themeMode: StateFlow<String> = _themeMode.asStateFlow()

    fun setThemeMode(mode: String) {
        _themeMode.value = mode
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
        onSongReady: (SongEntity) -> Unit = {}
    ) {
        viewModelScope.launch {
            val trimmedTitle = title.trim()
            val trimmedArtist = artist.trim()
            val existing = repository.findOrCreateSong(trimmedTitle, trimmedArtist)
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
            if (song.id == 0L) {
                repository.insertSong(song)
            } else {
                repository.updateSong(song)
            }
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
        _sessionStartTime.value = System.currentTimeMillis()
        _sessionRecords.value = emptyList()
    }

    fun resetAllData() {
        viewModelScope.launch {
            repository.deleteAllData()
            _sessionRecords.value = emptyList()
        }
    }

    suspend fun exportJson(): String = repository.exportJson()
    suspend fun importJson(json: String): Boolean = repository.importJson(json)
}

class MainViewModelFactory(private val repository: KaraLogRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(MainViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return MainViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
