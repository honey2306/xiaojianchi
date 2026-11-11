package com.xiaojianchi.app.data

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test
import java.time.LocalDate

class HabitRulesTest {
    private val today = LocalDate.of(2025, 4, 18)

    private fun habit(startedDaysAgo: Long = 30) =
        HabitEntity(
            name = "跑步",
            icon = "move",
            color = 0xFF5C8371,
            createdDate = today.minusDays(startedDaysAgo),
        )

    @Test
    fun `a start date in the past records history but only the last 7 days stay editable`() {
        val habit = habit()
        assertEquals(today.minusDays(BACKFILL_WINDOW_DAYS), habit.editableFrom(today))
        assertTrue(habit.canEditDate(today, today))
        assertTrue(habit.canEditDate(today.minusDays(BACKFILL_WINDOW_DAYS), today))
        assertFalse(habit.canEditDate(today.minusDays(BACKFILL_WINDOW_DAYS + 1), today))
        assertFalse(habit.canEditDate(habit.createdDate, today))
    }

    @Test
    fun `future days are never editable`() {
        assertFalse(habit().canEditDate(today.plusDays(1), today))
    }

    @Test
    fun `a habit that starts today cannot reach back before its start`() {
        val habit = habit(startedDaysAgo = 0)
        assertEquals(today, habit.editableFrom(today))
        assertTrue(habit.canEditDate(today, today))
        assertFalse(habit.canEditDate(today.minusDays(1), today))
    }

    @Test
    fun `a start date after today disables every day`() {
        val habit = habit(startedDaysAgo = -3)
        assertFalse(habit.canEditDate(today, today))
    }

    @Test
    fun `available prior days count every day before today`() {
        assertEquals(30, habit(startedDaysAgo = 30).availablePriorDays(today))
        assertEquals(1, habit(startedDaysAgo = 1).availablePriorDays(today))
        assertEquals(0, habit(startedDaysAgo = 0).availablePriorDays(today))
    }
}
