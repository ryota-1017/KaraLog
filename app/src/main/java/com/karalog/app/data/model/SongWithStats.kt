package com.karalog.app.data.model

import com.karalog.app.data.local.entity.SongEntity
import com.karalog.app.data.local.entity.SingingRecordEntity

data class SongWithStats(
    val song: SongEntity,
    val maxScore: Double? = null,
    val avgScore: Double? = null,
    val singCount: Int = 0,
    val lastSungDate: Long? = null,
    val lastScore: Double? = null,
    val previousScore: Double? = null
) {
    val id: Long get() = song.id
    val title: String get() = song.title
    val artist: String get() = song.artist
    val duration: String get() = song.duration
    val requestNumber: String get() = song.requestNumber
    val isDamAvailable: Boolean get() = song.isDamAvailable
    val isJoysoundAvailable: Boolean get() = song.isJoysoundAvailable
    val defaultKey: Int get() = song.defaultKey
    val isKept: Boolean get() = song.isKept
    val isTrashed: Boolean get() = song.isTrashed
}

data class SingingRecordWithSong(
    val record: SingingRecordEntity,
    val songTitle: String,
    val songArtist: String
)