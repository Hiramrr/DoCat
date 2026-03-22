package com.smart.docat.core.alarm

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.smart.docat.core.notification.NotificationHelper
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class AlarmReceiver : BroadcastReceiver() {

    @Inject lateinit var notificationHelper: NotificationHelper

    override fun onReceive(context: Context, intent: Intent) {
        val typeName = intent.getStringExtra(EXTRA_ALARM_TYPE) ?: return
        val alarmType = runCatching { AlarmType.valueOf(typeName) }.getOrNull() ?: return
        notificationHelper.showAlarmNotification(alarmType)
    }

    companion object {
        const val EXTRA_ALARM_TYPE = "extra_alarm_type"
    }
}