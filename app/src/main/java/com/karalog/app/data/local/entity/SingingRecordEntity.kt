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
    val pitchAccuracy: Double? = null,
    val expressionScore: Double? = null,
    val keyChange: Int = 0,
    val machineType: String = "DAM (精密採点Ai)",
    val memo: String = "",
    val techniqueNotes: String = "",
    val isTrashed: Boolean = false
)
