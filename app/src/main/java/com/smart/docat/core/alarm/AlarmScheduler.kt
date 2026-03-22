package com.smart.docat.core.alarm

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AlarmScheduler @Inject constructor(
    @ApplicationContext private val context: Context
) {
    private val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager

    fun schedule(alarmType: AlarmType, triggerAtMillis: Long) {
        val pendingIntent = buildPendingIntent(alarmType, PendingIntent.FLAG_UPDATE_CURRENT) ?: return
        if (alarmManager.canScheduleExactAlarms()) {
            alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, triggerAtMillis, pendingIntent)
        } else {
            alarmManager.setAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, triggerAtMillis, pendingIntent)
        }
    }

    fun cancel(alarmType: AlarmType) {
        val pendingIntent = buildPendingIntent(alarmType, PendingIntent.FLAG_NO_CREATE) ?: return
        alarmManager.cancel(pendingIntent)
    }

    private fun buildPendingIntent(alarmType: AlarmType, flags: Int): PendingIntent? {
        val intent = Intent(context, AlarmReceiver::class.java).apply {
            putExtra(AlarmReceiver.EXTRA_ALARM_TYPE, alarmType.name)
        }
        return PendingIntent.getBroadcast(
            context,
            alarmType.ordinal,
            intent,
            flags or PendingIntent.FLAG_IMMUTABLE
        )
    }
}