package com.smart.docat.di

import android.content.Context
import com.smart.docat.core.alarm.AlarmScheduler
import com.smart.docat.core.audio.AmbientSoundPlayer
import com.smart.docat.core.notification.NotificationHelper
import com.smart.docat.core.utils.TimeFormatter
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object ServiceModule {

    @Provides
    @Singleton
    fun provideAlarmScheduler(
        @ApplicationContext context: Context
    ): AlarmScheduler = AlarmScheduler(context)

    @Provides
    @Singleton
    fun provideNotificationHelper(
        @ApplicationContext context: Context
    ): NotificationHelper = NotificationHelper(context)

    @Provides
    @Singleton
    fun provideTimeFormatter(): TimeFormatter = TimeFormatter()
}