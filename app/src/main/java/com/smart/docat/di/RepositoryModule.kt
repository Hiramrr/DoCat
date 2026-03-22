package com.smart.docat.di

import com.smart.docat.data.local.dao.SessionHistoryDao
import com.smart.docat.data.local.dao.SubTaskDao
import com.smart.docat.data.local.dao.TaskDao
import com.smart.docat.data.repository.SessionHistoryRepository
import com.smart.docat.data.repository.TaskRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object RepositoryModule {

    @Provides
    @Singleton
    fun provideTaskRepository(
        taskDao: TaskDao,
        subTaskDao: SubTaskDao
    ): TaskRepository = TaskRepository(taskDao, subTaskDao)

    @Provides
    @Singleton
    fun provideSessionHistoryRepository(
        sessionHistoryDao: SessionHistoryDao
    ): SessionHistoryRepository = SessionHistoryRepository(sessionHistoryDao)
}