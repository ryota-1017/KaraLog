package com.karalog.app.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.GraphicEq
import androidx.compose.material.icons.outlined.Mic
import androidx.compose.material.icons.outlined.PlayArrow
import androidx.compose.material.icons.outlined.StopCircle
import androidx.compose.material.icons.outlined.Tune
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.karalog.app.ui.theme.*

@Composable
fun HeaderBar(
    onFilterClick: () -> Unit,
    onFinishClick: (() -> Unit)? = null,
    onStartClick: (() -> Unit)? = null,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .background(BgPrimary)
            .statusBarsPadding()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(58.dp)
                .padding(horizontal = 16.dp, vertical = 6.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            // Refreshed Modern Aesthetic Logo Pill
            Surface(
                shape = RoundedCornerShape(22.dp),
                color = CardBg,
                shadowElevation = 2.dp,
                modifier = Modifier.border(1.dp, CardBorder, RoundedCornerShape(22.dp))
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 14.dp, vertical = 7.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    // Modern Soundwave / Mic Emblem
                    Surface(
                        shape = CircleShape,
                        color = TextMain,
                        modifier = Modifier.size(24.dp)
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Icon(
                                imageVector = Icons.Outlined.GraphicEq,
                                contentDescription = null,
                                tint = BgPrimary,
                                modifier = Modifier.size(15.dp)
                            )
                        }
                    }

                    // Modern Sleek Typography
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = "KARA",
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Black,
                            letterSpacing = 1.2.sp,
                            color = TextMain
                        )
                        Text(
                            text = "·LOG",
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Light,
                            letterSpacing = 1.2.sp,
                            color = TextSub
                        )
                    }
                }
            }

            // Right Action Buttons
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                // ライブラリ閲覧中：Startボタン（カラオケを開始する）
                if (onStartClick != null) {
                    Surface(
                        shape = RoundedCornerShape(18.dp),
                        color = TextMain,
                        shadowElevation = 3.dp,
                        modifier = Modifier
                            .height(38.dp)
                            .clip(RoundedCornerShape(18.dp))
                            .clickable { onStartClick() }
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 12.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Outlined.PlayArrow,
                                contentDescription = "Start Karaoke",
                                tint = BgPrimary,
                                modifier = Modifier.size(18.dp)
                            )
                            Text(
                                text = "Start",
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold,
                                color = BgPrimary
                            )
                        }
                    }
                }

                // セッション中：Finishロゴ（カラオケを終了する）
                if (onFinishClick != null) {
                    Surface(
                        shape = CircleShape,
                        color = CardBg,
                        shadowElevation = 2.dp,
                        modifier = Modifier
                            .size(38.dp)
                            .border(1.dp, CardBorder, CircleShape)
                            .clickable { onFinishClick() }
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Icon(
                                imageVector = Icons.Outlined.StopCircle,
                                contentDescription = "Finish Karaoke",
                                tint = TextMain,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                    }
                }

                // 設定ロゴ（立体的な丸ボタン）
                Surface(
                    shape = CircleShape,
                    color = CardBg,
                    shadowElevation = 2.dp,
                    modifier = Modifier
                        .size(38.dp)
                        .border(1.dp, CardBorder, CircleShape)
                        .clickable { onFilterClick() }
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Icon(
                            imageVector = Icons.Outlined.Tune,
                            contentDescription = "Settings",
                            tint = TextMain,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }
            }
        }
    }
}
