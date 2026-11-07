package com.xiaojianchi.app.data

import org.junit.Assert.assertEquals
import org.junit.Test
import java.time.LocalDate

class HabitStatsTest {
    private val today = LocalDate.of(2025, 4, 18)

    private fun checks(vararg days: Int): List<CheckInEntity> =
        days.map {
            CheckInEntity(habitId = 1, date = LocalDate.ofEpochDay(today.toEpochDay() + it))
        }

    @Test
    fun `today is included in current streak`() {
        val stats = calculateHabitStats(checks(0, -1, -2, -5, -6), today)
        assertEquals(HabitStats(total = 5, current = 3, longest = 3), stats)
    }

    @Test
    fun `yesterday keeps current streak alive`() {
        val stats = calculateHabitStats(checks(-1, -2, -4), today)
        assertEquals(2, stats.current)
        assertEquals(3, stats.total)
    }

    @Test
    fun `a missed day does not erase total or longest streak`() {
        val stats = calculateHabitStats(checks(-10, -9, -8, -2), today)
        assertEquals(HabitStats(total = 4, current = 0, longest = 3), stats)
    }

    @Test
    fun `empty history has zero stats`() {
        assertEquals(HabitStats(0, 0, 0), calculateHabitStats(emptyList(), today))
    }
}
