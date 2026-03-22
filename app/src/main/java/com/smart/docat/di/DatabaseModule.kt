package com.smart.docat.di

import android.content.Context
import androidx.room.Room
import com.smart.docat.data.local.AppDatabase
import com.smart.docat.data.local.dao.SessionHistoryDao
import com.smart.docat.data.local.dao.SubTaskDao
import com.smart.docat.data.local.dao.TaskDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    @Singleton
    fun provideAppDatabase(
        @ApplicationContext context: Context
    ): AppDatabase {
        return Room.databaseBuilder(
            context,
            AppDatabase::class.java,
            "docat_db"
        ).build()
    }

    @Provides
    fun provideTaskDao(database: AppDatabase): TaskDao {
        return database.taskDao()
    }

    @Provides
    fun provideSubTaskDao(database: AppDatabase): SubTaskDao {
        return database.subTaskDao()
    }

    @Provides
    fun provideSessionHistoryDao(database: AppDatabase): SessionHistoryDao {
        return database.sessionHistoryDao()
    }
}
