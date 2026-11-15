package com.karalog.app.ui.components

import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.karalog.app.ui.theme.*

@Composable
fun StoriesActionTray(
    onSearchClick: () -> Unit,
    onSingClick: () -> Unit,
    onHistoryClick: () -> Unit,
    onAddClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 12.dp, horizontal = 16.dp),
        horizontalArrangement = Arrangement.SpaceAround,
        verticalAlignment = Alignment.CenterVertically
    ) {
        StoryActionItem(icon = Icons.Default.Search, label = "検索", onClick = onSearchClick)
        StoryActionItem(icon = Icons.Default.Mic, label = "即歌唱", onClick = onSingClick)
        StoryActionItem(icon = Icons.Default.Schedule, label = "履歴", onClick = onHistoryClick)
        StoryActionItem(icon = Icons.Default.Add, label = "曲追加", onClick = onAddClick)
    }
}

@Composable
private fun StoryActionItem(
    icon: ImageVector,
    label: String,
    onClick: () -> Unit
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(6.dp),
        modifier = Modifier.clickable { onClick() }
    ) {
        Surface(
            shape = CircleShape,
            color = CardBg,
            shadowElevation = 3.dp,
            modifier = Modifier
                .size(54.dp)
                .border(1.2.dp, CardBorder, CircleShape)
        ) {
            Box(contentAlignment = Alignment.Center) {
                Icon(
                    imageVector = icon,
                    contentDescription = label,
                    tint = TextMain,
                    modifier = Modifier.size(22.dp)
                )
            }
        }

        Text(
            text = label,
            fontSize = 11.sp,
            fontWeight = FontWeight.Medium,
            color = TextSub
        )
    }
}
