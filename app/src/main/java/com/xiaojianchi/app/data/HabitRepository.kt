package com.xiaojianchi.app.data

import androidx.room.withTransaction
import kotlinx.coroutines.flow.Flow
import java.time.LocalDate

class HabitRepository(
    private val database: AppDatabase,
) {
    private val dao = database.habitDao()

    fun observeActiveHabits(): Flow<List<HabitWithCheckIns>> = dao.observeActiveHabits()

    suspend fun saveHabit(habit: HabitEntity): Long =
        if (habit.id == 0L) {
            dao.insertHabit(habit)
        } else {
            dao.updateHabit(habit)
            habit.id
        }

    suspend fun habit(id: Long): HabitEntity? = dao.habitById(id)

    suspend fun archiveHabit(id: Long) {
        dao.habitById(id)?.let { dao.updateHabit(it.copy(archived = true)) }
    }

    suspend fun setChecked(
        habitId: Long,
        date: LocalDate,
        backfill: Boolean,
    ): CheckInEntity {
        require(!date.isAfter(LocalDate.now())) { "Future dates cannot be checked in." }
        return database.withTransaction {
            dao.checkIn(habitId, date) ?: CheckInEntity(habitId = habitId, date = date, backfill = backfill).also {
                dao.insertCheckIn(it)
            }
        }
    }

    suspend fun toggleToday(
        habitId: Long,
        today: LocalDate = LocalDate.now(),
    ): Boolean =
        database.withTransaction {
            val existing = dao.checkIn(habitId, today)
            if (existing == null) {
                dao.insertCheckIn(CheckInEntity(habitId = habitId, date = today))
                true
            } else {
                dao.deleteCheckIn(habitId, today)
                false
            }
        }

    suspend fun uncheck(
        habitId: Long,
        date: LocalDate,
    ) {
        require(!date.isAfter(LocalDate.now())) { "Future dates cannot be changed." }
        dao.deleteCheckIn(habitId, date)
    }

    suspend fun saveNote(
        habitId: Long,
        date: LocalDate,
        note: String,
    ) {
        database.withTransaction {
            dao.checkIn(habitId, date)?.let { dao.updateCheckIn(it.copy(note = note.trim())) }
        }
    }
}
