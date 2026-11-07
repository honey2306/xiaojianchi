package com.xiaojianchi.app.data

import java.time.LocalDate

data class HabitStats(
    val total: Int,
    val current: Int,
    val longest: Int,
)

fun calculateHabitStats(
    checkIns: Collection<CheckInEntity>,
    today: LocalDate = LocalDate.now(),
): HabitStats {
    val dates = checkIns.mapTo(linkedSetOf()) { it.date }
    if (dates.isEmpty()) return HabitStats(0, 0, 0)

    var current = 0
    val streakStart =
        when {
            today in dates -> today
            today.minusDays(1) in dates -> today.minusDays(1)
            else -> null
        }
    streakStart?.let { start ->
        var cursor = start
        while (cursor in dates) {
            current += 1
            cursor = cursor.minusDays(1)
        }
    }

    var longest = 0
    var run = 0
    var previous: LocalDate? = null
    dates.sorted().forEach { date ->
        run = if (previous?.plusDays(1) == date) run + 1 else 1
        longest = maxOf(longest, run)
        previous = date
    }
    return HabitStats(total = dates.size, current = current, longest = longest)
}
