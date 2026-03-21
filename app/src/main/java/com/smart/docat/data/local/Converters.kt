package com.smart.docat.data.local

import androidx.room.TypeConverter
import com.smart.docat.domain.model.TaskStatus

class Converters {

    @TypeConverter
    fun fromStatus(status: TaskStatus): String {
        return status.name
    }

    @TypeConverter
    fun toStatus(status : String): TaskStatus {
        return try{
            enumValueOf<TaskStatus>(status)
        }catch (e: IllegalArgumentException){
            TaskStatus.IN_PROGRESS
        }
    }
}