package com.xiaojianchi.app.ui

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.ChevronLeft
import androidx.compose.material.icons.rounded.ChevronRight
import androidx.compose.material.icons.rounded.MoreHoriz
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.xiaojianchi.app.data.HabitWithCheckIns
import com.xiaojianchi.app.data.calculateHabitStats
import com.xiaojianchi.app.data.canEditDate
import java.time.LocalDate
import java.time.YearMonth

@Composable
fun HistoryScreen(
    habits: List<HabitWithCheckIns>,
    selectedId: Long?,
    today: LocalDate,
    onSelect: (Long) -> Unit,
    onDaySelected: (Long, LocalDate) -> Unit,
    onEdit: (Long) -> Unit,
    onAdd: () -> Unit,
) {
    val selected = habits.firstOrNull { it.habit.id == selectedId } ?: habits.firstOrNull()
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding =
            androidx.compose.foundation.layout
                .PaddingValues(start = 20.dp, end = 20.dp, bottom = 18.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        item {
            Row(Modifier.fillMaxWidth().height(56.dp), verticalAlignment = Alignment.CenterVertically) {
                JournalBrand()
                Spacer(Modifier.weight(1f))
                if (selected != null) {
                    IconButton(onClick = { onEdit(selected.habit.id) }) {
                        Icon(Icons.Rounded.MoreHoriz, contentDescription = "编辑${selected.habit.name}", tint = Muted)
                    }
                }
            }
        }
        item {
            Column(Modifier.padding(vertical = 4.dp)) {
                Text("我的坚持", style = MaterialTheme.typography.headlineSmall, fontSize = 27.sp)
                Text("那些没有放弃的日子，都被好好收藏。", color = Muted, fontSize = 11.sp)
            }
        }
        if (habits.isEmpty()) {
            item {
                Column(Modifier.fillMaxWidth().padding(vertical = 48.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                    Text("这里会收藏你走过的每一天", fontFamily = FontFamily.Serif, fontSize = 19.sp)
                    Spacer(Modifier.height(8.dp))
                    Text("先从一个很小的习惯开始。", color = Muted, fontSize = 12.sp)
                    Spacer(Modifier.height(16.dp))
                    androidx.compose.material3.TextButton(onClick = onAdd) { Text("写下第一个习惯") }
                }
            }
        } else if (selected != null) {
            item {
                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(7.dp),
                    contentPadding =
                        androidx.compose.foundation.layout
                            .PaddingValues(vertical = 4.dp),
                ) {
                    items(habits, key = { it.habit.id }) { item ->
                        FilterChip(
                            selected = item.habit.id == selected.habit.id,
                            onClick = { onSelect(item.habit.id) },
                            label = { Text(item.habit.name, fontSize = 11.sp) },
                            shape = CircleShape,
                            colors =
                                FilterChipDefaults.filterChipColors(
                                    containerColor = Color.Transparent,
                                    labelColor = Muted,
                                    selectedContainerColor = Sage,
                                    selectedLabelColor = OnSage,
                                ),
                            border =
                                FilterChipDefaults.filterChipBorder(
                                    enabled = true,
                                    selected = item.habit.id == selected.habit.id,
                                    borderColor = Color.Transparent,
                                    selectedBorderColor = Sage,
                                ),
                        )
                    }
                }
            }
            item { StatsRow(selected) }
            item { MonthCalendar(item = selected, today = today, onDaySelected = onDaySelected) }
            item { LatestNote(selected) }
            item {
                Text(
                    "从 ${selected.habit.createdDate.monthValue} 月 ${selected.habit.createdDate.dayOfMonth} 日开始，" +
                        "中断不会抹去你已经走过的 ${calculateHabitStats(selected.checkIns, today).total} 天。",
                    modifier = Modifier.fillMaxWidth().padding(top = 6.dp),
                    color = Color(0xFF7A6E5E),
                    fontFamily = FontFamily.Serif,
                    fontSize = 11.sp,
                    textAlign = TextAlign.Center,
                )
            }
        }
    }
}

@Composable
private fun StatsRow(item: HabitWithCheckIns) {
    val stats = calculateHabitStats(item.checkIns)
    Row(Modifier.fillMaxWidth().padding(vertical = 10.dp), verticalAlignment = Alignment.Bottom) {
        Column {
            Text("累计坚持", color = Muted, fontSize = 11.sp)
            Row(verticalAlignment = Alignment.Bottom) {
                Text("${stats.total}", color = Sage, fontSize = 48.sp, lineHeight = 52.sp, fontWeight = FontWeight.Medium)
                Text(" 天", color = Sage, fontSize = 12.sp, modifier = Modifier.padding(bottom = 7.dp))
            }
        }
        Spacer(Modifier.width(22.dp))
        StatSmall("当前连续", stats.current, Modifier.weight(1f))
        StatSmall("最长连续", stats.longest, Modifier.weight(1f))
    }
}

