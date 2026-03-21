package com.smart.docat.data.repository

import com.smart.docat.data.local.dao.TaskDao
import jakarta.inject.Inject

class TaskRepository @Inject constructor(
    private val taskDao: TaskDao
)