package com.xiaojianchi.app.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Add
import androidx.compose.material.icons.rounded.AutoAwesome
import androidx.compose.material.icons.rounded.DirectionsWalk
import androidx.compose.material.icons.rounded.MenuBook
import androidx.compose.material.icons.rounded.SelfImprovement
import androidx.compose.material.icons.rounded.Settings
import androidx.compose.material.icons.rounded.Translate
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun JournalBrand(modifier: Modifier = Modifier) {
    Text(
        text = "小坚持",
        modifier = modifier,
        style = MaterialTheme.typography.headlineSmall,
        fontSize = 25.sp,
        color = Ink,
        letterSpacing = 2.sp,
    )
}

@Composable
fun HomeTopBar(
    onAdd: () -> Unit,
    onSettings: () -> Unit,
) {
    Row(
        modifier = Modifier.fillMaxWidth().height(56.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        JournalBrand()
        Spacer(Modifier.weight(1f))
        IconButton(onClick = onAdd) {
            Icon(Icons.Rounded.Add, contentDescription = "新建习惯", tint = Muted)
        }
        IconButton(onClick = onSettings) {
            Icon(Icons.Rounded.Settings, contentDescription = "设置", tint = Muted)
        }
    }
}

@Composable
fun HabitIcon(
    name: String,
    modifier: Modifier = Modifier,
    tint: Color = Color.Unspecified,
) {
    val icon: ImageVector =
        when (name) {
            "move" -> Icons.Rounded.DirectionsWalk
            "language" -> Icons.Rounded.Translate
            "lotus" -> Icons.Rounded.SelfImprovement
            "spark" -> Icons.Rounded.AutoAwesome
            else -> Icons.Rounded.MenuBook
        }
    Icon(icon, contentDescription = null, modifier = modifier, tint = tint)
}

@Composable
fun HabitSymbol(
    name: String,
    color: Color,
) {
    Box(
        modifier = Modifier.size(46.dp).background(color.copy(alpha = 0.13f), RoundedCornerShape(14.dp, 14.dp, 14.dp, 5.dp)),
        contentAlignment = Alignment.Center,
    ) {
        HabitIcon(name = name, modifier = Modifier.size(24.dp), tint = color)
    }
}
