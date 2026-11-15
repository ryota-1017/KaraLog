package com.karalog.app.ui.screens.search

import android.widget.Toast
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.BookmarkBorder
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.karalog.app.data.local.entity.SongEntity
import com.karalog.app.data.model.SongWithStats
import com.karalog.app.ui.components.ListenMusicSheet
import com.karalog.app.ui.theme.*
import com.karalog.app.util.HapticHelper
import com.karalog.app.util.IntentHelper

@Composable
fun SearchScreen(
    songsWithStats: List<SongWithStats>,
    onSongClick: (SongEntity) -> Unit,
    onKeepToggle: (SongEntity) -> Unit,
    onSingClick: (SongEntity) -> Unit,
    onAddOrEditSong: (SongEntity) -> Unit,
    onAddToQueue: (SongEntity) -> Unit = {},
    onDeleteSong: (Long) -> Unit = {},
    initialCategory: String = "ALL",
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    var searchQuery by remember { mutableStateOf("") }
    var selectedMachine by remember { mutableStateOf("ALL") } // ALL, DAM, JOY
    var selectedCategory by remember { mutableStateOf(initialCategory) } // ALL, KEPT, WARMUP, BEST, CLIMAX, PRACTICE
    var editingSong by remember { mutableStateOf<SongEntity?>(null) }
    var showEditDialog by remember { mutableStateOf(false) }
    var listeningSong by remember { mutableStateOf<SongEntity?>(null) }
    var songToDelete by remember { mutableStateOf<SongEntity?>(null) }

    val filteredSongs = remember(songsWithStats, searchQuery, selectedMachine, selectedCategory) {
        val q = searchQuery.trim().lowercase()
        songsWithStats.filter { item ->
            val song = item.song
            val matchesQuery = q.isEmpty() ||
                    song.title.lowercase().contains(q) ||
                    song.artist.lowercase().contains(q) ||
                    song.requestNumber.lowercase().contains(q)

            val matchesMachine = when (selectedMachine) {
                "DAM" -> song.isDamAvailable
                "JOY" -> song.isJoysoundAvailable
                else -> true
            }

            val matchesCategory = when (selectedCategory) {
                "KEPT" -> song.isKept
                "WARMUP" -> song.stageTag == "WARMUP"
                "BEST" -> song.stageTag == "BEST" || (item.maxScore ?: 0.0) >= 90.0
                "CLIMAX" -> song.stageTag == "CLIMAX"
                "PRACTICE" -> song.stageTag == "PRACTICE"
                else -> true
            }

            matchesQuery && matchesMachine && matchesCategory
        }
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(BgPrimary)
    ) {
        // Clear & Prominent 3D Rounded Search Bar Section
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(BgPrimary)
                .padding(horizontal = 16.dp, vertical = 10.dp)
        ) {
            // High-visibility, 3D rounded search capsule (50dp height)
            Surface(
                shape = RoundedCornerShape(26.dp),
                color = CardBg,
                shadowElevation = 2.dp,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp)
                    .border(1.2.dp, CardBorder, RoundedCornerShape(26.dp))
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.Search,
                        contentDescription = null,
                        tint = TextMain,
                        modifier = Modifier.size(20.dp)
                    )

                    Spacer(modifier = Modifier.width(10.dp))

                    Box(
                        modifier = Modifier.weight(1f),
                        contentAlignment = Alignment.CenterStart
                    ) {
                        if (searchQuery.isEmpty()) {
                            Text(
                                text = "曲名・歌手名、またはリンクで検索",
                                fontSize = 14.sp,
                                color = TextSub
                            )
                        }

                        BasicTextField(
                            value = searchQuery,
                            onValueChange = { searchQuery = it },
                            singleLine = true,
                            textStyle = TextStyle(
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Medium,
                                color = TextMain
                            ),
                            cursorBrush = SolidColor(TextMain),
                            modifier = Modifier.fillMaxWidth()
                        )
                    }

                    if (searchQuery.isNotEmpty()) {
                        Surface(
                            shape = CircleShape,
                            color = BgSecondary,
                            modifier = Modifier.size(26.dp),
                            onClick = { searchQuery = "" }
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Icon(
                                    imageVector = Icons.Default.Close,
                                    contentDescription = "クリア",
                                    tint = TextMain,
                                    modifier = Modifier.size(14.dp)
                                )
                            }
                        }
                    }
                }
            }

            // Quick Link Detect Bar (if user pastes Spotify or YouTube Music URL)
            val isSpotify = IntentHelper.isSpotifyUrl(searchQuery)
            val isYtMusic = IntentHelper.isYouTubeMusicUrl(searchQuery)
            if (isSpotify || isYtMusic) {
                Spacer(modifier = Modifier.height(8.dp))
                Surface(
                    shape = RoundedCornerShape(16.dp),
                    color = SurfaceElevated,
                    shadowElevation = 2.dp,
                    modifier = Modifier
                        .fillMaxWidth()
                        .border(1.dp, TextMain, RoundedCornerShape(16.dp)),
                    onClick = {
                        val newSong = if (isSpotify) {
                            SongEntity(title = "", spotifyUrl = searchQuery.trim())
                        } else {
                            SongEntity(title = "", youtubeMusicUrl = searchQuery.trim())
                        }
                        editingSong = newSong
                        showEditDialog = true
                    }
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 14.dp, vertical = 8.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = if (isSpotify) "🔗 Spotifyの共有リンクを検出" else "🔗 YouTube Musicのリンクを検出",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = TextMain
                        )
                        Text(
                            text = "このリンクで曲を登録 ➔",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = TextMain
                        )
                    }
                }
            }

            // Machine Filters & Add Button (Rounded 3D Pills with native onClick!)
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 10.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    listOf("ALL" to "全機種", "DAM" to "DAM", "JOY" to "JOYSOUND").forEach { (code, label) ->
                        val isSelected = selectedMachine == code
                        Surface(
                            shape = RoundedCornerShape(16.dp),
                            color = if (isSelected) TextMain else CardBg,
                            border = if (isSelected) null else BorderStroke(1.dp, CardBorder),
                            shadowElevation = if (isSelected) 2.dp else 1.dp,
                            onClick = {
                                HapticHelper.playClick(context)
                                selectedMachine = code
                            }
                        ) {
                            Text(
                                text = label,
                                fontSize = 11.sp,
                                color = if (isSelected) BgPrimary else TextSub,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                modifier = Modifier.padding(horizontal = 12.dp, vertical = 5.dp)
                            )
                        }
                    }
                }

                Surface(
                    shape = RoundedCornerShape(16.dp),
                    color = BgSecondary,
                    border = BorderStroke(1.dp, CardBorder),
                    shadowElevation = 1.dp,
                    onClick = {
                        HapticHelper.playClick(context)
                        editingSong = SongEntity(title = searchQuery.trim())
                        showEditDialog = true
                    }
                ) {
                    Text(
                        text = "+ 新規曲を登録",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextMain,
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp)
                    )
                }
            }

            // Category & Stage Tags Pills (Native onClick ensures 100% responsiveness!)
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 8.dp),
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                listOf(
                    "ALL" to "すべて",
                    "KEPT" to "キープ",
                    "WARMUP" to "序盤",
                    "BEST" to "勝負曲",
                    "CLIMAX" to "終盤",
                    "PRACTICE" to "練習中"
                ).forEach { (code, label) ->
                    val isSelected = selectedCategory == code
                    Surface(
                        shape = RoundedCornerShape(14.dp),
                        color = if (isSelected) TextMain else CardBg,
                        border = if (isSelected) null else BorderStroke(1.dp, CardBorder),
                        shadowElevation = if (isSelected) 2.dp else 1.dp,
                        onClick = {
                            HapticHelper.playClick(context)
                            selectedCategory = code
                        }
                    ) {
                        Text(
                            text = label,
                            fontSize = 10.sp,
                            color = if (isSelected) BgPrimary else TextSub,
                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                            modifier = Modifier.padding(horizontal = 9.dp, vertical = 4.dp)
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(4.dp))

        // Search Results List (Rounded 3D Cards)
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .background(BgPrimary),
            contentPadding = PaddingValues(bottom = 90.dp)
        ) {
            if (filteredSongs.isEmpty()) {
                item {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 40.dp, horizontal = 16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = if (searchQuery.isEmpty()) "登録された曲がありません" else "「$searchQuery」に一致する曲は見つかりませんでした",
                            fontSize = 13.sp,
                            color = TextSub
                        )
                        if (searchQuery.isNotEmpty()) {
                            Spacer(modifier = Modifier.height(14.dp))
                            Surface(
                                shape = RoundedCornerShape(22.dp),
                                color = TextMain,
                                shadowElevation = 3.dp,
                                onClick = {
                                    editingSong = SongEntity(title = searchQuery.trim())
                                    showEditDialog = true
                                }
                            ) {
                                Text(
                                    text = "「$searchQuery」を新規登録する",
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = BgPrimary,
                                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 10.dp)
                                )
                            }
                        }
                    }
                }
            } else {
                items(filteredSongs, key = { it.song.id }) { item ->
                    val song = item.song
                    val defaultKeyStr = when {
                        song.defaultKey == 0 -> "±0"
                        song.defaultKey > 0 -> "+${song.defaultKey}"
                        else -> "${song.defaultKey}"
                    }

                    Surface(
                        shape = RoundedCornerShape(18.dp),
                        color = CardBg,
                        shadowElevation = 2.dp,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 4.dp)
                            .border(1.dp, CardBorder, RoundedCornerShape(18.dp))
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 12.dp, vertical = 10.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            // Song Info Click Area
                            Column(
                                modifier = Modifier
                                    .weight(1f)
                                    .clickable { onSongClick(song) }
                                    .padding(end = 6.dp)
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                                ) {
                                    Text(
                                        text = song.title,
                                        fontSize = 15.sp,
                                        fontWeight = FontWeight.SemiBold,
                                        color = TextMain,
                                        maxLines = 1,
                                        overflow = TextOverflow.Ellipsis
                                    )
                                    if (song.stageTag.isNotEmpty()) {
                                        val tagShort = when (song.stageTag) {
                                            "WARMUP" -> "序盤"
                                            "BEST" -> "勝負"
                                            "CLIMAX" -> "終盤"
                                            "PRACTICE" -> "練習"
                                            else -> song.stageTag
                                        }
                                        Surface(
                                            shape = RoundedCornerShape(6.dp),
                                            color = BgSecondary,
                                            modifier = Modifier.border(0.6.dp, CardBorder, RoundedCornerShape(6.dp))
                                        ) {
                                            Text(
                                                text = tagShort,
                                                fontSize = 9.sp,
                                                fontWeight = FontWeight.Bold,
                                                color = TextSub,
                                                modifier = Modifier.padding(horizontal = 5.dp, vertical = 1.dp)
                                            )
                                        }
                                    }
                                }

                                Row(
                                    modifier = Modifier.padding(top = 2.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                                ) {
                                    Text(
                                        text = if (song.artist.isNotEmpty()) song.artist else "未設定",
                                        fontSize = 12.sp,
                                        color = TextSub,
                                        maxLines = 1,
                                        overflow = TextOverflow.Ellipsis
                                    )

                                    if (song.defaultKey != 0) {
                                        Surface(
                                            shape = RoundedCornerShape(6.dp),
                                            color = BgSecondary,
                                            modifier = Modifier.border(0.6.dp, CardBorder, RoundedCornerShape(6.dp))
                                        ) {
                                            Text(
                                                text = defaultKeyStr,
                                                fontSize = 9.sp,
                                                fontWeight = FontWeight.Bold,
                                                fontFamily = FontFamily.Monospace,
                                                color = TextMain,
                                                modifier = Modifier.padding(horizontal = 4.dp, vertical = 1.dp)
                                            )
                                        }
                                    }

                                    if (song.isDamAvailable) {
                                        Surface(shape = RoundedCornerShape(4.dp), color = BgSecondary) {
                                            Text("DAM", fontSize = 8.sp, fontWeight = FontWeight.Bold, color = TextSub, modifier = Modifier.padding(horizontal = 3.dp, vertical = 1.dp))
                                        }
                                    }
                                    if (song.isJoysoundAvailable) {
                                        Surface(shape = RoundedCornerShape(4.dp), color = BgSecondary) {
                                            Text("JOY", fontSize = 8.sp, fontWeight = FontWeight.Bold, color = TextSub, modifier = Modifier.padding(horizontal = 3.dp, vertical = 1.dp))
                                        }
                                    }
                                }

                                Row(
                                    modifier = Modifier.padding(top = 4.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                                ) {
                                    if (item.maxScore != null) {
                                        Surface(
                                            shape = RoundedCornerShape(8.dp),
                                            color = BgSecondary,
                                            modifier = Modifier.border(0.8.dp, CardBorder, RoundedCornerShape(8.dp))
                                        ) {
                                            Text(
                                                text = "Best: ${String.format("%.1f", item.maxScore)}",
                                                fontSize = 10.sp,
                                                fontWeight = FontWeight.Bold,
                                                fontFamily = FontFamily.Monospace,
                                                color = TextMain,
                                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                            )
                                        }
                                    }

                                    if (song.spotifyUrl.isNotEmpty() || song.youtubeMusicUrl.isNotEmpty()) {
                                        Surface(
                                            shape = RoundedCornerShape(8.dp),
                                            color = BgSecondary,
                                            modifier = Modifier.border(0.8.dp, CardBorder, RoundedCornerShape(8.dp)),
                                            onClick = { listeningSong = song }
                                        ) {
                                            Row(
                                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
                                                verticalAlignment = Alignment.CenterVertically,
                                                horizontalArrangement = Arrangement.spacedBy(3.dp)
                                            ) {
                                                Icon(Icons.Default.Headphones, contentDescription = null, tint = TextMain, modifier = Modifier.size(11.dp))
                                                Text("原曲", fontSize = 10.sp, fontWeight = FontWeight.SemiBold, color = TextMain)
                                            }
                                        }
                                    }

                                    if (song.requestNumber.isNotEmpty()) {
                                        Text(
                                            text = "#${song.requestNumber}",
                                            fontSize = 10.sp,
                                            fontFamily = FontFamily.Monospace,
                                            color = TextSub,
                                            modifier = Modifier.padding(start = 2.dp)
                                        )
                                    }
                                }
                            }

                            // Right Action Buttons (Native onClick ensures 100% responsiveness!)
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(4.dp)
                            ) {
                                // ＋予約ボタン
                                Surface(
                                    shape = CircleShape,
                                    color = BgSecondary,
                                    modifier = Modifier.size(32.dp).border(1.dp, CardBorder, CircleShape),
                                    onClick = {
                                        HapticHelper.playClick(context)
                                        onAddToQueue(song)
                                        Toast.makeText(context, "予約ストックに追加しました", Toast.LENGTH_SHORT).show()
                                    }
                                ) {
                                    Box(contentAlignment = Alignment.Center) {
                                        Icon(Icons.Default.PlaylistAdd, contentDescription = "予約に追加", tint = TextMain, modifier = Modifier.size(17.dp))
                                    }
                                }

                                // キープボタン (ブックマーク)
                                Surface(
                                    shape = CircleShape,
                                    color = if (song.isKept) TextMain else BgSecondary,
                                    modifier = Modifier.size(32.dp).border(1.dp, if (song.isKept) TextMain else CardBorder, CircleShape),
                                    onClick = {
                                        HapticHelper.playClick(context)
                                        onKeepToggle(song)
                                    }
                                ) {
                                    Box(contentAlignment = Alignment.Center) {
                                        Icon(
                                            imageVector = if (song.isKept) Icons.Default.Bookmark else Icons.Outlined.BookmarkBorder,
                                            contentDescription = "キープ",
                                            tint = if (song.isKept) BgPrimary else IconInactive,
                                            modifier = Modifier.size(16.dp)
                                        )
                                    }
                                }

                                // 編集ボタン
                                Surface(
                                    shape = CircleShape,
                                    color = BgSecondary,
                                    modifier = Modifier.size(32.dp).border(1.dp, CardBorder, CircleShape),
                                    onClick = {
                                        HapticHelper.playClick(context)
                                        editingSong = song
                                        showEditDialog = true
                                    }
                                ) {
                                    Box(contentAlignment = Alignment.Center) {
                                        Icon(Icons.Default.Edit, contentDescription = "編集", tint = IconInactive, modifier = Modifier.size(15.dp))
                                    }
                                }

                                // 削除ボタン (ゴミ箱アイコン)
                                Surface(
                                    shape = CircleShape,
                                    color = BgSecondary,
                                    modifier = Modifier.size(32.dp).border(1.dp, CardBorder, CircleShape),
                                    onClick = {
                                        HapticHelper.playClick(context)
                                        songToDelete = song
                                    }
                                ) {
                                    Box(contentAlignment = Alignment.Center) {
                                        Icon(Icons.Default.DeleteOutline, contentDescription = "曲を削除", tint = IconInactive, modifier = Modifier.size(16.dp))
                                    }
                                }

                                // 歌うボタン
                                Surface(
                                    shape = RoundedCornerShape(14.dp),
                                    color = TextMain,
                                    shadowElevation = 2.dp,
                                    modifier = Modifier.height(30.dp),
                                    onClick = {
                                        HapticHelper.playClick(context)
                                        onSingClick(song)
                                    }
                                ) {
                                    Box(contentAlignment = Alignment.Center, modifier = Modifier.padding(horizontal = 10.dp)) {
                                        Text("歌う", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = BgPrimary)
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    // 試聴シート
    if (listeningSong != null) {
        ListenMusicSheet(
            title = listeningSong!!.title,
            artist = listeningSong!!.artist,
            spotifyUrl = listeningSong!!.spotifyUrl,
            youtubeMusicUrl = listeningSong!!.youtubeMusicUrl,
            onDismiss = { listeningSong = null }
        )
    }

    // 曲の削除確認ダイアログ
    if (songToDelete != null) {
        val s = songToDelete!!
        AlertDialog(
            onDismissRequest = { songToDelete = null },
            shape = RoundedCornerShape(20.dp),
            title = { Text("曲の削除", fontSize = 15.sp, fontWeight = FontWeight.Bold) },
            text = { Text("「${s.title}」をライブラリから削除しますか？", fontSize = 13.sp, color = TextSub) },
            confirmButton = {
                Surface(
                    shape = RoundedCornerShape(14.dp),
                    color = TextMain,
                    onClick = {
                        onDeleteSong(s.id)
                        songToDelete = null
                        Toast.makeText(context, "曲を削除しました", Toast.LENGTH_SHORT).show()
                    }
                ) {
                    Text("削除する", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = BgPrimary, modifier = Modifier.padding(horizontal = 14.dp, vertical = 6.dp))
                }
            },
            dismissButton = {
                TextButton(onClick = { songToDelete = null }) {
                    Text("キャンセル", fontSize = 12.sp, color = TextSub)
                }
            },
            containerColor = CardBg
        )
    }

    // 曲の登録・編集ダイアログ
    if (showEditDialog && editingSong != null) {
        val target = editingSong!!
        var titleInput by remember { mutableStateOf(target.title) }
        var artistInput by remember { mutableStateOf(target.artist) }
        var keyInput by remember { mutableStateOf(target.defaultKey) }
        var reqInput by remember { mutableStateOf(target.requestNumber) }
        var damInput by remember { mutableStateOf(target.isDamAvailable) }
        var joyInput by remember { mutableStateOf(target.isJoysoundAvailable) }
        var stageInput by remember { mutableStateOf(target.stageTag) }
        var spotifyInput by remember { mutableStateOf(target.spotifyUrl) }
        var ytMusicInput by remember { mutableStateOf(target.youtubeMusicUrl) }

        AlertDialog(
            onDismissRequest = { showEditDialog = false },
            shape = RoundedCornerShape(24.dp),
            title = {
                Text(
                    text = if (target.id == 0L) "新しい曲を登録" else "楽曲の編集",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold
                )
            },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(
                        value = titleInput,
                        onValueChange = { titleInput = it },
                        label = { Text("曲名 (必須)") },
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.fillMaxWidth()
                    )
                    OutlinedTextField(
                        value = artistInput,
                        onValueChange = { artistInput = it },
                        label = { Text("アーティスト名") },
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.fillMaxWidth()
                    )

                    // Key Adjuster (Rounded 3D)
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("マイキー設定:", fontSize = 12.sp, color = TextSub)
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Surface(
                                shape = CircleShape,
                                color = BgSecondary,
                                modifier = Modifier.size(32.dp).border(1.dp, CardBorder, CircleShape),
                                onClick = { keyInput-- }
                            ) {
                                Box(contentAlignment = Alignment.Center) {
                                    Icon(Icons.Default.Remove, contentDescription = null, modifier = Modifier.size(16.dp))
                                }
                            }
                            Text(
                                text = when {
                                    keyInput == 0 -> "±0"
                                    keyInput > 0 -> "+$keyInput"
                                    else -> "$keyInput"
                                },
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.width(32.dp),
                                textAlign = androidx.compose.ui.text.style.TextAlign.Center
                            )
                            Surface(
                                shape = CircleShape,
                                color = BgSecondary,
                                modifier = Modifier.size(32.dp).border(1.dp, CardBorder, CircleShape),
                                onClick = { keyInput++ }
                            ) {
                                Box(contentAlignment = Alignment.Center) {
                                    Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(16.dp))
                                }
                            }
                        }
                    }

                    OutlinedTextField(
                        value = reqInput,
                        onValueChange = { reqInput = it },
                        label = { Text("デンモク予約番号") },
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.fillMaxWidth()
                    )

                    // Spotify URL
                    OutlinedTextField(
                        value = spotifyInput,
                        onValueChange = { spotifyInput = it },
                        label = { Text("Spotifyリンク (任意)") },
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.fillMaxWidth(),
                        placeholder = { Text("https://open.spotify.com/...", fontSize = 11.sp) }
                    )

                    // YouTube Music URL
                    OutlinedTextField(
                        value = ytMusicInput,
                        onValueChange = { ytMusicInput = it },
                        label = { Text("YouTube Musicリンク (任意)") },
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.fillMaxWidth(),
                        placeholder = { Text("https://music.youtube.com/...", fontSize = 11.sp) }
                    )

                    // Machine support flags
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("配信機種:", fontSize = 12.sp, color = TextSub)
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Checkbox(checked = damInput, onCheckedChange = { damInput = it })
                            Text("DAM", fontSize = 12.sp)
                        }
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Checkbox(checked = joyInput, onCheckedChange = { joyInput = it })
                            Text("JOY", fontSize = 12.sp)
                        }
                    }
                }
            },
            confirmButton = {
                Surface(
                    shape = RoundedCornerShape(18.dp),
                    color = TextMain,
                    shadowElevation = 2.dp,
                    onClick = {
                        if (titleInput.isNotBlank()) {
                            onAddOrEditSong(
                                target.copy(
                                    title = titleInput.trim(),
                                    artist = artistInput.trim(),
                                    defaultKey = keyInput,
                                    requestNumber = reqInput.trim(),
                                    isDamAvailable = damInput,
                                    isJoysoundAvailable = joyInput,
                                    stageTag = stageInput,
                                    spotifyUrl = spotifyInput.trim(),
                                    youtubeMusicUrl = ytMusicInput.trim()
                                )
                            )
                            showEditDialog = false
                            Toast.makeText(context, "曲を保存しました", Toast.LENGTH_SHORT).show()
                        }
                    }
                ) {
                    Text(
                        text = "保存する",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = BgPrimary,
                        modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
                    )
                }
            },
            dismissButton = {
                TextButton(onClick = { showEditDialog = false }) {
                    Text("キャンセル", fontSize = 12.sp, color = TextSub)
                }
            },
            containerColor = CardBg
        )
    }
}
