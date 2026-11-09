package com.karalog.app.ui.screens.search

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.BookmarkBorder
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.karalog.app.data.local.entity.SongEntity
import com.karalog.app.data.model.SongWithStats
import com.karalog.app.ui.theme.*

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
    var searchQuery by remember { mutableStateOf("") }
    var selectedMachine by remember { mutableStateOf("ALL") } // ALL, DAM, JOY
    var selectedCategory by remember { mutableStateOf(initialCategory) } // ALL, KEPT, BEST, UNTRIED
    var editingSong by remember { mutableStateOf<SongEntity?>(null) }
    var showEditDialog by remember { mutableStateOf(false) }

    val filteredSongs = remember(songsWithStats, searchQuery, selectedMachine, selectedCategory) {
        songsWithStats.filter { item ->
            val song = item.song
            val matchesQuery = searchQuery.isEmpty() ||
                    song.title.contains(searchQuery, ignoreCase = true) ||
                    song.artist.contains(searchQuery, ignoreCase = true) ||
                    song.requestNumber.contains(searchQuery, ignoreCase = true)

            val matchesMachine = when (selectedMachine) {
                "DAM" -> song.isDamAvailable
                "JOY" -> song.isJoysoundAvailable
                else -> true
            }

            val matchesCategory = when (selectedCategory) {
                "KEPT" -> song.isKept
                "BEST" -> (item.maxScore ?: 0.0) >= 90.0
                "UNTRIED" -> item.singCount == 0
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
        // Search Input Bar
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(BgPrimary)
                .padding(horizontal = 16.dp, vertical = 10.dp)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(40.dp)
                    .background(BgSecondary)
                    .border(1.dp, BorderLight)
                    .padding(horizontal = 12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Default.Search,
                    contentDescription = null,
                    tint = IconInactive,
                    modifier = Modifier.size(18.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                TextField(
                    value = searchQuery,
                    onValueChange = { searchQuery = it },
                    placeholder = {
                        Text("曲名、歌手名、予約番号で検索", fontSize = 13.sp, color = TextSub)
                    },
                    colors = TextFieldDefaults.colors(
                        focusedContainerColor = BgSecondary,
                        unfocusedContainerColor = BgSecondary,
                        focusedIndicatorColor = androidx.compose.ui.graphics.Color.Transparent,
                        unfocusedIndicatorColor = androidx.compose.ui.graphics.Color.Transparent,
                        focusedTextColor = TextMain,
                        unfocusedTextColor = TextMain
                    ),
                    singleLine = true,
                    modifier = Modifier.weight(1f)
                )
                if (searchQuery.isNotEmpty()) {
                    IconButton(
                        onClick = { searchQuery = "" },
                        modifier = Modifier.size(24.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "Clear",
                            tint = IconInactive,
                            modifier = Modifier.size(16.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Machine Filters & Category Pills
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Category Pills
                Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                    listOf("ALL" to "すべて", "KEPT" to "キープ", "BEST" to "十八番", "UNTRIED" to "未挑戦").forEach { (code, label) ->
                        val isSel = selectedCategory == code
                        Box(
                            modifier = Modifier
                                .clickable { selectedCategory = code }
                                .background(if (isSel) TextMain else BgSecondary)
                                .border(if (isSel) 0.dp else 1.dp, BorderLight)
                                .padding(horizontal = 8.dp, vertical = 4.dp)
                        ) {
                            Text(
                                text = label,
                                fontSize = 11.sp,
                                color = if (isSel) BgPrimary else TextSub,
                                fontWeight = if (isSel) FontWeight.SemiBold else FontWeight.Normal
                            )
                        }
                    }
                }

                // Machine Selector (ALL / DAM / JOY)
                Row(horizontalArrangement = Arrangement.spacedBy(3.dp)) {
                    listOf("ALL", "DAM", "JOY").forEach { m ->
                        val isSel = selectedMachine == m
                        Box(
                            modifier = Modifier
                                .clickable { selectedMachine = m }
                                .background(if (isSel) TextMain else BgSecondary)
                                .padding(horizontal = 6.dp, vertical = 4.dp)
                        ) {
                            Text(
                                text = m,
                                fontSize = 10.sp,
                                color = if (isSel) BgPrimary else TextSub,
                                fontWeight = if (isSel) FontWeight.Bold else FontWeight.Normal
                            )
                        }
                    }
                }
            }
        }

        Box(modifier = Modifier.fillMaxWidth().height(1.dp).background(BorderLight))

        // Quick Add Card if searching and not exact match
        if (searchQuery.isNotBlank() && songsWithStats.none { it.song.title.equals(searchQuery.trim(), ignoreCase = true) }) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(BgSecondary)
                    .clickable {
                        editingSong = SongEntity(title = searchQuery.trim())
                        showEditDialog = true
                    }
                    .padding(horizontal = 16.dp, vertical = 10.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    Icon(Icons.Default.Add, contentDescription = null, tint = TextMain, modifier = Modifier.size(18.dp))
                    Text(
                        text = "「${searchQuery.trim()}」をマイライブラリに追加",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Medium,
                        color = TextMain
                    )
                }
                Text("＋登録", fontSize = 11.sp, color = TextMain, fontWeight = FontWeight.Bold)
            }
            Box(modifier = Modifier.fillMaxWidth().height(1.dp).background(BorderLight))
        }

        // Search Results Count
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 6.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = "${filteredSongs.size} 曲が該当",
                fontSize = 11.sp,
                color = TextSub
            )
            Text(
                text = "タップで歌唱開始",
                fontSize = 11.sp,
                color = TextSub
            )
        }

        // Song List
        LazyColumn(modifier = Modifier.fillMaxSize()) {
            if (filteredSongs.isEmpty()) {
                item {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 48.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Icon(Icons.Default.MusicNote, contentDescription = null, tint = IconInactive, modifier = Modifier.size(32.dp))
                            Spacer(modifier = Modifier.height(8.dp))
                            Text("該当する曲が見つかりません", fontSize = 13.sp, color = TextSub)
                            if (searchQuery.isNotEmpty()) {
                                Spacer(modifier = Modifier.height(8.dp))
                                OutlinedButton(
                                    onClick = {
                                        editingSong = SongEntity(title = searchQuery.trim())
                                        showEditDialog = true
                                    },
                                    shape = RectangleShape,
                                    border = androidx.compose.foundation.BorderStroke(1.dp, BorderLight),
                                    colors = ButtonDefaults.outlinedButtonColors(contentColor = TextMain)
                                ) {
                                    Text("この曲を登録する", fontSize = 12.sp)
                                }
                            }
                        }
                    }
                }
            } else {
                items(filteredSongs, key = { it.song.id }) { item ->
                    val song = item.song
                    val bestScore = item.maxScore
                    val keyStr = when {
                        song.defaultKey == 0 -> "±0"
                        song.defaultKey > 0 -> "+${song.defaultKey}"
                        else -> "${song.defaultKey}"
                    }

                    Column(modifier = Modifier.fillMaxWidth()) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { onSongClick(song) }
                                .padding(horizontal = 16.dp, vertical = 10.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Row(
                                modifier = Modifier.weight(1f).padding(end = 8.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(12.dp)
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(44.dp)
                                        .clip(RoundedCornerShape(4.dp))
                                        .background(BgSecondary)
                                        .border(0.8.dp, BorderLight, RoundedCornerShape(4.dp)),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.MusicNote,
                                        contentDescription = null,
                                        tint = IconInactive,
                                        modifier = Modifier.size(20.dp)
                                    )
                                }

                                Column(modifier = Modifier.weight(1f)) {
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                                    ) {
                                        Text(
                                            text = song.title,
                                            fontSize = 14.sp,
                                            fontWeight = FontWeight.Medium,
                                            color = TextMain,
                                            maxLines = 1,
                                            overflow = TextOverflow.Ellipsis,
                                            modifier = Modifier.weight(1f, fill = false)
                                        )
                                        if (bestScore != null) {
                                            Text(
                                                text = String.format("%.1f", bestScore),
                                                fontSize = 11.sp,
                                                fontWeight = FontWeight.Bold,
                                                color = TextMain
                                            )
                                        }
                                    }

                                    Row(
                                        modifier = Modifier.padding(top = 2.dp),
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                                    ) {
                                        Text(
                                            text = song.artist.ifEmpty { "アーティスト未設定" },
                                            fontSize = 11.sp,
                                            color = TextSub,
                                            maxLines = 1,
                                            overflow = TextOverflow.Ellipsis
                                        )
                                        Text(text = "/", fontSize = 10.sp, color = BorderLight)
                                        Text(text = "キー $keyStr", fontSize = 11.sp, color = TextSub)
                                        if (song.requestNumber.isNotEmpty()) {
                                            Text(text = "/", fontSize = 10.sp, color = BorderLight)
                                            Text(text = "No.${song.requestNumber}", fontSize = 11.sp, color = TextSub)
                                        }
                                    }
                                }
                            }

                            Row(verticalAlignment = Alignment.CenterVertically) {
                                IconButton(
                                    onClick = { onKeepToggle(song) },
                                    modifier = Modifier.size(32.dp)
                                ) {
                                    Icon(
                                        imageVector = if (song.isKept) Icons.Filled.Bookmark else Icons.Outlined.BookmarkBorder,
                                        contentDescription = "Keep",
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
                                        contentDescription = "Edit",
                                        tint = IconInactive,
                                        modifier = Modifier.size(16.dp)
                                    )
                                }
                                IconButton(
                                    onClick = { onSingClick(song) },
                                    modifier = Modifier.size(32.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Mic,
                                        contentDescription = "Sing",
                                        tint = TextMain,
                                        modifier = Modifier.size(18.dp)
                                    )
                                }
                            }
                        }
                        Box(modifier = Modifier.fillMaxWidth().height(1.dp).background(BorderLight))
                    }
                }
            }
        }
    }

    // Add / Edit Song Dialog
    if (showEditDialog && editingSong != null) {
        val target = editingSong!!
        var titleInput by remember { mutableStateOf(target.title) }
        var artistInput by remember { mutableStateOf(target.artist) }
        var keyInput by remember { mutableStateOf(target.defaultKey) }
        var reqInput by remember { mutableStateOf(target.requestNumber) }
        var damInput by remember { mutableStateOf(target.isDamAvailable) }
        var joyInput by remember { mutableStateOf(target.isJoysoundAvailable) }

        AlertDialog(
            onDismissRequest = { showEditDialog = false },
            title = {
                Text(
                    if (target.id == 0L) "曲の新規登録" else "曲情報の編集",
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextMain
                )
            },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
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
                        Text("基準キー: ${if (keyInput >= 0) "+$keyInput" else "$keyInput"}", fontSize = 12.sp, color = TextMain)
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
                        label = { Text("デンモク予約番号 (メモ)", fontSize = 11.sp) },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("配信:", fontSize = 12.sp, color = TextSub)
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
                                    isJoysoundAvailable = joyInput
                                )
                            )
                            showEditDialog = false
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = TextMain, contentColor = BgPrimary),
                    shape = RectangleShape
                ) {
                    Text("保存", fontSize = 12.sp)
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