package com.karalog.app.data.local.dao

import androidx.room.*
import com.karalog.app.data.local.entity.SingingRecordEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface SingingRecordDao {
    @Query("SELECT * FROM singing_records WHERE isTrashed = 0 ORDER BY date DESC")
    fun getAllActiveRecords(): Flow<List<SingingRecordEntity>>

    @Query("SELECT * FROM singing_records WHERE songId = :songId AND isTrashed = 0 ORDER BY date DESC")
    fun getRecordsForSong(songId: Long): Flow<List<SingingRecordEntity>>

    @Query("SELECT * FROM singing_records WHERE isTrashed = 1 ORDER BY date DESC")
    fun getTrashRecords(): Flow<List<SingingRecordEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertRecord(record: SingingRecordEntity): Long

    @Update
    suspend fun updateRecord(record: SingingRecordEntity)

    @Query("UPDATE singing_records SET isTrashed = 1 WHERE id = :id")
    suspend fun moveToTrash(id: Long)

    @Query("UPDATE singing_records SET isTrashed = 0 WHERE id = :id")
    suspend fun restoreFromTrash(id: Long)

    @Query("DELETE FROM singing_records WHERE id = :id")
    suspend fun deletePermanently(id: Long)

    @Query("DELETE FROM singing_records WHERE isTrashed = 1")
    suspend fun emptyTrash()

    @Query("DELETE FROM singing_records")
    suspend fun deleteAll()
}