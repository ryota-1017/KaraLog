package com.karalog.app.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "songs")
data class SongEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val title: String,
    val artist: String = "",
    val duration: String = "",
    val requestNumber: String = "",
    val isDamAvailable: Boolean = true,
    val isJoysoundAvailable: Boolean = true,
    val defaultKey: Int = 0,
    val isKept: Boolean = false,
    val genre: String = "",
    val memo: String = "",
    val isTrashed: Boolean = false,
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis()
)