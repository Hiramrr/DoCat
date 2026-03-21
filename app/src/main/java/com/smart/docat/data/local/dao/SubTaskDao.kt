package com.smart.docat.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.smart.docat.data.local.entity.SubTaskEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface SubTaskDao{
    @Query("SELECT * FROM sub_tasks WHERE tarea_id = :tareaId ORDER BY orden ASC")
    fun getSubTasksForTask(tareaId: Long): Flow<List<SubTaskEntity>>

    @Query("DELETE FROM sub_tasks WHERE tarea_id = :tareaId")
    suspend fun deleteSubTasksForTask(tareaId: Long)

    @Insert
    suspend fun insertSubTask(task: SubTaskEntity): Long

    @Insert
    suspend fun insertSubTasks(subTasks: List<SubTaskEntity>)

    @Update
    suspend fun updateSubTask(task: SubTaskEntity)

    @Delete
    suspend fun deleteSubTask(task: SubTaskEntity)
}