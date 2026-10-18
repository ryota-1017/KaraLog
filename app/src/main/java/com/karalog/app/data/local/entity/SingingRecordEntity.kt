package com.karalog.app.data.local.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "singing_records",
    foreignKeys = [
        ForeignKey(
            entity = SongEntity::class,
            parentColumns = ["id"],
            childColumns = ["songId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index("songId")]
)
data class SingingRecordEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val songId: Long,
    val date: Long = System.currentTimeMillis(),
    val score: Double,
    val keyChange: Int = 0,
    val machineType: String = "DAM (\u7cbe\u5bc6\u63a1\u70b9Ai)",
    val memo: String = "",
    val isTrashed: Boolean = false
)