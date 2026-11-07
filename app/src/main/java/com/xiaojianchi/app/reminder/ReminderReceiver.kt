package com.xiaojianchi.app.reminder

import android.Manifest
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat
import com.xiaojianchi.app.LittleStepsApplication
import com.xiaojianchi.app.MainActivity
import com.xiaojianchi.app.R
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

class ReminderReceiver : BroadcastReceiver() {
    override fun onReceive(
        context: Context,
        intent: Intent,
    ) {
        val pendingResult = goAsync()
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val id = intent.action?.substringAfterLast('.')?.toLongOrNull() ?: return@launch
                val app = context.applicationContext as LittleStepsApplication
                val habit = app.repository.habit(id) ?: return@launch
                if (habit.archived || habit.reminderMinutes == null) return@launch
                showNotification(context, habit.id, habit.name)
                ReminderScheduler.schedule(context, habit)
            } finally {
                pendingResult.finish()
            }
        }
    }

    private fun showNotification(
        context: Context,
        habitId: Long,
        name: String,
    ) {
        if (Build.VERSION.SDK_INT >= 33 &&
            ContextCompat.checkSelfPermission(context, Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED
        ) {
            return
        }
        if (!NotificationManagerCompat.from(context).areNotificationsEnabled()) return
        val openApp =
            PendingIntent.getActivity(
                context,
                habitId.hashCode(),
                Intent(context, MainActivity::class.java).putExtra("habitId", habitId),
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE,
            )
        val notification =
            NotificationCompat
                .Builder(context, ReminderScheduler.CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_notification_leaf)
                .setContentTitle("给「$name」留一点时间")
                .setContentText("今天想给自己留一点时间吗？")
                .setContentIntent(openApp)
                .setAutoCancel(true)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .build()
        NotificationManagerCompat.from(context).notify(habitId.hashCode(), notification)
    }
}

class BootReceiver : BroadcastReceiver() {
    override fun onReceive(
        context: Context,
        intent: Intent,
    ) {
        if (intent.action !in setOf(Intent.ACTION_BOOT_COMPLETED, Intent.ACTION_MY_PACKAGE_REPLACED)) return
        val pendingResult = goAsync()
        val app = context.applicationContext as LittleStepsApplication
        app.applicationScope.launch {
            try {
                val habits =
                    app.repository
                        .observeActiveHabits()
                        .first()
                        .map { it.habit }
                ReminderScheduler.sync(context, habits)
            } finally {
                pendingResult.finish()
            }
        }
    }
}

fun createReminderChannel(context: Context) {
    val channel =
        NotificationChannel(
            ReminderScheduler.CHANNEL_ID,
            "温和打卡提醒",
            NotificationManager.IMPORTANCE_DEFAULT,
        ).apply { description = "每天轻轻提醒你，为自己的小事留一点时间。" }
    context.getSystemService(NotificationManager::class.java).createNotificationChannel(channel)
}
