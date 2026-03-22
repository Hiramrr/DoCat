package com.smart.docat.di

import android.content.Context
import com.smart.docat.core.audio.AmbientSoundPlayer
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AudioModule {

    @Provides
    @Singleton
    fun provideAmbientSoundPlayer(@ApplicationContext context: Context): AmbientSoundPlayer {
        return AmbientSoundPlayer(context)
    }
}