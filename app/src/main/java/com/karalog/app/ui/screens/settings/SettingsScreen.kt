package com.karalog.app.ui.screens.settings

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.karalog.app.ui.theme.*
import kotlinx.coroutines.launch

@Composable
fun SettingsScreen(
    totalSongsCount: Int,
    totalRecordsCount: Int,
    currentDefaultMachine: String,
    onDefaultMachineChange: (String) -> Unit,
    autoResetAt3Am: Boolean = true,
    onAutoResetChange: (Boolean) -> Unit = {},
    currentThemeMode: String = "SYSTEM",
    onThemeModeChange: (String) -> Unit = {},
    onResetSession: () -> Unit,
    onSeedPopularSongs: () -> Unit = {},
    onResetAllData: () -> Unit,
    onExportJson: suspend () -> String,
    onImportJson: suspend (String) -> Boolean,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()
    var showResetDialog by remember { mutableStateOf(false) }
    var showImportDialog by remember { mutableStateOf(false) }
    var importInput by remember { mutableStateOf("") }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(BgPrimary)
            .verticalScroll(rememberScrollState())
            .padding(start = 16.dp, end = 16.dp, top = 12.dp, bottom = 90.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Section 1: Appearance & Theme (Rounded 3D Card Group)
        Column {
            Text(
                text = "外観 ＆ テーマ",
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                color = TextSub,
                modifier = Modifier.padding(start = 8.dp, bottom = 6.dp)
            )
            Surface(
                shape = RoundedCornerShape(20.dp),
                color = CardBg,
                shadowElevation = 2.dp,
                modifier = Modifier
                    .fillMaxWidth()
                    .border(1.dp, CardBorder, RoundedCornerShape(20.dp))
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text("テーマ設定", fontSize = 14.sp, fontWeight = FontWeight.SemiBold, color = TextMain)
                    Text(
                        "暗いカラオケ部屋では「ダーク (True Black)」が眩しくなく快適です",
                        fontSize = 11.sp,
                        color = TextSub,
                        modifier = Modifier.padding(top = 2.dp)
                    )
                    Row(
                        modifier = Modifier.padding(top = 10.dp),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        listOf(
                            "SYSTEM" to "システム連動",
                            "DARK" to "ダーク (漆黒)",
                            "LIGHT" to "ライト"
                        ).forEach { (mode, label) ->
                            val isSel = currentThemeMode == mode
                            Surface(
                                shape = RoundedCornerShape(14.dp),
                                color = if (isSel) TextMain else BgSecondary,
                                shadowElevation = if (isSel) 2.dp else 0.dp,
                                modifier = Modifier
                                    .border(1.dp, if (isSel) TextMain else CardBorder, RoundedCornerShape(14.dp))
                                    .clickable { onThemeModeChange(mode) }
                            ) {
                                Text(
                                    text = label,
                                    fontSize = 12.sp,
                                    fontWeight = if (isSel) FontWeight.Bold else FontWeight.Medium,
                                    color = if (isSel) BgPrimary else TextSub,
                                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                                )
                            }
                        }
                    }
                }
            }
        }

        // Section 2: Environment & Machine Settings (Rounded 3D Card Group)
        Column {
            Text(
                text = "環境・機種設定",
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                color = TextSub,
                modifier = Modifier.padding(start = 8.dp, bottom = 6.dp)
            )
            Surface(
                shape = RoundedCornerShape(20.dp),
                color = CardBg,
                shadowElevation = 2.dp,
                modifier = Modifier
                    .fillMaxWidth()
                    .border(1.dp, CardBorder, RoundedCornerShape(20.dp))
            ) {
                Column {
                    // Default Machine
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text("いつも使うカラオケ機種 (デフォルト)", fontSize = 14.sp, fontWeight = FontWeight.SemiBold, color = TextMain)
                        Text("歌唱モード開始時の初期選択になります", fontSize = 11.sp, color = TextSub, modifier = Modifier.padding(top = 2.dp))
                        Row(
                            modifier = Modifier.padding(top = 10.dp),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            listOf("DAM", "JOYSOUND").forEach { m ->
                                val isSel = currentDefaultMachine.contains(m)
                                Surface(
                                    shape = RoundedCornerShape(14.dp),
                                    color = if (isSel) TextMain else BgSecondary,
                                    shadowElevation = if (isSel) 2.dp else 0.dp,
                                    modifier = Modifier
                                        .border(1.dp, if (isSel) TextMain else CardBorder, RoundedCornerShape(14.dp))
                                        .clickable { onDefaultMachineChange(m) }
                                ) {
                                    Text(
                                        text = m,
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = if (isSel) BgPrimary else TextSub,
                                        modifier = Modifier.padding(horizontal = 16.dp, vertical = 6.dp)
                                    )
                                }
                            }
                        }
                    }

                    Box(modifier = Modifier.fillMaxWidth().height(1.dp).background(CardBorder))

                    // 3:00 AM Auto Reset Toggle
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f).padding(end = 12.dp)) {
                            Text("深夜3時の自動セッションリセット", fontSize = 13.sp, fontWeight = FontWeight.SemiBold, color = TextMain)
                            Text(
                                "深夜3時を過ぎると、前回のカラオケセッションを自動終了して初期の「Start Karaoke」画面に戻します（退室忘れ防止）",
                                fontSize = 11.sp,
                                color = TextSub,
                                modifier = Modifier.padding(top = 2.dp)
                            )
                        }
                        Switch(
                            checked = autoResetAt3Am,
                            onCheckedChange = { isChecked ->
                                onAutoResetChange(isChecked)
                                val msg = if (isChecked) "深夜3時自動リセットを有効にしました" else "深夜3時自動リセットを無効にしました"
                                Toast.makeText(context, msg, Toast.LENGTH_SHORT).show()
                            },
                            colors = SwitchDefaults.colors(
                                checkedThumbColor = BgPrimary,
                                checkedTrackColor = TextMain,
                                uncheckedThumbColor = TextSub,
                                uncheckedTrackColor = BgSecondary
                            )
                        )
                    }

                    Box(modifier = Modifier.fillMaxWidth().height(1.dp).background(CardBorder))

                    // Session Reset
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable {
                                onResetSession()
                            }
                            .padding(16.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text("今日のカラオケセッションを終了", fontSize = 13.sp, fontWeight = FontWeight.SemiBold, color = TextMain)
                            Text("本日の歌唱履歴を締め切り、初期画面に戻ります", fontSize = 11.sp, color = TextSub, modifier = Modifier.padding(top = 2.dp))
                        }
                        Surface(
                            shape = RoundedCornerShape(12.dp),
                            color = BgSecondary,
                            modifier = Modifier.border(1.dp, CardBorder, RoundedCornerShape(12.dp))
                        ) {
                            Text("終了", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = TextMain, modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp))
                        }
                    }
                }
            }
        }

        // Section 3: Data & Library (Rounded 3D Card Group)
        Column {
            Text(
                text = "ライブラリ ＆ バックアップ",
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                color = TextSub,
                modifier = Modifier.padding(start = 8.dp, bottom = 6.dp)
            )
            Surface(
                shape = RoundedCornerShape(20.dp),
                color = CardBg,
                shadowElevation = 2.dp,
                modifier = Modifier
                    .fillMaxWidth()
                    .border(1.dp, CardBorder, RoundedCornerShape(20.dp))
            ) {
                Column {
                    // Seed Popular Ranking Songs
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { onSeedPopularSongs() }
                            .padding(16.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text("カラオケ人気定番曲 (35曲) を一括追加", fontSize = 13.sp, fontWeight = FontWeight.SemiBold, color = TextMain)
                            Text("怪獣の花唄、ドライフラワー、残酷な天使のテーゼ等の人気曲を登録", fontSize = 11.sp, color = TextSub, modifier = Modifier.padding(top = 2.dp))
                        }
                        Surface(
                            shape = RoundedCornerShape(12.dp),
                            color = BgSecondary,
                            modifier = Modifier.border(1.dp, CardBorder, RoundedCornerShape(12.dp))
                        ) {
                            Text("追加", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = TextMain, modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp))
                        }
                    }

                    Box(modifier = Modifier.fillMaxWidth().height(1.dp).background(CardBorder))

                    // Export JSON
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable {
                                coroutineScope.launch {
                                    val json = onExportJson()
                                    val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                                    val clip = ClipData.newPlainText("KaraLog Backup", json)
                                    clipboard.setPrimaryClip(clip)
                                    Toast.makeText(context, "全データをクリップボードにコピーしました", Toast.LENGTH_SHORT).show()
                                }
                            }
                            .padding(16.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text("データをJSONで書き出し (エクスポート)", fontSize = 13.sp, fontWeight = FontWeight.SemiBold, color = TextMain)
                            Text("全楽曲・スコア履歴をクリップボードにコピーします", fontSize = 11.sp, color = TextSub, modifier = Modifier.padding(top = 2.dp))
                        }
                        Surface(
                            shape = RoundedCornerShape(12.dp),
                            color = BgSecondary,
                            modifier = Modifier.border(1.dp, CardBorder, RoundedCornerShape(12.dp))
                        ) {
                            Text("コピー", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = TextMain, modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp))
                        }
                    }

                    Box(modifier = Modifier.fillMaxWidth().height(1.dp).background(CardBorder))

                    // Import JSON
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { showImportDialog = true }
                            .padding(16.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text("JSONからデータを復元 (インポート)", fontSize = 13.sp, fontWeight = FontWeight.SemiBold, color = TextMain)
                            Text("以前書き出したJSONから楽曲と履歴を復元します", fontSize = 11.sp, color = TextSub, modifier = Modifier.padding(top = 2.dp))
                        }
                        Surface(
                            shape = RoundedCornerShape(12.dp),
                            color = BgSecondary,
                            modifier = Modifier.border(1.dp, CardBorder, RoundedCornerShape(12.dp))
                        ) {
                            Text("復元", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = TextMain, modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp))
                        }
                    }

                    Box(modifier = Modifier.fillMaxWidth().height(1.dp).background(CardBorder))

                    // Reset All Data
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { showResetDialog = true }
                            .padding(16.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text("全データを初期化", fontSize = 13.sp, fontWeight = FontWeight.SemiBold, color = TextMain)
                            Text("登録された曲とすべての履歴を完全に削除します", fontSize = 11.sp, color = TextSub, modifier = Modifier.padding(top = 2.dp))
                        }
                        Surface(
                            shape = RoundedCornerShape(12.dp),
                            color = BgSecondary,
                            modifier = Modifier.border(1.dp, CardBorder, RoundedCornerShape(12.dp))
                        ) {
                            Text("初期化", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = TextMain, modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp))
                        }
                    }
                }
            }
        }

        // Section 4: Storage Info (Rounded 3D Card)
        Column {
            Text(
                text = "ストレージ ＆ プライバシー",
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                color = TextSub,
                modifier = Modifier.padding(start = 8.dp, bottom = 6.dp)
            )
            Surface(
                shape = RoundedCornerShape(20.dp),
                color = CardBg,
                shadowElevation = 2.dp,
                modifier = Modifier
                    .fillMaxWidth()
                    .border(1.dp, CardBorder, RoundedCornerShape(20.dp))
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text("完全ローカル・プライベート保護", fontSize = 13.sp, fontWeight = FontWeight.SemiBold, color = TextMain)
                    Text(
                        "登録曲: $totalSongsCount 曲 / 歌唱履歴: $totalRecordsCount 件\n" +
                                "データは端末内のSQLite/Roomにのみ保存され、外部通信なし・サーバー代0円・登録不要で利用可能です。",
                        fontSize = 11.sp,
                        color = TextSub,
                        lineHeight = 16.sp,
                        modifier = Modifier.padding(top = 2.dp)
                    )
                }
            }
        }

        // App Footer
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 32.dp),
            contentAlignment = Alignment.Center
        ) {
            Text("KaraLog v1.9.0 · Tactile & Minimal Karaoke Assistant", fontSize = 11.sp, color = IconInactive)
        }
    }

    // Import Dialog (Rounded 3D Dialog)
    if (showImportDialog) {
        AlertDialog(
            onDismissRequest = { showImportDialog = false },
            shape = RoundedCornerShape(24.dp),
            title = { Text("バックアップJSONの復元", fontSize = 15.sp, fontWeight = FontWeight.Bold) },
            text = {
                Column {
                    Text("コピーしたJSONテキストを貼り付けてください：", fontSize = 11.sp, color = TextSub)
                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedTextField(
                        value = importInput,
                        onValueChange = { importInput = it },
                        modifier = Modifier.fillMaxWidth().height(120.dp),
                        shape = RoundedCornerShape(12.dp),
                        placeholder = { Text("{\"version\":\"1.9.0\"...}", fontSize = 10.sp) }
                    )
                }
            },
            confirmButton = {
                Surface(
                    shape = RoundedCornerShape(18.dp),
                    color = TextMain,
                    shadowElevation = 2.dp,
                    modifier = Modifier
                        .clip(RoundedCornerShape(18.dp))
                        .clickable {
                            coroutineScope.launch {
                                val success = onImportJson(importInput)
                                if (success) {
                                    Toast.makeText(context, "データを復元しました", Toast.LENGTH_SHORT).show()
                                    showImportDialog = false
                                } else {
                                    Toast.makeText(context, "JSON形式が正しくありません", Toast.LENGTH_LONG).show()
                                }
                            }
                        }
                ) {
                    Text("復元実行", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = BgPrimary, modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp))
                }
            },
            dismissButton = {
                TextButton(onClick = { showImportDialog = false }) {
                    Text("キャンセル", fontSize = 12.sp, color = TextSub)
                }
            },
            containerColor = CardBg
        )
    }

    // Reset Confirm Dialog (Rounded 3D Dialog)
    if (showResetDialog) {
        AlertDialog(
            onDismissRequest = { showResetDialog = false },
            shape = RoundedCornerShape(24.dp),
            title = { Text("全データの初期化", fontSize = 15.sp, fontWeight = FontWeight.Bold) },
            text = {
                Text("登録したすべての曲と歌唱記録が削除されます。本当によろしいですか？", fontSize = 12.sp, color = TextSub)
            },
            confirmButton = {
                Surface(
                    shape = RoundedCornerShape(18.dp),
                    color = TextMain,
                    shadowElevation = 2.dp,
                    modifier = Modifier
                        .clip(RoundedCornerShape(18.dp))
                        .clickable {
                            onResetAllData()
                            Toast.makeText(context, "すべてのデータを初期化しました", Toast.LENGTH_SHORT).show()
                            showResetDialog = false
                        }
                ) {
                    Text("初期化する", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = BgPrimary, modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp))
                }
            },
            dismissButton = {
                TextButton(onClick = { showResetDialog = false }) {
                    Text("キャンセル", fontSize = 12.sp, color = TextSub)
                }
            },
            containerColor = CardBg
        )
    }
}
