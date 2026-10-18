package com.karalog.app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.*
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import com.karalog.app.data.local.entity.SongEntity
import com.karalog.app.ui.components.*
import com.karalog.app.ui.screens.history.HistoryScreen
import com.karalog.app.ui.screens.home.HomeScreen
import com.karalog.app.ui.screens.recommend.RecommendationsScreen
import com.karalog.app.ui.screens.settings.SettingsScreen
import com.karalog.app.ui.screens.start.StartScreen
import com.karalog.app.ui.theme.KaraLogTheme
import com.karalog.app.ui.theme.MidnightBlack

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
                val sessionRecords by viewModel.sessionRecords.collectAsState()

                var isSessionActive by remember { mutableStateOf(false) }
                var currentMachine by remember { mutableStateOf("DAM (\u7cbe\u5bc6\u63a1\u70b9Ai)") }
                var currentTab by remember { mutableStateOf(NavTab.HOME) }

                var activeSingingSong by remember { mutableStateOf<SongEntity?>(null) }
                var showScoreInputSheet by remember { mutableStateOf(false) }
                var lastResultData by remember { mutableStateOf<Triple<String, Double, Double?>?>(null) }
                var showSessionSummaryDialog by remember { mutableStateOf(false) }

                if (!isSessionActive) {
                    StartScreen(
                        onStartSession = { machine ->
                            currentMachine = machine
                            viewModel.resetSession()
                            isSessionActive = true
                            currentTab = NavTab.HOME
                        }
                    )
                } else if (activeSingingSong != null) {
                    NowSingingView(
                        song = activeSingingSong!!,
                        currentMachine = currentMachine,
                        onCancel = { activeSingingSong = null },
                        onInputScore = { showScoreInputSheet = true },
                        onKeyChange = { newKey ->
                            activeSingingSong = activeSingingSong!!.copy(defaultKey = newKey)
                            viewModel.updateSongKey(activeSingingSong!!.id, newKey)
                        }
                    )
                } else {
                    Scaffold(
                        topBar = { HeaderBar(currentMachine = currentMachine) },
                        bottomBar = {
                            BottomNavBar(
                                selectedTab = currentTab,
                                onTabSelected = { tab ->
                                    if (tab == NavTab.END_SESSION) {
                                        showSessionSummaryDialog = true
                                    } else {
                                        currentTab = tab
                                    }
                                }
                            )
                        },
                        containerColor = MidnightBlack
                    ) { innerPadding ->
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(innerPadding)
                        ) {
                            AnimatedContent(
                                targetState = currentTab,
                                transitionSpec = {
                                    if (targetState.ordinal > initialState.ordinal) {
                                        slideInHorizontally(animationSpec = tween(280)) { width -> width / 3 } + fadeIn(animationSpec = tween(280)) togetherWith
                                        slideOutHorizontally(animationSpec = tween(280)) { width -> -width / 3 } + fadeOut(animationSpec = tween(280))
                                    } else {
                                        slideInHorizontally(animationSpec = tween(280)) { width -> -width / 3 } + fadeIn(animationSpec = tween(280)) togetherWith
                                        slideOutHorizontally(animationSpec = tween(280)) { width -> width / 3 } + fadeOut(animationSpec = tween(280))
                                    }
                                },
                                label = "ScreenTransition"
                            ) { tab ->
                                when (tab) {
                                    NavTab.HOME -> HomeScreen(
                                        songsWithStats = songsWithStats,
                                        keptSongs = keptSongs,
                                        onStartSinging = { song -> activeSingingSong = song },
                                        onKeyChange = { id, key -> viewModel.updateSongKey(id, key) },
                                        onToggleKept = { id, kept -> viewModel.toggleKept(id, kept) },
                                        onQuickAddSong = { title ->
                                            viewModel.quickAddAndGetSong(title) { newSong ->
                                                activeSingingSong = newSong
                                            }
                                        }
                                    )
                                    NavTab.RECOMMEND -> RecommendationsScreen(
                                        songsWithStats = songsWithStats,
                                        onStartSinging = { song -> activeSingingSong = song },
                                        onKeyChange = { id, key -> viewModel.updateSongKey(id, key) },
                                        onToggleKept = { id, kept -> viewModel.toggleKept(id, kept) }
                                    )
                                    NavTab.HISTORY -> HistoryScreen(
                                        records = recordsWithSong,
                                        songsWithStats = songsWithStats
                                    )
                                    NavTab.SETTINGS -> SettingsScreen(
                                        onResetData = { viewModel.resetAllData() }
                                    )
                                    NavTab.END_SESSION -> {}
                                }
                            }
                        }
                    }
                }

                if (showScoreInputSheet && activeSingingSong != null) {
                    val currentSong = activeSingingSong!!
                    val prevMaxScore = songsWithStats.find { it.id == currentSong.id }?.maxScore

                    ScoreInputSheet(
                        songTitle = currentSong.title,
                        onDismissRequest = { showScoreInputSheet = false },
                        onSubmit = { score, memo ->
                            viewModel.recordScore(
                                song = currentSong,
                                score = score,
                                keyChange = currentSong.defaultKey,
                                machineType = currentMachine,
                                memo = memo
                            )
                            showScoreInputSheet = false
                            lastResultData = Triple(currentSong.title, score, prevMaxScore)
                        }
                    )
                }

                if (lastResultData != null) {
                    val (title, score, prevScore) = lastResultData!!
                    ScoreResultModal(
                        songTitle = title,
                        score = score,
                        previousScore = prevScore,
                        maxScoreBefore = prevScore,
                        onNextSong = {
                            lastResultData = null
                            activeSingingSong = null
                            currentTab = NavTab.HOME
                        },
                        onReplaySinging = {
                            lastResultData = null
                        }
                    )
                }

                if (showSessionSummaryDialog) {
                    SessionSummaryDialog(
                        sessionRecords = sessionRecords,
                        onFinishSession = {
                            showSessionSummaryDialog = false
                            isSessionActive = false
                            activeSingingSong = null
                            lastResultData = null
                            viewModel.resetSession()
                        }
                    )
                }
            }
        }
    }
}