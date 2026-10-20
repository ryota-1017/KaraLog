package com.karalog.app.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.karalog.app.ui.theme.*

@Composable
fun HeaderBar(
    onFilterClick: () -> Unit,
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
                .height(48.dp)
                .padding(horizontal = 16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Text(
                    text = "KaraLog",
                    fontSize = 24.sp,
                    fontStyle = FontStyle.Italic,
                    fontWeight = FontWeight.Bold,
                    fontFamily = FontFamily.Cursive,
                    letterSpacing = (-0.5).sp,
                    color = TextMain
                )
                Icon(
                    imageVector = Icons.Default.Mic,
                    contentDescription = null,
                    tint = TextMain,
                    modifier = Modifier.size(19.dp)
                )
            }

            IconButton(
                onClick = onFilterClick,
                modifier = Modifier.size(36.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Tune,
                    contentDescription = "Filter",
                    tint = TextMain,
                    modifier = Modifier.size(22.dp)
                )
            }
        }

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(1.dp)
                .background(BorderLight)
        )
    }
}