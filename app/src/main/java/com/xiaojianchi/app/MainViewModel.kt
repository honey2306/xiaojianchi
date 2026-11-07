package com.xiaojianchi.app

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.xiaojianchi.app.data.CheckInEntity
import com.xiaojianchi.app.data.HabitEntity
import com.xiaojianchi.app.data.HabitWithCheckIns
import com.xiaojianchi.app.reminder.ReminderScheduler
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.time.LocalDate

class MainViewModel(
    application: Application,
) : AndroidViewModel(application) {
    private val app = application as LittleStepsApplication
    private val repository = app.repository

    val habits: StateFlow<List<HabitWithCheckIns>> =
        repository.observeActiveHabits().stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(5_000),
            emptyList(),
        )

    fun toggleToday(
        habitId: Long,
        onComplete: (Boolean) -> Unit = {},
    ) {
        viewModelScope.launch { onComplete(repository.toggleToday(habitId)) }
    }

    fun checkIn(
        habitId: Long,
        date: LocalDate,
        backfill: Boolean,
        onComplete: () -> Unit = {},
    ) {
        require(!date.isAfter(LocalDate.now())) { "未来日期不能打卡" }
        require(!backfill || !date.isBefore(LocalDate.now().minusDays(7))) { "仅支持补记最近 7 天" }
        viewModelScope.launch {
            repository.setChecked(habitId, date, backfill)
            onComplete()
        }
    }

    fun uncheck(
        habitId: Long,
        date: LocalDate,
        onComplete: () -> Unit = {},
    ) {
        viewModelScope.launch {
            repository.uncheck(habitId, date)
            onComplete()
        }
    }

    fun saveNote(
        habitId: Long,
        date: LocalDate,
        note: String,
        onComplete: () -> Unit = {},
    ) {
        viewModelScope.launch {
            repository.saveNote(habitId, date, note)
            onComplete()
        }
    }

    fun saveHabit(
        habit: HabitEntity,
        onComplete: (Long) -> Unit = {},
    ) {
        viewModelScope.launch {
            val id = repository.saveHabit(habit)
            val saved = habit.copy(id = id)
            if (saved.reminderMinutes == null) ReminderScheduler.cancel(app, id) else ReminderScheduler.schedule(app, saved)
            onComplete(id)
        }
    }

    fun archiveHabit(
        id: Long,
        onComplete: () -> Unit = {},
    ) {
        viewModelScope.launch {
            repository.archiveHabit(id)
            ReminderScheduler.cancel(app, id)
            onComplete()
        }
    }
}
