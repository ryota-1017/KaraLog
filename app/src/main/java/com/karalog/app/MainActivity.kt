package com.karalog.app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import com.karalog.app.data.local.entity.SongEntity
import com.karalog.app.ui.components.*
import com.karalog.app.ui.screens.history.HistoryScreen
import com.karalog.app.ui.screens.home.HomeScreen
import com.karalog.app.ui.screens.search.SearchScreen
import com.karalog.app.ui.screens.settings.SettingsScreen
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

        setContent {
            KaraLogTheme {
                val viewModel: MainViewModel = viewModel(factory = MainViewModelFactory(repository))
                val songsWithStats by viewModel.songsWithStats.collectAsState()
                val keptSongs by viewModel.keptSongs.collectAsState()
                val recordsWithSong by viewModel.recordsWithSong.collectAsState()
                val sessionStartTime by viewModel.sessionStartTime.collectAsState()

                var currentTab by remember { mutableStateOf(BottomTab.HOME) }
                var activeSingingSong by remember { mutableStateOf<SongEntity?>(null) }
                var showScoreInputSheet by remember { mutableStateOf(false) }
                var scoreResultData by remember { mutableStateOf<ScoreResultData?>(null) }
                var currentMachine by remember { mutableStateOf("DAM") }
                var currentKeyOffset by remember { mutableStateOf(0) }

                Scaffold(
                    topBar = {
                        HeaderBar(
                            onFilterClick = {
                                currentTab = BottomTab.SETTINGS
                            }
                        )
                    },
                    bottomBar = {
                        BottomNavBar(
                            currentTab = currentTab,
                            onTabSelected = { currentTab = it }
                        )
                    },
                    contentWindowInsets = WindowInsets(0, 0, 0, 0),
                    containerColor = BgPrimary
                ) { innerPadding ->
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(innerPadding)
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
                                onMachineChange = { currentMachine = it },
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
                                onDefaultMachineChange = { currentMachine = it },
                                onResetSession = {
                                    viewModel.resetSession()
                                },
                                onResetAllData = {
                                    viewModel.resetAllData()
                                    activeSingingSong = null
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
                                onScoreSubmit = { score ->
                                    val currentSong = activeSingingSong
                                    val stat = songsWithStats.find { it.song.id == currentSong?.id }
                                    val maxBefore = stat?.maxScore
                                    val prevScore = stat?.lastScore

                                    val songToRecord = currentSong ?: SongEntity(title = "フリー歌唱")
                                    viewModel.recordScore(
                                        song = songToRecord,
                                        score = score,
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
                    }
                }
            }
        }
    }
}