@Composable
private fun StatSmall(
    label: String,
    value: Int,
    modifier: Modifier,
) {
    Column(modifier.padding(start = 12.dp, bottom = 5.dp)) {
        Text(label, color = Muted, fontSize = 10.sp)
        Spacer(Modifier.height(5.dp))
        Text("$value 天", fontSize = 19.sp, fontWeight = FontWeight.Medium)
    }
}

@Composable
private fun MonthCalendar(
    item: HabitWithCheckIns,
    today: LocalDate,
    onDaySelected: (Long, LocalDate) -> Unit,
) {
    var month by remember(item.habit.id) { mutableStateOf(YearMonth.from(today)) }
    val checks = item.checkIns.associateBy { it.date }
    Surface(shape = RoundedCornerShape(20.dp), color = PaperLight, shadowElevation = 1.dp) {
        Column(Modifier.fillMaxWidth().padding(horizontal = 10.dp, vertical = 12.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                IconButton(onClick = { month = month.minusMonths(1) }) { Icon(Icons.Rounded.ChevronLeft, contentDescription = "上个月") }
                Text(
                    "${month.year} 年 ${month.monthValue} 月",
                    modifier = Modifier.weight(1f),
                    textAlign = TextAlign.Center,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.SemiBold,
                )
                IconButton(onClick = { month = month.plusMonths(1) }) { Icon(Icons.Rounded.ChevronRight, contentDescription = "下个月") }
            }
            Row(Modifier.fillMaxWidth()) {
                listOf("一", "二", "三", "四", "五", "六", "日").forEach {
                    Text(it, modifier = Modifier.weight(1f), textAlign = TextAlign.Center, color = Muted, fontSize = 9.sp)
                }
            }
            Spacer(Modifier.height(5.dp))
            val leading = month.atDay(1).dayOfWeek.value - 1
            val cellCount = ((leading + month.lengthOfMonth() + 6) / 7) * 7
            for (weekStart in 0 until cellCount step 7) {
                Row(Modifier.fillMaxWidth()) {
                    for (offset in 0 until 7) {
                        val index = weekStart + offset
                        val day = index - leading + 1
                        if (day !in 1..month.lengthOfMonth()) {
                            Spacer(Modifier.weight(1f).aspectRatio(1f))
                        } else {
                            val date = month.atDay(day)
                            val checkIn = checks[date]
                            val canEdit = checkIn != null || item.habit.canEditDate(date, today)
                            DayCell(
                                day = day,
                                marked = checkIn != null,
                                today = date == today,
                                future = date.isAfter(today),
                                enabled = canEdit,
                                onClick = { onDaySelected(item.habit.id, date) },
                                modifier = Modifier.weight(1f),
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun DayCell(
    day: Int,
    marked: Boolean,
    today: Boolean,
    future: Boolean,
    enabled: Boolean,
    onClick: () -> Unit,
    modifier: Modifier,
) {
    Box(
        modifier =
            modifier
                .aspectRatio(1f)
                .padding(2.dp)
                .clip(CircleShape)
                .background(if (marked) Color(0xFFE4E9DC) else Color.Transparent)
                .then(if (today) Modifier.background(Color.Transparent) else Modifier)
                .clickable(enabled = enabled, onClick = onClick),
        contentAlignment = Alignment.Center,
    ) {
        if (today) Canvas(Modifier.fillMaxSize().padding(1.dp)) { drawCircle(Sage, style = Stroke(1.2.dp.toPx())) }
        if (marked) Canvas(Modifier.fillMaxSize().padding(7.dp)) { drawOval(Color(0xFF829078), style = Stroke(1.dp.toPx())) }
        Text(
            "$day",
            color = if (marked) Sage else Ink.copy(alpha = if (future) 0.28f else 1f),
            fontSize = 11.sp,
            fontWeight = if (marked) FontWeight.Bold else FontWeight.Normal,
        )
    }
}

@Composable
private fun LatestNote(item: HabitWithCheckIns) {
    val note = item.checkIns.filter { it.note.isNotBlank() }.maxByOrNull { it.date }
    Surface(shape = RoundedCornerShape(16.dp), color = PaperDeep) {
        Column(Modifier.fillMaxWidth().padding(13.dp)) {
            Text(note?.let { "${it.date.monthValue} 月 ${it.date.dayOfMonth} 日 · 留下的小记录" } ?: "给今天留一句话", color = Muted, fontSize = 10.sp)
            Spacer(Modifier.height(4.dp))
            Text(note?.note?.let { "“$it”" } ?: "不必每天都写，想留下时再写就好。", fontSize = 12.sp, lineHeight = 20.sp)
        }
    }
}
