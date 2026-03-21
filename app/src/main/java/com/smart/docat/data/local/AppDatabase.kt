package com.smart.docat.data.local

import android.content.Context
import androidx.databinding.adapters.Converters
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.smart.docat.data.local.dao.SessionHistoryDao
import com.smart.docat.data.local.dao.SubTaskDao
import com.smart.docat.data.local.dao.TaskDao
import com.smart.docat.data.local.entity.SessionHistoryEntity
import com.smart.docat.data.local.entity.TaskEntity
import com.smart.docat.data.local.entity.SubTaskEntity

@TypeConverters(Converters::class)
@Database(
    entities = [
        TaskEntity::class,
        SubTaskEntity::class,
        SessionHistoryEntity::class
    ],
    version = 1,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun taskDao(): TaskDao
    abstract fun subTaskDao(): SubTaskDao
    abstract fun sessionHistoryDao(): SessionHistoryDao

    companion object{
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "app_database"
                ).build()
                INSTANCE = instance
                instance
            }
        }
    }
}