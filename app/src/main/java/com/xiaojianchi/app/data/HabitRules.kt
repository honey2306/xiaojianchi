package com.xiaojianchi.app.data

import java.time.LocalDate

/**
 * 习惯可以从更早的日子开始：新建时选择过去的开始日期，
 * 之前已经坚持的连续天数在保存时一次性记下，不必从第一天重新数。
 *
 * 之后能补记的日子是三个条件的交集：
 * - 不晚于今天，未来日期始终不能打卡
 * - 不早于开始日期，开始之前的日子不属于这个习惯
 * - 最多回到最近 [BACKFILL_WINDOW_DAYS] 天，与 1.0.1 的旧规则一致
 *
 * 也就是说，过去的日子只在新建时一次性记下；想补更早的记录，
 * 必须显式把开始日期改早。升级后的老习惯不会突然获得整段历史的补记能力。
 */
const val BACKFILL_WINDOW_DAYS = 7L

fun HabitEntity.editableFrom(today: LocalDate = LocalDate.now()): LocalDate = maxOf(createdDate, today.minusDays(BACKFILL_WINDOW_DAYS))

fun HabitEntity.canEditDate(
    date: LocalDate,
    today: LocalDate = LocalDate.now(),
): Boolean = !date.isAfter(today) && !date.isBefore(editableFrom(today))

/** 开始日期到今天之间、可以作为「已经坚持」一次性记下的天数（不含今天）。 */
fun HabitEntity.availablePriorDays(today: LocalDate = LocalDate.now()): Int {
    val days = today.toEpochDay() - createdDate.toEpochDay()
    return days.coerceIn(0L, Int.MAX_VALUE.toLong()).toInt()
}
