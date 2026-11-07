package com.xiaojianchi.app.reminder

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import com.xiaojianchi.app.data.HabitEntity
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.ZoneId

object ReminderScheduler {
    const val CHANNEL_ID = "gentle_habit_reminders"
    private const val ACTION_REMIND = "com.xiaojianchi.app.action.REMIND."

    fun sync(
        context: Context,
        habits: List<HabitEntity>,
    ) {
        habits.forEach { habit ->
            if (habit.reminderMinutes == null) cancel(context, habit.id) else schedule(context, habit)
        }
    }

    fun schedule(
        context: Context,
        habit: HabitEntity,
    ) {
        val minutes = habit.reminderMinutes ?: return
        val alarmManager = context.getSystemService(AlarmManager::class.java)
        val now = LocalDateTime.now()
        var next = now.toLocalDate().atTime(LocalTime.of(minutes / 60, minutes % 60))
        if (!next.isAfter(now)) next = next.plusDays(1)
        val triggerAt = next.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli()
        val pendingIntent = pendingIntent(context, habit.id)
        alarmManager.setAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, triggerAt, pendingIntent)
    }

    fun cancel(
        context: Context,
        habitId: Long,
    ) {
        context.getSystemService(AlarmManager::class.java).cancel(pendingIntent(context, habitId))
    }

    private fun pendingIntent(
        context: Context,
        habitId: Long,
    ): PendingIntent =
        PendingIntent.getBroadcast(
            context,
            habitId.hashCode(),
            Intent(context, ReminderReceiver::class.java).setAction(ACTION_REMIND + habitId),
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE,
        )
}
