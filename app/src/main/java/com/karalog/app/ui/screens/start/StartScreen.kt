package com.karalog.app.ui.screens.start

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Check
import androidx.compose.material.icons.outlined.Mic
import androidx.compose.material.icons.outlined.PlayArrow
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.RectangleShape
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
            .padding(horizontal = 24.dp, vertical = 20.dp)
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            // Header & Branding
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.padding(top = 24.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(56.dp)
                        .clip(RoundedCornerShape(16.dp))
                        .background(BgSecondary)
                        .border(1.dp, BorderLight, RoundedCornerShape(16.dp)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Outlined.Mic,
                        contentDescription = null,
                        tint = TextMain,
                        modifier = Modifier.size(28.dp)
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = "KaraLog",
                    fontSize = 28.sp,
                    fontStyle = FontStyle.Italic,
                    fontWeight = FontWeight.Bold,
                    fontFamily = FontFamily.Serif,
                    letterSpacing = (-0.5).sp,
                    color = TextMain
                )

                Spacer(modifier = Modifier.height(6.dp))

                Text(
                    text = "START KARAOKE SESSION",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 2.sp,
                    color = TextSub
                )

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = "本日のカラオケ機種を選んでスタート",
                    fontSize = 13.sp,
                    color = TextSub
                )
            }

            // Machine Selection List
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Text(
                    text = "カラオケ機種を選択",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextSub,
                    modifier = Modifier.padding(start = 4.dp, bottom = 2.dp)
                )

                machines.forEach { (machineName, desc) ->
                    val isSelected = selectedMachine == machineName
                    val isDam = machineName.contains("DAM")

                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(if (isSelected) BgSecondary else BgPrimary)
                            .border(
                                width = if (isSelected) 1.5.dp else 1.dp,
                                color = if (isSelected) TextMain else BorderLight,
                                shape = RoundedCornerShape(10.dp)
                            )
                            .clip(RoundedCornerShape(10.dp))
                            .clickable {
                                HapticHelper.playClick(context)
                                selectedMachine = machineName
                            }
                            .padding(horizontal = 16.dp, vertical = 14.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    // Machine Type Tag
                                    Box(
                                        modifier = Modifier
                                            .background(if (isSelected) TextMain else BgSecondary)
                                            .padding(horizontal = 6.dp, vertical = 2.dp)
                                    ) {
                                        Text(
                                            text = if (isDam) "DAM" else "JOY",
                                            fontSize = 9.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = if (isSelected) BgPrimary else TextSub
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
                                Spacer(modifier = Modifier.height(3.dp))
                                Text(
                                    text = desc,
                                    fontSize = 11.sp,
                                    color = TextSub
                                )
                            }

                            // Selection Indicator
                            Box(
                                modifier = Modifier
                                    .size(22.dp)
                                    .clip(RoundedCornerShape(11.dp))
                                    .background(if (isSelected) TextMain else BgSecondary)
                                    .border(
                                        1.dp,
                                        if (isSelected) TextMain else BorderLight,
                                        RoundedCornerShape(11.dp)
                                    ),
                                contentAlignment = Alignment.Center
                            ) {
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

            // Bottom Actions: Start Button & Skip
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 12.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Button(
                    onClick = {
                        HapticHelper.playHeavyClick(context)
                        onStartSession(selectedMachine)
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = TextMain,
                        contentColor = BgPrimary
                    ),
                    shape = RoundedCornerShape(10.dp)
                ) {
                    Icon(
                        imageVector = Icons.Outlined.PlayArrow,
                        contentDescription = null,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "カラオケを開始する (Start Karaoke)",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold
                    )
                }

                TextButton(
                    onClick = {
                        HapticHelper.playClick(context)
                        onSkipToLibrary()
                    }
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
