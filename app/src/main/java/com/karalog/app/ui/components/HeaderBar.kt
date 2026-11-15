package com.karalog.app.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Mic
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
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.karalog.app.ui.theme.*

@Composable
fun HeaderBar(
    onFilterClick: () -> Unit,
    onFinishClick: (() -> Unit)? = null,
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
                .height(56.dp)
                .padding(horizontal = 16.dp, vertical = 6.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            // App Brand Pill
            Surface(
                shape = RoundedCornerShape(20.dp),
                color = CardBg,
                shadowElevation = 2.dp,
                modifier = Modifier.border(1.dp, CardBorder, RoundedCornerShape(20.dp))
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Text(
                        text = "KaraLog",
                        fontSize = 18.sp,
                        fontStyle = FontStyle.Italic,
                        fontWeight = FontWeight.Bold,
                        fontFamily = FontFamily.Serif,
                        letterSpacing = (-0.5).sp,
                        color = TextMain
                    )
                    Box(
                        modifier = Modifier
                            .size(20.dp)
                            .clip(CircleShape)
                            .background(BgSecondary),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Outlined.Mic,
                            contentDescription = null,
                            tint = TextMain,
                            modifier = Modifier.size(12.dp)
                        )
                    }
                }
            }

            // Right Action Buttons (Rounded 3D Icon Buttons)
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                // カラオケFinish（終了）ロゴ（立体的な丸ボタン）
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
