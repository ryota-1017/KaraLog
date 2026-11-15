package com.karalog.app.ui.screens.start

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Check
import androidx.compose.material.icons.outlined.GraphicEq
import androidx.compose.material.icons.outlined.Mic
import androidx.compose.material.icons.outlined.PlayArrow
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.karalog.app.ui.theme.*
import com.karalog.app.util.HapticHelper

@Composable
fun StartScreen(
    initialMachine: String = "DAM (精密採点Ai)",
    onStartSession: (machine: String) -> Unit,
    onSkipToLibrary: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val isDark = isSystemInDarkTheme()
    var selectedMachine by remember { mutableStateOf(initialMachine) }

    val machines = listOf(
        "DAM (精密採点Ai)" to "DAMの最新Ai採点・繊細な表現力判定",
        "DAM (精密採点DX-G)" to "定番DX-G・ボーナス点＆安定した音程判定",
        "JOYSOUND (分析採点Ai)" to "JOYSOUND最新Ai・高音域＆ロングトーン分析",
        "JOYSOUND (分析採点MAX)" to "テンポの速い曲やボカロ曲・MAXシリーズ"
    )

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(BgPrimary)
            .statusBarsPadding()
            .navigationBarsPadding()
            .padding(horizontal = 20.dp, vertical = 18.dp)
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            // Header & Branding (Rounded & Dimensional)
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.padding(top = 18.dp)
            ) {
                Surface(
                    shape = CircleShape,
                    color = CardBg,
                    shadowElevation = 5.dp,
                    modifier = Modifier
                        .size(72.dp)
                        .border(1.5.dp, CardBorder, CircleShape)
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Icon(
                            imageVector = Icons.Outlined.GraphicEq,
                            contentDescription = null,
                            tint = TextMain,
                            modifier = Modifier.size(34.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = "KARA",
                        fontSize = 28.sp,
                        fontWeight = FontWeight.Black,
                        letterSpacing = 2.sp,
                        color = TextMain
                    )
                    Text(
                        text = "·LOG",
                        fontSize = 28.sp,
                        fontWeight = FontWeight.Light,
                        letterSpacing = 2.sp,
                        color = TextSub
                    )
                }

                Spacer(modifier = Modifier.height(10.dp))

                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = BgSecondary,
                    modifier = Modifier.border(1.dp, CardBorder, RoundedCornerShape(12.dp))
                ) {
                    Text(
                        text = "START KARAOKE SESSION",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 1.5.sp,
                        color = TextSub,
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 4.dp)
                    )
                }

                Spacer(modifier = Modifier.height(6.dp))

                Text(
                    text = "本日のカラオケ機種を選んでスタート",
                    fontSize = 13.sp,
                    color = TextSub
                )
            }

            // Machine Selection (Soft 3D Rounded Cards)
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Text(
                    text = "カラオケ機種を選択",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextSub,
                    modifier = Modifier.padding(start = 6.dp, bottom = 2.dp)
                )

                machines.forEach { (machineName, desc) ->
                    val isSelected = selectedMachine == machineName
                    val isDam = machineName.contains("DAM")

                    Surface(
                        shape = RoundedCornerShape(20.dp),
                        color = if (isSelected) SurfaceElevated else CardBg,
                        border = BorderStroke(
                            width = if (isSelected) 1.5.dp else 1.dp,
                            color = if (isSelected) TextMain else CardBorder
                        ),
                        shadowElevation = if (isSelected) 4.dp else 1.dp,
                        modifier = Modifier.fillMaxWidth(),
                        onClick = {
                            HapticHelper.playClick(context)
                            selectedMachine = machineName
                        }
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 16.dp, vertical = 14.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    // Machine Type Pill
                                    Surface(
                                        shape = RoundedCornerShape(8.dp),
                                        color = if (isSelected) TextMain else BgSecondary,
                                        border = if (isSelected) null else BorderStroke(0.8.dp, CardBorder)
                                    ) {
                                        Text(
                                            text = if (isDam) "DAM" else "JOY",
                                            fontSize = 9.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = if (isSelected) BgPrimary else TextSub,
                                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                        )
                                    }
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text(
                                        text = machineName,
                                        fontSize = 14.sp,
                                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                        color = TextMain
                                    )
                                }
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = desc,
                                    fontSize = 11.sp,
                                    color = TextSub
                                )
                            }

                            // Selection Indicator Circle
                            Surface(
                                shape = CircleShape,
                                color = if (isSelected) TextMain else BgSecondary,
                                shadowElevation = if (isSelected) 2.dp else 0.dp,
                                modifier = Modifier
                                    .size(24.dp)
                                    .border(1.dp, if (isSelected) TextMain else CardBorder, CircleShape)
                            ) {
                                Box(contentAlignment = Alignment.Center) {
                                    if (isSelected) {
                                        Icon(
                                            imageVector = Icons.Outlined.Check,
                                            contentDescription = null,
                                            tint = BgPrimary,
                                            modifier = Modifier.size(14.dp)
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }

            // Bottom Actions: 3D Rounded Start Button
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 8.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Surface(
                    shape = RoundedCornerShape(28.dp),
                    color = TextMain,
                    shadowElevation = 5.dp,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(54.dp)
                        .clip(RoundedCornerShape(28.dp))
                        .clickable {
                            HapticHelper.playHeavyClick(context)
                            onStartSession(selectedMachine)
                        }
                ) {
                    Row(
                        modifier = Modifier.fillMaxSize(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center
                    ) {
                        Icon(
                            imageVector = Icons.Outlined.PlayArrow,
                            contentDescription = null,
                            tint = BgPrimary,
                            modifier = Modifier.size(22.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "カラオケを開始する (Start Karaoke)",
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold,
                            color = BgPrimary
                        )
                    }
                }

                TextButton(
                    onClick = {
                        HapticHelper.playClick(context)
                        onSkipToLibrary()
                    },
                    shape = RoundedCornerShape(20.dp)
                ) {
                    Text(
                        text = "開始せずにライブラリを閲覧",
                        fontSize = 12.sp,
                        color = TextSub
                    )
                }
            }
        }
    }
}
