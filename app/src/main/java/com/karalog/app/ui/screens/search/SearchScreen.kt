package com.karalog.app.ui.screens.search

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.BookmarkBorder
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.RectangleShape
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
        // Clear & Prominent Search Bar Section
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(BgPrimary)
                .padding(horizontal = 16.dp, vertical = 12.dp)
        ) {
            // High-visibility, perfectly centered search input (48dp height)
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp)
                    .background(BgSecondary)
                    .border(1.2.dp, BorderLight)
                    .padding(horizontal = 12.dp),
                contentAlignment = Alignment.CenterStart
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
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
                        IconButton(
                            onClick = { searchQuery = "" },
                            modifier = Modifier.size(28.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Close,
                                contentDescription = "クリア",
                                tint = TextMain,
                                modifier = Modifier.size(18.dp)
                            )
                        }
                    }
                }
            }

            // Quick Link Detect Bar (if user pastes Spotify or YouTube Music URL)
            val isSpotify = IntentHelper.isSpotifyUrl(searchQuery)
            val isYtMusic = IntentHelper.isYouTubeMusicUrl(searchQuery)
            if (isSpotify || isYtMusic) {
                Spacer(modifier = Modifier.height(6.dp))
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(BgSecondary)
                        .border(1.dp, BorderLight)
                        .clickable {
                            val newSong = if (isSpotify) {
                                SongEntity(title = "", spotifyUrl = searchQuery.trim())
                            } else {
                                SongEntity(title = "", youtubeMusicUrl = searchQuery.trim())
                            }
                            editingSong = newSong
                            showEditDialog = true
                        }
                        .padding(horizontal = 10.dp, vertical = 6.dp),
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
                        text = "このリンクで曲を統合登録 ➔",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextMain
                    )
                }
            }

            // Machine Filters & Add Button
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
                        Box(
                            modifier = Modifier
                                .clickable { selectedMachine = code }
                                .background(if (isSelected) TextMain else BgSecondary)
                                .border(1.dp, if (isSelected) TextMain else BorderLight)
                                .padding(horizontal = 10.dp, vertical = 5.dp)
                        ) {
                            Text(
                                text = label,
                                fontSize = 11.sp,
                                color = if (isSelected) BgPrimary else TextSub,
                                fontWeight = if (isSelected) FontWeight.SemiBold else FontWeight.Normal
                            )
                        }
                    }
                }

                Text(
                    text = "+ 新規曲を追加",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextMain,
                    modifier = Modifier.clickable {
                        editingSong = SongEntity(title = searchQuery.trim())
                        showEditDialog = true
                    }
                )
            }

            // Category & Stage Tags Pills
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
                    Box(
                        modifier = Modifier
                            .clickable { selectedCategory = code }
                            .background(if (isSelected) TextMain else BgSecondary)
                            .border(1.dp, if (isSelected) TextMain else BorderLight)
                            .padding(horizontal = 8.dp, vertical = 4.dp)
                    ) {
                        Text(
                            text = label,
                            fontSize = 10.sp,
                            color = if (isSelected) BgPrimary else TextSub,
                            fontWeight = if (isSelected) FontWeight.SemiBold else FontWeight.Normal
                        )
                    }
                }
            }
        }

        Box(modifier = Modifier.fillMaxWidth().height(1.dp).background(BorderLight))

        // Search Results List (Showing 1 song with integrated Spotify & YouTube Music)
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .background(BgPrimary)
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
                            fontSize = 12.sp,
                            color = TextSub
                        )
                        if (searchQuery.isNotEmpty()) {
                            Spacer(modifier = Modifier.height(12.dp))
                            Button(
                                onClick = {
                                    editingSong = SongEntity(title = searchQuery.trim())
                                    showEditDialog = true
                                },
                                colors = ButtonDefaults.buttonColors(containerColor = TextMain, contentColor = BgPrimary),
                                shape = RectangleShape
                            ) {
                                Text("「$searchQuery」を新規登録する", fontSize = 12.sp)
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

                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { onSongClick(song) }
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 16.dp, vertical = 12.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(modifier = Modifier.weight(1f).padding(end = 8.dp)) {
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
                                        Box(
                                            modifier = Modifier
                                                .background(BgSecondary)
                                                .border(1.dp, BorderLight)
                                                .padding(horizontal = 4.dp, vertical = 1.dp)
                                        ) {
                                            Text(tagShort, fontSize = 9.sp, color = TextSub)
                                        }
                                    }
                                }

                                Row(
                                    modifier = Modifier.padding(top = 3.dp),
                                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    if (song.artist.isNotEmpty()) {
                                        Text(text = song.artist, fontSize = 12.sp, color = TextSub)
                                        Text(text = "·", fontSize = 10.sp, color = BorderLight)
                                    }
                                    Text(text = "キー $defaultKeyStr", fontSize = 11.sp, color = TextSub)

                                    if (song.requestNumber.isNotEmpty()) {
                                        Text(text = "·", fontSize = 10.sp, color = BorderLight)
                                        Text(
                                            text = "No. ${song.requestNumber}",
                                            fontSize = 11.sp,
                                            fontFamily = FontFamily.Monospace,
                                            color = TextSub
                                        )
                                    }
                                }

                                // Integrated Music Platforms (Spotify & YouTube Music together on 1 song)
                                Row(
                                    modifier = Modifier.padding(top = 5.dp),
                                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    // Quick Spotify Button
                                    Row(
                                        modifier = Modifier
                                            .background(BgSecondary)
                                            .border(1.dp, BorderLight)
                                            .clickable {
                                                HapticHelper.playClick(context)
                                                IntentHelper.openSpotify(context, song.title, song.artist, song.spotifyUrl)
                                            }
                                            .padding(horizontal = 6.dp, vertical = 2.dp),
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.spacedBy(3.dp)
                                    ) {
                                        Text("▶ Spotify", fontSize = 10.sp, fontWeight = FontWeight.SemiBold, color = TextMain)
                                    }

                                    // Quick YouTube Music Button
                                    Row(
                                        modifier = Modifier
                                            .background(BgSecondary)
                                            .border(1.dp, BorderLight)
                                            .clickable {
                                                HapticHelper.playClick(context)
                                                IntentHelper.openYouTubeMusic(context, song.title, song.artist, song.youtubeMusicUrl)
                                            }
                                            .padding(horizontal = 6.dp, vertical = 2.dp),
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.spacedBy(3.dp)
                                    ) {
                                        Text("▶ YT Music", fontSize = 10.sp, fontWeight = FontWeight.SemiBold, color = TextMain)
                                    }

                                    if (item.maxScore != null) {
                                        Text(
                                            text = "最高 ${String.format("%.1f", item.maxScore)}点",
                                            fontSize = 10.sp,
                                            fontFamily = FontFamily.Monospace,
                                            color = TextSub,
                                            modifier = Modifier.padding(start = 2.dp)
                                        )
                                    }
                                }
                            }

                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(2.dp)
                            ) {
                                IconButton(
                                    onClick = { onKeepToggle(song) },
                                    modifier = Modifier.size(32.dp)
                                ) {
                                    Icon(
                                        imageVector = if (song.isKept) Icons.Default.Bookmark else Icons.Outlined.BookmarkBorder,
                                        contentDescription = "キープ",
                                        tint = if (song.isKept) TextMain else IconInactive,
                                        modifier = Modifier.size(18.dp)
                                    )
                                }

                                IconButton(
                                    onClick = {
                                        editingSong = song
                                        showEditDialog = true
                                    },
                                    modifier = Modifier.size(32.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Edit,
                                        contentDescription = "編集",
                                        tint = IconInactive,
                                        modifier = Modifier.size(16.dp)
                                    )
                                }

                                Button(
                                    onClick = { onSingClick(song) },
                                    colors = ButtonDefaults.buttonColors(containerColor = TextMain, contentColor = BgPrimary),
                                    shape = RectangleShape,
                                    contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp),
                                    modifier = Modifier.height(28.dp)
                                ) {
                                    Text("歌う", fontSize = 11.sp, fontWeight = FontWeight.SemiBold)
                                }
                            }
                        }
                        Box(modifier = Modifier.fillMaxWidth().height(1.dp).background(BorderLight))
                    }
                }
            }
        }
    }

    // Listen Music Sheet
    if (listeningSong != null) {
        ListenMusicSheet(
            title = listeningSong!!.title,
            artist = listeningSong!!.artist,
            spotifyUrl = listeningSong!!.spotifyUrl,
            youtubeMusicUrl = listeningSong!!.youtubeMusicUrl,
            onDismiss = { listeningSong = null }
        )
    }

    // Add / Edit Song Dialog (Handles single song with Spotify & YT Music merged)
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
            title = {
                Text(
                    if (target.id == 0L) "曲の新規登録 (1曲に統合)" else "曲情報の編集",
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextMain
                )
            },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text(
                        text = "※同名・同アーティストの曲は自動的に1曲に統合されます",
                        fontSize = 10.sp,
                        color = TextSub
                    )

                    OutlinedTextField(
                        value = titleInput,
                        onValueChange = { titleInput = it },
                        label = { Text("曲名 (必須)", fontSize = 11.sp) },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                    OutlinedTextField(
                        value = artistInput,
                        onValueChange = { artistInput = it },
                        label = { Text("歌手名", fontSize = 11.sp) },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("基準マイキー: ${if (keyInput >= 0) "+$keyInput" else "$keyInput"}", fontSize = 12.sp, color = TextMain)
                        Row {
                            IconButton(onClick = { keyInput-- }, modifier = Modifier.size(32.dp)) {
                                Icon(Icons.Default.Remove, contentDescription = "-")
                            }
                            IconButton(onClick = { keyInput++ }, modifier = Modifier.size(32.dp)) {
                                Icon(Icons.Default.Add, contentDescription = "+")
                            }
                        }
                    }

                    OutlinedTextField(
                        value = reqInput,
                        onValueChange = { reqInput = it },
                        label = { Text("デンモク予約番号", fontSize = 11.sp) },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )

                    // Spotify & YouTube Music Integration URLs
                    OutlinedTextField(
                        value = spotifyInput,
                        onValueChange = { spotifyInput = it },
                        label = { Text("Spotify リンク (任意)", fontSize = 10.sp) },
                        placeholder = { Text("https://open.spotify.com/...", fontSize = 10.sp) },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )

                    OutlinedTextField(
                        value = ytMusicInput,
                        onValueChange = { ytMusicInput = it },
                        label = { Text("YouTube Music リンク (任意)", fontSize = 10.sp) },
                        placeholder = { Text("https://music.youtube.com/...", fontSize = 10.sp) },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )

                    // Stage Tag Selector
                    Column {
                        Text("歌唱シーン:", fontSize = 10.sp, color = TextSub)
                        Spacer(modifier = Modifier.height(3.dp))
                        Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                            listOf(
                                "" to "未設定",
                                "WARMUP" to "序盤",
                                "BEST" to "勝負曲",
                                "CLIMAX" to "終盤",
                                "PRACTICE" to "練習中"
                            ).forEach { (code, label) ->
                                val isSel = stageInput == code
                                Box(
                                    modifier = Modifier
                                        .clickable { stageInput = code }
                                        .background(if (isSel) TextMain else BgSecondary)
                                        .border(1.dp, BorderLight)
                                        .padding(horizontal = 6.dp, vertical = 2.dp)
                                ) {
                                    Text(label, fontSize = 10.sp, color = if (isSel) BgPrimary else TextSub)
                                }
                            }
                        }
                    }

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("配信:", fontSize = 11.sp, color = TextSub)
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Checkbox(checked = damInput, onCheckedChange = { damInput = it })
                            Text("DAM", fontSize = 11.sp)
                        }
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Checkbox(checked = joyInput, onCheckedChange = { joyInput = it })
                            Text("JOY", fontSize = 11.sp)
                        }
                    }
                }
            },
            confirmButton = {
                Button(
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
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = TextMain, contentColor = BgPrimary),
                    shape = RectangleShape
                ) {
                    Text("1曲に統合して保存", fontSize = 12.sp)
                }
            },
            dismissButton = {
                TextButton(onClick = { showEditDialog = false }) {
                    Text("キャンセル", fontSize = 12.sp, color = TextSub)
                }
            },
            containerColor = BgPrimary
        )
    }
}
