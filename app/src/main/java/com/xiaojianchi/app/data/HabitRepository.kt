package com.xiaojianchi.app.data

import androidx.room.withTransaction
import kotlinx.coroutines.flow.Flow
import java.time.LocalDate

class HabitRepository(
    private val database: AppDatabase,
) {
    private val dao = database.habitDao()

    fun observeActiveHabits(): Flow<List<HabitWithCheckIns>> = dao.observeActiveHabits()

    /**
     * 保存习惯。新建时可以把之前已经坚持的 [priorDays] 天（截止昨天）
     * 一并记下，让累计天数不从第一天重新开始。
     */
    suspend fun saveHabit(
        habit: HabitEntity,
        priorDays: Int = 0,
        today: LocalDate = LocalDate.now(),
    ): Long =
        database.withTransaction {
            val id =
                if (habit.id == 0L) {
                    dao.insertHabit(habit)
                } else {
                    dao.updateHabit(habit)
                    habit.id
                }
            if (habit.id == 0L && priorDays > 0) {
                val count = minOf(priorDays, habit.copy(id = id).availablePriorDays(today))
                (1..count).forEach { offset ->
                    val date = today.minusDays(offset.toLong())
                    if (dao.checkIn(id, date) == null) {
                        dao.insertCheckIn(CheckInEntity(habitId = id, date = date, backfill = true))
                    }
                }
            }
            id
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
        val today = LocalDate.now()
        require(!date.isAfter(today)) { "Future dates cannot be checked in." }
        val habit = dao.habitById(habitId) ?: error("Habit $habitId no longer exists.")
        require(habit.canEditDate(date, today)) { "$date is before the habit start date." }
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
