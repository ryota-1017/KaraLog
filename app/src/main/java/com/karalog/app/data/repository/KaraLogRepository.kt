package com.karalog.app.data.repository

import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.karalog.app.data.local.dao.SingingRecordDao
import com.karalog.app.data.local.dao.SongDao
import com.karalog.app.data.local.entity.SingingRecordEntity
import com.karalog.app.data.local.entity.SongEntity
import com.karalog.app.data.model.SingingRecordWithSong
import com.karalog.app.data.model.SongWithStats
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.first

class KaraLogRepository(
    private val songDao: SongDao,
    private val recordDao: SingingRecordDao
) {
    val songsFlow: Flow<List<SongEntity>> = songDao.getAllActiveSongs()
    val keptSongsFlow: Flow<List<SongEntity>> = songDao.getKeptSongs()
    val recordsFlow: Flow<List<SingingRecordEntity>> = recordDao.getAllActiveRecords()

    val songsWithStatsFlow: Flow<List<SongWithStats>> = combine(songsFlow, recordsFlow) { songs, records ->
        val recordMap = records.groupBy { it.songId }
        songs.map { song ->
            val songRecords = recordMap[song.id]?.sortedByDescending { it.date } ?: emptyList()
            val singCount = songRecords.size
            val maxScore = if (singCount > 0) songRecords.maxOf { it.score } else null
            val avgScore = if (singCount > 0) songRecords.map { it.score }.average() else null
            val lastSungDate = songRecords.firstOrNull()?.date
            val lastScore = songRecords.firstOrNull()?.score
            val previousScore = if (singCount > 1) songRecords[1].score else null

            SongWithStats(
                song = song,
                maxScore = maxScore,
                avgScore = avgScore,
                singCount = singCount,
                lastSungDate = lastSungDate,
                lastScore = lastScore,
                previousScore = previousScore
            )
        }
    }

    val recordsWithSongFlow: Flow<List<SingingRecordWithSong>> = combine(songsFlow, recordsFlow) { songs, records ->
        val songMap = songs.associateBy { it.id }
        records.mapNotNull { record ->
            val song = songMap[record.songId]
            if (song != null) {
                SingingRecordWithSong(
                    record = record,
                    songTitle = song.title,
                    songArtist = song.artist
                )
            } else null
        }
    }

    suspend fun insertSong(song: SongEntity): Long = songDao.insertSong(song)
    suspend fun updateSong(song: SongEntity) = songDao.updateSong(song)
    suspend fun toggleKeptStatus(songId: Long, currentKept: Boolean) = songDao.updateKeptStatus(songId, !currentKept)
    suspend fun updateDefaultKey(songId: Long, key: Int) = songDao.updateDefaultKey(songId, key)

    suspend fun findOrCreateSong(title: String, artist: String): SongEntity {
        val trimmedTitle = title.trim()
        val trimmedArtist = artist.trim()
        val existing = songDao.getSongByTitleAndArtist(trimmedTitle, trimmedArtist)
        if (existing != null) return existing

        val newSong = SongEntity(title = trimmedTitle, artist = trimmedArtist)
        val id = songDao.insertSong(newSong)
        return newSong.copy(id = id)
    }

    suspend fun insertRecord(record: SingingRecordEntity): Long {
        val id = recordDao.insertRecord(record)
        val song = songDao.getSongById(record.songId)
        if (song != null) {
            songDao.updateSong(song.copy(updatedAt = System.currentTimeMillis()))
        }
        return id
    }

    suspend fun deleteRecord(recordId: Long) = recordDao.deletePermanently(recordId)
    suspend fun deleteSong(songId: Long) = songDao.deletePermanently(songId)

    suspend fun deleteAllData() {
        songDao.deleteAll()
        recordDao.deleteAll()
    }

    suspend fun exportJson(): String {
        val songs = songsFlow.first()
        val records = recordsFlow.first()
        val map = mapOf(
            "version" to "1.2.2",
            "songs" to songs,
            "records" to records
        )
        return Gson().toJson(map)
    }

    suspend fun importJson(json: String): Boolean {
        return try {
            val type = object : TypeToken<Map<String, Any>>() {}.type
            val map: Map<String, Any> = Gson().fromJson(json, type)
            val songsJson = Gson().toJson(map["songs"])
            val recordsJson = Gson().toJson(map["records"])

            val songsType = object : TypeToken<List<SongEntity>>() {}.type
            val recordsType = object : TypeToken<List<SingingRecordEntity>>() {}.type

            val songs: List<SongEntity> = Gson().fromJson(songsJson, songsType)
            val records: List<SingingRecordEntity> = Gson().fromJson(recordsJson, recordsType)

            songDao.deleteAll()
            recordDao.deleteAll()
            songDao.insertSongs(songs)
            records.forEach { recordDao.insertRecord(it) }
            true
        } catch (e: Exception) {
            false
        }
    }
}