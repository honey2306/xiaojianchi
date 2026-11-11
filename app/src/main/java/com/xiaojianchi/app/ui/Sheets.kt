package com.xiaojianchi.app.ui

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.content.Intent
import android.provider.Settings
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Check
import androidx.compose.material.icons.rounded.Close
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.xiaojianchi.app.BuildConfig
import com.xiaojianchi.app.data.CheckInEntity
import com.xiaojianchi.app.data.HabitEntity
import com.xiaojianchi.app.update.UpdateState
import java.time.LocalDate

/** 开始日期不能晚于已有的第一条记录，否则日历里会出现「开始之前就有脚印」。 */
private fun clampStartDate(
    date: LocalDate,
    earliestCheckIn: LocalDate?,
): LocalDate = if (earliestCheckIn != null && date.isAfter(earliestCheckIn)) earliestCheckIn else date

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HabitEditorSheet(
    existing: HabitEntity?,
    nextSortOrder: Int,
    today: LocalDate,
    earliestCheckIn: LocalDate?,
    onDismiss: () -> Unit,
    onSave: (HabitEntity, Int) -> Unit,
    onArchive: (Long) -> Unit,
    onRequestNotifications: () -> Unit,
) {
    var name by remember(existing) { mutableStateOf(existing?.name ?: "") }
    var icon by remember(existing) { mutableStateOf(existing?.icon ?: "book") }
    var color by remember(existing) { mutableStateOf(existing?.color?.let(::Color) ?: HabitColors.first()) }
    var reminderEnabled by remember(existing) { mutableStateOf(existing?.reminderMinutes != null) }
    var reminderMinutes by remember(existing) { mutableIntStateOf(existing?.reminderMinutes ?: 20 * 60 + 30) }
    var startDate by remember(existing) { mutableStateOf(clampStartDate(existing?.createdDate ?: today, earliestCheckIn)) }
    var priorDaysText by remember(existing) { mutableStateOf("") }
    var archiveConfirm by remember { mutableStateOf(false) }
    val context = LocalContext.current
    val isNew = existing == null
    val availablePriorDays = maxOf(0L, today.toEpochDay() - startDate.toEpochDay()).toInt()
    val priorDays = priorDaysText.toIntOrNull()?.coerceIn(0, availablePriorDays) ?: 0
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    ModalBottomSheet(onDismissRequest = onDismiss, sheetState = sheetState, containerColor = PaperLight) {
        Column(Modifier.fillMaxWidth().imePadding()) {
            Column(
                Modifier
                    .weight(1f, fill = false)
                    .verticalScroll(rememberScrollState())
                    .padding(start = 22.dp, end = 22.dp),
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(if (existing == null) "想坚持什么？" else "照顾这个小习惯", style = MaterialTheme.typography.headlineSmall, fontSize = 22.sp)
                    Spacer(Modifier.weight(1f))
                    IconButton(onClick = onDismiss) { Icon(Icons.Rounded.Close, contentDescription = "关闭") }
                }
                Spacer(Modifier.height(14.dp))
                Text("习惯名称", color = Muted, fontSize = 11.sp)
                Spacer(Modifier.height(8.dp))
                OutlinedTextField(
                    value = name,
                    onValueChange = { if (it.length <= 12) name = it },
                    modifier = Modifier.fillMaxWidth(),
                    placeholder = { Text("例如：每天写一点") },
                    singleLine = true,
                    shape = RoundedCornerShape(14.dp),
                )
                Spacer(Modifier.height(18.dp))
                Text("选择一个图标", color = Muted, fontSize = 11.sp)
                Spacer(Modifier.height(8.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                    listOf("book", "move", "language", "lotus", "spark").forEach { item ->
                        Box(
                            Modifier
                                .size(48.dp)
                                .clip(RoundedCornerShape(15.dp))
                                .background(if (icon == item) SageSoft else Color.Transparent)
                                .border(1.dp, if (icon == item) Sage else LineColor, RoundedCornerShape(15.dp))
                                .clickable { icon = item },
                            contentAlignment = Alignment.Center,
                        ) { HabitIcon(item, Modifier.size(23.dp), if (icon == item) Sage else Muted) }
                    }
                }
                Spacer(Modifier.height(18.dp))
                Text("一点属于自己的颜色", color = Muted, fontSize = 11.sp)
                Spacer(Modifier.height(8.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    HabitColors.forEach { item ->
                        Box(
                            Modifier
                                .size(42.dp)
                                .clip(CircleShape)
                                .background(item)
                                .border(2.dp, if (color == item) Ink else PaperLight, CircleShape)
                                .clickable { color = item },
                            contentAlignment = Alignment.Center,
                        ) {
                            if (color ==
                                item
                            ) {
                                Icon(Icons.Rounded.Check, contentDescription = "已选择颜色", tint = Color.White, modifier = Modifier.size(18.dp))
                            }
                        }
                    }
                }
                Spacer(Modifier.height(20.dp))
                Text("从哪天开始", color = Muted, fontSize = 11.sp)
                Spacer(Modifier.height(6.dp))
                Row(
                    modifier =
                        Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(12.dp))
                            .clickable {
                                DatePickerDialog(
                                    context,
                                    { _, year, month, day ->
                                        startDate = clampStartDate(LocalDate.of(year, month + 1, day), earliestCheckIn)
                                    },
                                    startDate.year,
                                    startDate.monthValue - 1,
                                    startDate.dayOfMonth,
                                ).apply { datePicker.maxDate = System.currentTimeMillis() }.show()
                            }.padding(vertical = 8.dp),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Text(
                        "${startDate.year} 年 ${startDate.monthValue} 月 ${startDate.dayOfMonth} 日",
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Medium,
                    )
                    Spacer(Modifier.weight(1f))
                    Text("更改", color = Sage, fontSize = 11.sp)
                }
                Text(
                    when {
                        isNew -> {
                            "之前已经在做了？设到开始的那一天，下面可以一次记下已经坚持的日子。"
                        }

                        earliestCheckIn != null -> {
                            "开始日期只标记足迹的起点，不能晚于第一条记录" +
                                "（${earliestCheckIn.monthValue} 月 ${earliestCheckIn.dayOfMonth} 日）。"
                        }

                        else -> {
                            "开始日期只标记足迹的起点，已经记下的不会受影响。"
                        }
                    },
                    color = Muted,
                    fontSize = 10.sp,
                )
                if (isNew) {
                    Spacer(Modifier.height(16.dp))
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Column(Modifier.weight(1f)) {
                            Text("已经坚持了多久", fontWeight = FontWeight.Medium)
                            Spacer(Modifier.height(4.dp))
                            Text(
                                if (availablePriorDays > 0) {
                                    "可以把之前连续的日子一次记下，最多 $availablePriorDays 天，之后只补记最近 7 天"
                                } else {
                                    "从今天开始，先留下一次就很好"
                                },
                                color = Muted,
                                fontSize = 10.sp,
                            )
                        }
                        OutlinedTextField(
                            value = priorDaysText,
                            onValueChange = { input ->
                                priorDaysText = input.filter { it.isDigit() }.take(4)
                            },
                            enabled = availablePriorDays > 0,
                            modifier = Modifier.width(104.dp),
                            placeholder = { Text("0", fontSize = 14.sp) },
                            suffix = { Text("天", fontSize = 12.sp, color = Muted) },
                            singleLine = true,
                            shape = RoundedCornerShape(14.dp),
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        )
                    }
                }
                Spacer(Modifier.height(20.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Column(Modifier.weight(1f)) {
                        Text("每日温和提醒", fontWeight = FontWeight.Medium)
                        Spacer(Modifier.height(4.dp))
                        Text(
                            if (reminderEnabled) {
                                "${"%02d".format(
                                    reminderMinutes / 60,
                                )}:${"%02d".format(reminderMinutes % 60)} · 今天想给自己留一点时间吗？"
                            } else {
                                "不提醒，按自己的节奏来"
                            },
                            color = Muted,
                            fontSize = 10.sp,
                        )
                    }
                    Switch(
                        checked = reminderEnabled,
                        onCheckedChange = {
                            reminderEnabled = it
                            if (it) onRequestNotifications()
                        },
                        colors = SwitchDefaults.colors(checkedTrackColor = Sage),
                    )
                }
                if (reminderEnabled) {
                    TextButton(onClick = {
                        TimePickerDialog(
                            context,
                            { _, hour, minute -> reminderMinutes = hour * 60 + minute },
                            reminderMinutes / 60,
                            reminderMinutes % 60,
                            true,
                        ).show()
                    }) { Text("调整提醒时间") }
                }
            }
            Column(
                Modifier
                    .fillMaxWidth()
                    .padding(start = 22.dp, end = 22.dp, top = 12.dp, bottom = 24.dp),
            ) {
                Button(
                    onClick = {
                        onSave(
                            (
                                existing
                                    ?: HabitEntity(
                                        name = "",
                                        icon = "book",
                                        color = HabitColors.first().toArgb().toLong(),
                                        sortOrder = nextSortOrder,
                                    )
                            ).copy(
                                name = name.trim(),
                                icon = icon,
                                color = color.toArgb().toLong(),
                                createdDate = startDate,
                                reminderMinutes = if (reminderEnabled) reminderMinutes else null,
                            ),
                            priorDays,
                        )
                    },
                    enabled = name.isNotBlank(),
                    modifier = Modifier.fillMaxWidth().height(50.dp),
                    shape = RoundedCornerShape(16.dp),
                ) { Text(if (existing == null) "开始坚持" else "保存改变") }
                if (existing != null) {
                    TextButton(onClick = {
                        archiveConfirm = true
                    }, modifier = Modifier.fillMaxWidth(), colors = ButtonDefaults.textButtonColors(contentColor = Muted)) {
                        Text("归档这个习惯，保留过去的记录")
                    }
                }
            }
        }
    }
    if (archiveConfirm && existing != null) {
        AlertDialog(
            onDismissRequest = { archiveConfirm = false },
            title = { Text("归档「${existing.name}」？") },
            text = { Text("它会从今天列表里收起，但过去的打卡和感受都会保留。") },
            confirmButton = { TextButton(onClick = { onArchive(existing.id) }) { Text("归档") } },
            dismissButton = { TextButton(onClick = { archiveConfirm = false }) { Text("再想想") } },
            containerColor = PaperLight,
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NoteEditorSheet(
    habitName: String,
    checkIn: CheckInEntity,
    onDismiss: () -> Unit,
    onSave: (String) -> Unit,
) {
    var note by remember(checkIn.id) { mutableStateOf(checkIn.note) }
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    ModalBottomSheet(onDismissRequest = onDismiss, sheetState = sheetState, containerColor = PaperLight) {
        Column(Modifier.fillMaxWidth().imePadding().padding(start = 22.dp, end = 22.dp, bottom = 28.dp)) {
            Text("今天也做到了", style = MaterialTheme.typography.headlineSmall, fontSize = 22.sp)
            Spacer(Modifier.height(8.dp))
            Text("给「$habitName」留一句话，也可以不写", color = Muted, fontSize = 11.sp)
            Spacer(Modifier.height(12.dp))
            OutlinedTextField(
                value = note,
                onValueChange = { if (it.length <= 80) note = it },
                modifier = Modifier.fillMaxWidth().height(120.dp),
                placeholder = { Text("今天只做了十分钟，但没有放弃。") },
                shape = RoundedCornerShape(14.dp),
            )
            Spacer(Modifier.height(16.dp))
            Button(onClick = {
                onSave(note.trim())
            }, modifier = Modifier.fillMaxWidth().height(50.dp), shape = RoundedCornerShape(16.dp)) { Text("留住今天") }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DayDetailSheet(
    habitName: String,
    date: LocalDate,
    checkIn: CheckInEntity?,
    today: LocalDate,
    startDate: LocalDate,
    editableFrom: LocalDate,
    onDismiss: () -> Unit,
    onCheckIn: () -> Unit,
    onEditNote: () -> Unit,
    onUncheck: () -> Unit,
) {
    val canBackfill = !date.isAfter(today) && !date.isBefore(editableFrom)
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    ModalBottomSheet(onDismissRequest = onDismiss, sheetState = sheetState, containerColor = PaperLight) {
        Column(Modifier.fillMaxWidth().padding(start = 22.dp, end = 22.dp, bottom = 28.dp)) {
            Text("${date.monthValue} 月 ${date.dayOfMonth} 日", style = MaterialTheme.typography.headlineSmall, fontSize = 24.sp)
            Spacer(Modifier.height(8.dp))
            Text(
                when {
                    checkIn != null -> "「$habitName」已经完成"
                    canBackfill -> "那天也做到了，可以补上一枚印记"
                    else -> "这一天还没有留下记录"
                },
                color = Muted,
                fontSize = 12.sp,
            )
            if (!checkIn?.note.isNullOrBlank()) {
                Spacer(Modifier.height(16.dp))
                Text("“${checkIn?.note}”", fontFamily = FontFamily.Serif, fontSize = 16.sp, lineHeight = 26.sp)
            }
            Spacer(Modifier.height(20.dp))
            if (checkIn == null) {
                Button(
                    onClick = onCheckIn,
                    enabled = canBackfill,
                    modifier = Modifier.fillMaxWidth().height(50.dp),
                    shape = RoundedCornerShape(16.dp),
                ) {
                    Text(if (date == today) "完成今日打卡" else "补记这一天")
                }
                if (!canBackfill) {
                    Text(
                        if (date.isBefore(startDate)) "这一天在你开始之前，不用补记" else "补记最多回到最近 7 天",
                        color = Muted,
                        fontSize = 11.sp,
                        modifier = Modifier.padding(top = 8.dp),
                    )
                }
            } else {
                Button(
                    onClick = onEditNote,
                    modifier = Modifier.fillMaxWidth().height(50.dp),
                    shape = RoundedCornerShape(16.dp),
                ) { Text("写一句今天的感受") }
                TextButton(onClick = onUncheck, modifier = Modifier.fillMaxWidth()) { Text("撤销这一天的记录") }
            }
        }
    }
}

@Composable
fun SettingsDialog(
    onDismiss: () -> Unit,
    updateState: UpdateState,
    onCheckUpdate: () -> Unit,
) {
    val context = LocalContext.current
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("小坚持", fontFamily = FontFamily.Serif) },
        text = {
            Column {
                Text("你的坚持，只属于你。", fontWeight = FontWeight.SemiBold)
                Spacer(Modifier.height(10.dp))
                Text("无需登录，所有习惯、打卡与感受都保存在这台设备中。每日提醒只使用系统本地通知，不会上传任何内容。", lineHeight = 22.sp)
                Spacer(Modifier.height(18.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Column(Modifier.weight(1f)) {
                        Text("版本 ${BuildConfig.VERSION_NAME}", fontWeight = FontWeight.Medium)
                        Text(
                            if (updateState is UpdateState.Checking) "正在检查新版本…" else "通过 GitHub 安全获取应用更新",
                            color = Muted,
                            fontSize = 11.sp,
                        )
                    }
                    TextButton(onClick = onCheckUpdate, enabled = updateState !is UpdateState.Checking) { Text("检查更新") }
                }
            }
        },
        confirmButton = {
            TextButton(onClick = {
                context.startActivity(
                    Intent(Settings.ACTION_APP_NOTIFICATION_SETTINGS).putExtra(Settings.EXTRA_APP_PACKAGE, context.packageName),
                )
            }) { Text("系统通知设置") }
        },
        dismissButton = { TextButton(onClick = onDismiss) { Text("知道了") } },
        containerColor = PaperLight,
    )
}
