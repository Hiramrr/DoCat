package com.smart.docat.core.utils

import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class TimeFormatter @Inject constructor() {

    fun formatSeconds(seconds: Int): String {
        val minutes = seconds / 60
        val secs = seconds % 60
        return "%02d:%02d".format(minutes, secs)
    }

    fun formatDate(date: Date = Date()): String {
        return SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(date)
    }
}