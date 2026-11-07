package com.xiaojianchi.app.ui

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Add
import androidx.compose.material.icons.rounded.Check
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedIconButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.xiaojianchi.app.data.HabitWithCheckIns
import com.xiaojianchi.app.data.calculateHabitStats
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.Locale

@Composable
fun HomeScreen(
    habits: List<HabitWithCheckIns>,
    today: LocalDate,
    onAdd: () -> Unit,
    onSettings: () -> Unit,
    onToggle: (Long) -> Unit,
    onOpenHistory: (Long) -> Unit,
) {
    val haptic = LocalHapticFeedback.current
    val completed = habits.count { item -> item.checkIns.any { it.date == today } }
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding =
            androidx.compose.foundation.layout
                .PaddingValues(start = 20.dp, end = 20.dp, bottom = 18.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp),
    ) {
        item { HomeTopBar(onAdd = onAdd, onSettings = onSettings) }
        item { JournalHero(today = today, completed = completed, total = habits.size) }
        item {
            Row(Modifier.fillMaxWidth().padding(top = 4.dp), verticalAlignment = Alignment.CenterVertically) {
                Text("今天的小坚持", style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.SemiBold)
                Spacer(Modifier.weight(1f))
                Text("按自己的节奏来", color = Muted, fontSize = 11.sp)
            }
        }
        if (habits.isEmpty()) {
            item { EmptyHabits(onAdd) }
        } else {
            items(habits, key = { it.habit.id }) { item ->
                val checked = item.checkIns.any { it.date == today }
                HabitCard(
                    item = item,
                    checked = checked,
                    onToggle = {
                        if (!checked) haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                        onToggle(item.habit.id)
                    },
                    onOpen = { onOpenHistory(item.habit.id) },
                )
            }
        }
        item {
            Text(
                "每一个普通的日子，\n都因为你的一点坚持而不同。",
                modifier = Modifier.fillMaxWidth().padding(vertical = 12.dp),
                color = Color(0xFF8C7D6B),
                fontFamily = FontFamily.Serif,
                fontSize = 12.sp,
                lineHeight = 22.sp,
                textAlign = androidx.compose.ui.text.style.TextAlign.Center,
            )
        }
    }
}

@Composable
private fun JournalHero(
    today: LocalDate,
    completed: Int,
    total: Int,
) {
    val weekday =
        when (today.dayOfWeek) {
            DayOfWeek.MONDAY -> "星期一"
            DayOfWeek.TUESDAY -> "星期二"
            DayOfWeek.WEDNESDAY -> "星期三"
            DayOfWeek.THURSDAY -> "星期四"
            DayOfWeek.FRIDAY -> "星期五"
            DayOfWeek.SATURDAY -> "星期六"
            DayOfWeek.SUNDAY -> "星期日"
        }
    Row(
        modifier = Modifier.fillMaxWidth().padding(vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        Column(Modifier.weight(1f)) {
            Text("${today.monthValue} 月 ${today.dayOfMonth} 日 · $weekday", color = Muted, fontSize = 11.sp, letterSpacing = 1.sp)
            Spacer(Modifier.height(8.dp))
            Text("不必做得很多，\n今天做一点就好。", style = MaterialTheme.typography.headlineSmall, fontSize = 25.sp, lineHeight = 38.sp)
            Canvas(Modifier.width(64.dp).height(9.dp).rotate(-2f)) {
                drawLine(
                    HandwrittenGold,
                    start =
                        androidx.compose.ui.geometry
                            .Offset(0f, 5f),
                    end =
                        androidx.compose.ui.geometry
                            .Offset(size.width, 1f),
                    strokeWidth = 2.dp.toPx(),
                )
            }
        }
        Box(modifier = Modifier.size(78.dp).rotate(3f), contentAlignment = Alignment.Center) {
            Canvas(Modifier.fillMaxSize()) {
                drawCircle(
                    Color(0xFF9A9D88),
                    style = Stroke(width = 1.dp.toPx(), pathEffect = PathEffect.dashPathEffect(floatArrayOf(7f, 7f))),
                )
            }
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text("$completed/$total", color = Sage, fontSize = 24.sp, fontWeight = FontWeight.Medium)
                Text("今日已完成", color = Muted, fontSize = 9.sp)
            }
        }
    }
}

@Composable
private fun HabitCard(
    item: HabitWithCheckIns,
    checked: Boolean,
    onToggle: () -> Unit,
    onOpen: () -> Unit,
) {
    val color = Color(item.habit.color)
    val stats = remember(item.checkIns) { calculateHabitStats(item.checkIns) }
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(18.dp),
        color = if (checked) SageSoft else PaperLight,
        shadowElevation = 1.dp,
        border = androidx.compose.foundation.BorderStroke(1.dp, Color(0x1470634F)),
    ) {
        Row(
            Modifier.fillMaxWidth().padding(start = 14.dp, end = 10.dp, top = 12.dp, bottom = 12.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Row(Modifier.weight(1f).clickable(onClick = onOpen), verticalAlignment = Alignment.CenterVertically) {
                HabitSymbol(item.habit.icon, color)
                Spacer(Modifier.width(12.dp))
                Column {
                    Text(item.habit.name, fontSize = 16.sp, fontWeight = FontWeight.SemiBold, color = Ink)
                    Spacer(Modifier.height(5.dp))
                    Text(
                        if (checked) "今天已打卡 · 累计 ${stats.total} 天" else "当前连续 ${stats.current} 天 · 今天等你",
                        color = Muted,
                        fontSize = 11.sp,
                    )
                }
            }
            OutlinedIconButton(
                onClick = onToggle,
                modifier = Modifier.size(46.dp),
                shape = CircleShape,
                border = androidx.compose.foundation.BorderStroke(1.5.dp, if (checked) Sage else Color(0xFFC8C8C0)),
                colors =
                    androidx.compose.material3.IconButtonDefaults.outlinedIconButtonColors(
                        containerColor = if (checked) Sage else Color.Transparent,
                        contentColor = if (checked) OnSage else Muted,
                    ),
            ) {
                Icon(
                    Icons.Rounded.Check,
                    contentDescription = if (checked) "撤销${item.habit.name}今日打卡" else "完成${item.habit.name}今日打卡",
                    modifier = Modifier.size(22.dp),
                )
            }
        }
    }
}

@Composable
private fun EmptyHabits(onAdd: () -> Unit) {
    Surface(shape = RoundedCornerShape(18.dp), color = PaperLight, border = androidx.compose.foundation.BorderStroke(1.dp, LineColor)) {
        Column(Modifier.fillMaxWidth().padding(28.dp), horizontalAlignment = Alignment.CenterHorizontally) {
            Text("先写下一件想坚持的小事", fontFamily = FontFamily.Serif, fontSize = 19.sp, color = Ink)
            Spacer(Modifier.height(8.dp))
            Text(
                "不用给自己定太大的目标，\n每天做到一点，就值得被记住。",
                color = Muted,
                fontSize = 12.sp,
                lineHeight = 21.sp,
                textAlign = androidx.compose.ui.text.style.TextAlign.Center,
            )
            Spacer(Modifier.height(18.dp))
            androidx.compose.material3.Button(onClick = onAdd, shape = RoundedCornerShape(14.dp)) {
                Icon(Icons.Rounded.Add, contentDescription = null)
                Spacer(Modifier.width(6.dp))
                Text("写下第一个习惯")
            }
        }
    }
}
