package com.karalog.app.ui.screens.home

import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.karalog.app.data.local.entity.SongEntity
import com.karalog.app.data.model.SongWithStats
import com.karalog.app.ui.components.SongCard
import com.karalog.app.ui.theme.*
import com.karalog.app.util.HapticHelper

enum class MachineFilter(val title: String) {
    ALL("\u3059\u3079\u3066"),
    DAM("DAM"),
    JOYSOUND("JOYSOUND")
}

@Composable
fun HomeScreen(
    songsWithStats: List<SongWithStats>,
    keptSongs: List<SongEntity>,
    onStartSinging: (song: SongEntity) -> Unit,
    onKeyChange: (songId: Long, newKey: Int) -> Unit,
    onToggleKept: (songId: Long, currentKept: Boolean) -> Unit,
    onQuickAddSong: (title: String) -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    var searchQuery by remember { mutableStateOf("") }
    var selectedFilter by remember { mutableStateOf(MachineFilter.ALL) }

    val filteredSongs = remember(songsWithStats, searchQuery, selectedFilter) {
        songsWithStats.filter { item ->
            val matchFilter = when (selectedFilter) {
                MachineFilter.ALL -> true
                MachineFilter.DAM -> item.isDamAvailable
                MachineFilter.JOYSOUND -> item.isJoysoundAvailable
            }
            val matchQuery = searchQuery.isBlank() ||
                item.title.contains(searchQuery, ignoreCase = true) ||
                item.artist.contains(searchQuery, ignoreCase = true)

            matchFilter && matchQuery
        }
    }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
        contentPadding = PaddingValues(top = 8.dp, bottom = 100.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        if (keptSongs.isNotEmpty()) {
            item {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(Icons.Default.Bookmark, contentDescription = null, tint = AmberStar, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "\u672c\u65e5\u306e\u6b4c\u5531\u5019\u88dc (${keptSongs.size}\u66f2)",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold,
                            color = AmberStar
                        )
                    }

                    LazyRow(
                        horizontalArrangement = Arrangement.spacedBy(10.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        items(keptSongs, key = { it.id }) { song ->
                            Surface(
                                shape = RoundedCornerShape(16.dp),
                                color = SurfaceDark,
                                modifier = Modifier
                                    .width(180.dp)
                                    .border(1.dp, AmberStar.copy(alpha = 0.5f), RoundedCornerShape(16.dp))
                                    .clickable {
                                        HapticHelper.playHeavyClick(context)
                                        onStartSinging(song)
                                    }
                            ) {
                                Column(modifier = Modifier.padding(12.dp)) {
                                    Text(
                                        text = song.title,
                                        fontSize = 14.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = Color.White,
                                        maxLines = 1
                                    )
                                    Text(
                                        text = song.artist.ifBlank { "\u30a2\u30fc\u30c6\u30a3\u30b9\u30c8\u672a\u8a2d\u5b9a" },
                                        fontSize = 11.sp,
                                        color = Color(0xFF90A4AE),
                                        maxLines = 1
                                    )
                                    Spacer(modifier = Modifier.height(6.dp))
                                    Text(
                                        text = "\u25b6 \u6b4c\u3046",
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = NeonCyan
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }

        item {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = { searchQuery = it },
                    placeholder = { Text("\u66f2\u540d\u30fb\u30a2\u30fc\u30c6\u30a3\u30b9\u30c8\u540d\u3067\u691c\u7d22...") },
                    leadingIcon = { Icon(Icons.Default.Search, contentDescription = null, tint = NeonCyan) },
                    singleLine = true,
                    shape = RoundedCornerShape(20.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedContainerColor = SurfaceDark,
                        unfocusedContainerColor = SurfaceDark,
                        focusedBorderColor = NeonCyan,
                        unfocusedBorderColor = OutlineDark,
                        focusedTextColor = Color.White,
                        unfocusedTextColor = Color.White
                    ),
                    modifier = Modifier.fillMaxWidth()
                )

                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    MachineFilter.values().forEach { filter ->
                        val isSelected = filter == selectedFilter
                        FilterChip(
                            selected = isSelected,
                            onClick = {
                                HapticHelper.playClick(context)
                                selectedFilter = filter
                            },
                            label = { Text(filter.title, fontWeight = FontWeight.Bold, fontSize = 12.sp) },
                            shape = RoundedCornerShape(14.dp),
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = NeonCyan,
                                selectedLabelColor = MidnightBlack,
                                containerColor = SurfaceDark,
                                labelColor = Color(0xFF90A4AE)
                            ),
                            border = FilterChipDefaults.filterChipBorder(
                                enabled = true,
                                selected = isSelected,
                                borderColor = OutlineDark
                            )
                        )
                    }
                }
            }
        }

        if (searchQuery.isNotBlank() && filteredSongs.none { it.title.equals(searchQuery.trim(), ignoreCase = true) }) {
            item {
                Surface(
                    shape = RoundedCornerShape(16.dp),
                    color = VibrantBlue.copy(alpha = 0.2f),
                    modifier = Modifier
                        .fillMaxWidth()
                        .border(1.dp, NeonCyan, RoundedCornerShape(16.dp))
                        .clickable {
                            val trimmed = searchQuery.trim()
                            HapticHelper.playHeavyClick(context)
                            onQuickAddSong(trimmed)
                            searchQuery = ""
                        }
                ) {
                    Row(
                        modifier = Modifier.padding(14.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(Icons.Default.Add, contentDescription = null, tint = NeonCyan)
                        Spacer(modifier = Modifier.width(10.dp))
                        Text(
                            text = "\u300c" + searchQuery.trim() + "\u300d\u3092\u65b0\u3057\u304f\u8ffd\u52a0\u3057\u3066\u4eca\u3059\u304f\u6b4c\u3046",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold,
                            color = NeonCyan
                        )
                    }
                }
            }
        }

        if (filteredSongs.isEmpty()) {
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 40.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text("\u8a72\u5f53\u3059\u308b\u66f2\u304c\u3042\u308a\u307e\u305b\u3093", color = Color(0xFF90A4AE), fontSize = 14.sp)
                }
            }
        } else {
            items(filteredSongs, key = { it.song.id }) { item ->
                SongCard(
                    item = item,
                    onStartSinging = { onStartSinging(item.song) },
                    onKeyChange = { newKey -> onKeyChange(item.song.id, newKey) },
                    onToggleKept = { onToggleKept(item.song.id, item.song.isKept) }
                )
            }
        }
    }
}