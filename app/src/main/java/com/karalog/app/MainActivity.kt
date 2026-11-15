package com.karalog.app

import android.content.Context
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import com.karalog.app.data.local.entity.SongEntity
import com.karalog.app.ui.components.*
import com.karalog.app.ui.screens.history.HistoryScreen
import com.karalog.app.ui.screens.home.HomeScreen
import com.karalog.app.ui.screens.search.SearchScreen
import com.karalog.app.ui.screens.settings.SettingsScreen
import com.karalog.app.ui.screens.start.StartScreen
import com.karalog.app.ui.theme.BgPrimary
import com.karalog.app.ui.theme.KaraLogTheme

data class ScoreResultData(
    val songTitle: String,
    val score: Double,
    val previousScore: Double?,
    val maxScoreBefore: Double?
)

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        val app = application as KaraLogApplication
        val repository = app.repository
        val prefs = applicationContext.getSharedPreferences(MainViewModel.PREFS_NAME, Context.MODE_PRIVATE)

        setContent {
            val viewModel: MainViewModel = viewModel(factory = MainViewModelFactory(repository, prefs))
            val themeMode by viewModel.themeMode.collectAsState()
            val isDark = when (themeMode) {
                "DARK" -> true
                "LIGHT" -> false
                else -> isSystemInDarkTheme()
            }

            KaraLogTheme(darkTheme = isDark) {
                val songsWithStats by viewModel.songsWithStats.collectAsState()
                val keptSongs by viewModel.keptSongs.collectAsState()
                val recordsWithSong by viewModel.recordsWithSong.collectAsState()
                val sessionStartTime by viewModel.sessionStartTime.collectAsState()
                val sessionRecords by viewModel.sessionRecords.collectAsState()
                val isSessionActive by viewModel.isSessionActive.collectAsState()
                val autoResetAt3Am by viewModel.autoResetAt3Am.collectAsState()
                val selectedMachine by viewModel.selectedMachine.collectAsState()

                var currentTab by remember { mutableStateOf(BottomTab.HOME) }
                var activeSingingSong by remember { mutableStateOf<SongEntity?>(null) }
                var showScoreInputSheet by remember { mutableStateOf(false) }
                var scoreResultData by remember { mutableStateOf<ScoreResultData?>(null) }
                var showFinishDialog by remember { mutableStateOf(false) }
                var isBrowsingLibrary by remember { mutableStateOf(false) }
                var currentMachine by remember { mutableStateOf(selectedMachine) }
                var currentKeyOffset by remember { mutableStateOf(0) }

                // 機種設定の初期同期
                LaunchedEffect(selectedMachine) {
                    currentMachine = selectedMachine
                }

                // 起動時の深夜3時自動リセット判定
                LaunchedEffect(Unit) {
                    val didReset = viewModel.check3AmAutoReset()
                    if (didReset) {
                        Toast.makeText(
                            this@MainActivity,
                            "深夜3時を過ぎたため、前回のセッションをリセットしました",
                            Toast.LENGTH_LONG
                        ).show()
                    }
                }

                // セッション未開始かつ閲覧モードでもない場合は「Start Karaoke」画面を表示
                if (!isSessionActive && !isBrowsingLibrary) {
                    StartScreen(
                        initialMachine = if (selectedMachine.contains("JOY")) "JOYSOUND (分析採点Ai)" else "DAM (精密採点Ai)",
                        onStartSession = { machine ->
                            viewModel.startSession(machine)
                            currentMachine = machine
                            isBrowsingLibrary = false
                            currentTab = BottomTab.HOME
                        },
                        onSkipToLibrary = {
                            isBrowsingLibrary = true
                            currentTab = BottomTab.HOME
                        }
                    )
                } else {
                    Scaffold(
                        topBar = {
                            HeaderBar(
                                onFilterClick = {
                                    currentTab = BottomTab.SETTINGS
                                },
                                onFinishClick = if (isSessionActive) {
                                    { showFinishDialog = true }
                                } else null
                            )
                        },
                        contentWindowInsets = WindowInsets(0, 0, 0, 0),
                        containerColor = BgPrimary
                    ) { innerPadding ->
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(top = innerPadding.calculateTopPadding())
                                .background(BgPrimary)
                        ) {
                            when (currentTab) {
                                BottomTab.HOME -> HomeScreen(
                                    recommendedSongs = songsWithStats,
                                    keptSongs = songsWithStats.filter { it.isKept },
                                    rankingRecords = recordsWithSong,
                                    onSongClick = { song ->
                                        activeSingingSong = song
                                        currentKeyOffset = song.defaultKey
                                        currentTab = BottomTab.SING
                                    },
                                    onKeepToggle = { song ->
                                        viewModel.toggleKept(song.id, !song.isKept)
                                    },
                                    onSingClick = { song ->
                                        activeSingingSong = song
                                        currentKeyOffset = song.defaultKey
                                        currentTab = BottomTab.SING
                                    },
                                    onSearchClick = { currentTab = BottomTab.SEARCH },
                                    onBestClick = { currentTab = BottomTab.SEARCH },
                                    onAddClick = {
                                        val newNum = songsWithStats.size + 1
                                        viewModel.quickAddAndGetSong("新規楽曲 " + newNum) { song ->
                                            activeSingingSong = song
                                            currentKeyOffset = 0
                                            currentTab = BottomTab.SING
                                        }
                                    }
                                )

                                BottomTab.SEARCH -> SearchScreen(
                                    songsWithStats = songsWithStats,
                                    onSongClick = { song ->
                                        activeSingingSong = song
                                        currentKeyOffset = song.defaultKey
                                        currentTab = BottomTab.SING
                                    },
                                    onKeepToggle = { song ->
                                        viewModel.toggleKept(song.id, !song.isKept)
                                    },
                                    onSingClick = { song ->
                                        activeSingingSong = song
                                        currentKeyOffset = song.defaultKey
                                        currentTab = BottomTab.SING
                                    },
                                    onAddOrEditSong = { song ->
                                        viewModel.saveSong(song)
                                    }
                                )

                                BottomTab.SING -> NowSingingView(
                                    song = activeSingingSong,
                                    currentKey = currentKeyOffset,
                                    selectedMachine = currentMachine,
                                    onKeyChange = { currentKeyOffset = it },
                                    onMachineChange = {
                                        currentMachine = it
                                        viewModel.setSelectedMachine(it)
                                    },
                                    onInputScoreClick = { showScoreInputSheet = true },
                                    onSongChangeClick = { currentTab = BottomTab.SEARCH },
                                    onCancelClick = {
                                        activeSingingSong = null
                                        currentTab = BottomTab.HOME
                                    }
                                )

                                BottomTab.HISTORY -> HistoryScreen(
                                    records = recordsWithSong,
                                    sessionStartTime = sessionStartTime,
                                    onDeleteRecord = { recordId ->
                                        viewModel.deleteRecord(recordId)
                                    }
                                )

                                BottomTab.SETTINGS -> SettingsScreen(
                                    totalSongsCount = songsWithStats.size,
                                    totalRecordsCount = recordsWithSong.size,
                                    currentDefaultMachine = currentMachine,
                                    onDefaultMachineChange = {
                                        currentMachine = it
                                        viewModel.setSelectedMachine(it)
                                    },
                                    autoResetAt3Am = autoResetAt3Am,
                                    onAutoResetChange = {
                                        viewModel.setAutoResetAt3Am(it)
                                    },
                                    currentThemeMode = themeMode,
                                    onThemeModeChange = { viewModel.setThemeMode(it) },
                                    onResetSession = {
                                        showFinishDialog = true
                                    },
                                    onSeedPopularSongs = {
                                        viewModel.seedPopularRankingSongs { added ->
                                            Toast.makeText(
                                                this@MainActivity,
                                                "カラオケ人気曲をライブラリに追加しました (${added}曲)",
                                                Toast.LENGTH_SHORT
                                            ).show()
                                        }
                                    },
                                    onResetAllData = {
                                        viewModel.resetAllData()
                                        activeSingingSong = null
                                        isBrowsingLibrary = false
                                    },
                                    onExportJson = {
                                        viewModel.exportJson()
                                    },
                                    onImportJson = { json ->
                                        viewModel.importJson(json)
                                    }
                                )
                            }

                            // スコア入力テンキーシート
                            if (showScoreInputSheet) {
                                ScoreInputSheet(
                                    songTitle = activeSingingSong?.title ?: "フリー歌唱",
                                    onScoreSubmit = { score, pitch, notes ->
                                        val currentSong = activeSingingSong
                                        val stat = songsWithStats.find { it.song.id == currentSong?.id }
                                        val maxBefore = stat?.maxScore
                                        val prevScore = stat?.lastScore

                                        val songToRecord = currentSong ?: SongEntity(title = "フリー歌唱")
                                        viewModel.recordScore(
                                            song = songToRecord,
                                            score = score,
                                            pitchAccuracy = pitch,
                                            techniqueNotes = notes,
                                            keyChange = currentKeyOffset,
                                            machineType = currentMachine,
                                            memo = ""
                                        )

                                        showScoreInputSheet = false
                                        scoreResultData = ScoreResultData(
                                            songTitle = songToRecord.title,
                                            score = score,
                                            previousScore = prevScore,
                                            maxScoreBefore = maxBefore
                                        )
                                    },
                                    onDismiss = { showScoreInputSheet = false }
                                )
                            }

                            // スコア結果モーダル
                            scoreResultData?.let { result ->
                                ScoreResultModal(
                                    songTitle = result.songTitle,
                                    score = result.score,
                                    previousScore = result.previousScore,
                                    maxScoreBefore = result.maxScoreBefore,
                                    onReplaySinging = {
                                        scoreResultData = null
                                        currentTab = BottomTab.SING
                                    },
                                    onNextSong = {
                                        scoreResultData = null
                                        activeSingingSong = null
                                        currentTab = BottomTab.HOME
                                    }
                                )
                            }

                            // 真のフローティング・アイランドナビゲーション（背後にコンテンツが回り込み、宙に浮かぶ）
                            BottomNavBar(
                                currentTab = currentTab,
                                onTabSelected = { currentTab = it },
                                modifier = Modifier.align(Alignment.BottomCenter)
                            )

                            // カラオケFinish（終了確認）ダイアログ
                            if (showFinishDialog) {
                                SessionSummaryDialog(
                                    sessionRecords = sessionRecords,
                                    onDismiss = { showFinishDialog = false },
                                    onFinishSession = {
                                        showFinishDialog = false
                                        viewModel.finishSession()
                                        activeSingingSong = null
                                        isBrowsingLibrary = false
                                        Toast.makeText(
                                            this@MainActivity,
                                            "おつかれさまでした！カラオケを終了しました",
                                            Toast.LENGTH_SHORT
                                        ).show()
                                    }
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}
