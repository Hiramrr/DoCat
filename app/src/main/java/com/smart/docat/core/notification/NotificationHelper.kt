package com.smart.docat.core.notification

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import androidx.core.app.NotificationCompat
import com.smart.docat.MainActivity
import com.smart.docat.R
import com.smart.docat.core.alarm.AlarmType
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class NotificationHelper @Inject constructor(
    @ApplicationContext private val context: Context
) {
    private val notificationManager =
        context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

    companion object {
        const val CHANNEL_ID_TIMER = "docat_timer_channel"
        const val CHANNEL_ID_ALARM = "docat_alarm_channel"
        const val NOTIFICATION_ID_TIMER = 1
        const val NOTIFICATION_ID_ALARM = 2
    }

    fun createNotificationChannels() {
        val timerChannel = NotificationChannel(
            CHANNEL_ID_TIMER,
            "Temporizador activo",
            NotificationManager.IMPORTANCE_LOW
        ).apply {
            description = "Muestra el estado del temporizador en curso"
            setSound(null, null)
        }
        val alarmChannel = NotificationChannel(
            CHANNEL_ID_ALARM,
            "Alertas de actividad",
            NotificationManager.IMPORTANCE_HIGH
        ).apply {
            description = "Notificaciones al cambiar de actividad o finalizar"
        }
        notificationManager.createNotificationChannel(timerChannel)
        notificationManager.createNotificationChannel(alarmChannel)
    }

    fun buildTimerNotification(taskName: String, timeRemaining: String): Notification {
        val intent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_SINGLE_TOP
        }
        val pendingIntent = PendingIntent.getActivity(
            context, 0, intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
        return NotificationCompat.Builder(context, CHANNEL_ID_TIMER)
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setContentTitle(taskName)
            .setContentText(timeRemaining)
            .setContentIntent(pendingIntent)
            .setOngoing(true)
            .setSilent(true)
            .setForegroundServiceBehavior(NotificationCompat.FOREGROUND_SERVICE_IMMEDIATE)
            .build()
    }

    fun showAlarmNotification(alarmType: AlarmType) {
        val (title, text) = when (alarmType) {
            AlarmType.ACTIVITY_CHANGE -> "Cambio de actividad" to "Pasando a la siguiente subtarea"
            AlarmType.SERIES_COMPLETE -> "Serie completada" to "Terminaste todas las repeticiones de esta tarea"
            AlarmType.ALL_DONE -> "Día completado" to "Terminaste todas las tareas del día"
        }
        val notification = NotificationCompat.Builder(context, CHANNEL_ID_ALARM)
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setContentTitle(title)
            .setContentText(text)
            .setAutoCancel(true)
            .build()
        notificationManager.notify(NOTIFICATION_ID_ALARM, notification)
    }

    fun updateTimerNotification(taskName: String, timeRemaining: String) {
        notificationManager.notify(NOTIFICATION_ID_TIMER, buildTimerNotification(taskName, timeRemaining))
    }
}