package com.karalog.app.data.local.dao

import androidx.room.*
import com.karalog.app.data.local.entity.SongEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface SongDao {
    @Query("SELECT * FROM songs WHERE isTrashed = 0 ORDER BY updatedAt DESC")
    fun getAllActiveSongs(): Flow<List<SongEntity>>

    @Query("SELECT * FROM songs WHERE isTrashed = 0 AND isKept = 1 ORDER BY updatedAt DESC")
    fun getKeptSongs(): Flow<List<SongEntity>>

    @Query("SELECT * FROM songs WHERE id = :id LIMIT 1")
    suspend fun getSongById(id: Long): SongEntity?

    @Query("SELECT * FROM songs WHERE title = :title AND artist = :artist LIMIT 1")
    suspend fun getSongByTitleAndArtist(title: String, artist: String): SongEntity?

    @Query("SELECT * FROM songs WHERE isTrashed = 1 ORDER BY updatedAt DESC")
    fun getTrashSongs(): Flow<List<SongEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSong(song: SongEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSongs(songs: List<SongEntity>)

    @Update
    suspend fun updateSong(song: SongEntity)

    @Query("UPDATE songs SET isKept = :isKept WHERE id = :songId")
    suspend fun updateKeptStatus(songId: Long, isKept: Boolean)

    @Query("UPDATE songs SET defaultKey = :key WHERE id = :songId")
    suspend fun updateDefaultKey(songId: Long, key: Int)

    @Query("UPDATE songs SET isTrashed = 1 WHERE id = :id")
    suspend fun moveToTrash(id: Long)

    @Query("UPDATE songs SET isTrashed = 0 WHERE id = :id")
    suspend fun restoreFromTrash(id: Long)

    @Query("DELETE FROM songs WHERE id = :id")
    suspend fun deletePermanently(id: Long)

    @Query("DELETE FROM songs WHERE isTrashed = 1")
    suspend fun emptyTrash()

    @Query("DELETE FROM songs")
    suspend fun deleteAll()
}