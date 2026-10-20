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
import com.karalog.app.ui.screens.settings.SettingsScreen
import com.karalog.app.ui.theme.BgPrimary
import com.karalog.app.ui.theme.KaraLogTheme

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

                var currentTab by remember { mutableStateOf(BottomTab.HOME) }
                var activeSingingSong by remember { mutableStateOf<SongEntity?>(null) }
                var showScoreInputSheet by remember { mutableStateOf(false) }
                var currentMachine by remember { mutableStateOf("DAM") }
                var currentKeyOffset by remember { mutableStateOf(0) }

                Scaffold(
                    topBar = {
                        HeaderBar(
                            onAddClick = {
                                viewModel.quickAddAndGetSong("譁ｰ隕乗･ｽ譖ｲ " + (songsWithStats.size + 1)) { song ->
                                    activeSingingSong = song
                                }
                            },
                            onToggleLoading = { /* skeleton toggle */ }
                        )
                    },
                    bottomBar = {
                        BottomNavBar(
                            currentTab = currentTab,
                            onTabSelected = { currentTab = it }
                        )
                    },
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
                                keptSongs = songsWithStats.filter { it.isKept },
                                allSongs = songsWithStats,
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
                                }
                            )

                            BottomTab.SEARCH -> HomeScreen(
                                keptSongs = emptyList(),
                                allSongs = songsWithStats,
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
                                }
                            )

                            BottomTab.SING -> NowSingingView(
                                song = activeSingingSong,
                                currentKey = currentKeyOffset,
                                selectedMachine = currentMachine,
                                onKeyChange = { currentKeyOffset = it },
                                onMachineChange = { currentMachine = it },
                                onInputScoreClick = { showScoreInputSheet = true },
                                onCancelClick = {
                                    activeSingingSong = null
                                    currentTab = BottomTab.HOME
                                }
                            )

                            BottomTab.HISTORY -> HistoryScreen(
                                records = recordsWithSong
                            )

                            BottomTab.SETTINGS -> SettingsScreen()
                        }

                        if (showScoreInputSheet) {
                            ScoreInputSheet(
                                songTitle = activeSingingSong?.title ?: "繝輔Μ繝ｼ豁悟罰",
                                onScoreSubmit = { score ->
                                    val song = activeSingingSong ?: SongEntity(title = "繝輔Μ繝ｼ豁悟罰")
                                    viewModel.recordScore(
                                        song = song,
                                        score = score,
                                        keyChange = currentKeyOffset,
                                        machineType = currentMachine, memo = ""
                                    )
                                    showScoreInputSheet = false
                                    activeSingingSong = null
                                    currentTab = BottomTab.HISTORY
                                },
                                onDismiss = { showScoreInputSheet = false }
                            )
                        }
                    }
                }
            }
        }
    }
}

