package com.smart.docat.core.service

data class TimerState(
    val isRunning: Boolean = false,
    val isPaused: Boolean = false, // ¡NUEVO!
    val isWorkPhase: Boolean = true,
    val currentTaskName: String = "",
    val currentSubTaskName: String = "",
    val currentTaskIndex: Int = 0,
    val totalTasks: Int = 0,
    val currentRepetition: Int = 0,
    val totalRepetitions: Int = 0,
    val currentSubTaskIndex: Int = 0,
    val secondsRemaining: Int = 0
)