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

    private val _sessionRecords = MutableStateFlow<List<Pair<String, SingingRecordEntity>>>(emptyList())
    val sessionRecords: StateFlow<List<Pair<String, SingingRecordEntity>>> = _sessionRecords.asStateFlow()

    fun recordScore(
        song: SongEntity,
        score: Double,
        keyChange: Int,
        machineType: String,
        memo: String
    ) {
        viewModelScope.launch {
            val record = SingingRecordEntity(
                songId = song.id,
                score = score,
                keyChange = keyChange,
                machineType = machineType,
                memo = memo
            )
            repository.insertRecord(record)
            _sessionRecords.value = _sessionRecords.value + (song.title to record)
        }
    }

    fun quickAddAndGetSong(title: String, onSongReady: (SongEntity) -> Unit) {
        viewModelScope.launch {
            val song = repository.findOrCreateSong(title, "")
            onSongReady(song)
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
        _sessionRecords.value = emptyList()
    }

    fun resetAllData() {
        viewModelScope.launch {
            repository.deleteAllData()
            _sessionRecords.value = emptyList()
        }
    }
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