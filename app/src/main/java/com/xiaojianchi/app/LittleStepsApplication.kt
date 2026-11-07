package com.xiaojianchi.app

import android.app.Application
import com.xiaojianchi.app.data.AppDatabase
import com.xiaojianchi.app.data.HabitRepository
import com.xiaojianchi.app.reminder.ReminderScheduler
import com.xiaojianchi.app.reminder.createReminderChannel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch

class LittleStepsApplication : Application() {
    val applicationScope = CoroutineScope(SupervisorJob() + Dispatchers.IO)
    lateinit var repository: HabitRepository
        private set

    override fun onCreate() {
        super.onCreate()
        repository = HabitRepository(AppDatabase.get(this))
        createReminderChannel(this)
        applicationScope.launch {
            repository.observeActiveHabits().collect { habits ->
                ReminderScheduler.sync(this@LittleStepsApplication, habits.map { it.habit })
            }
        }
    }
}
