package com.smart.docat.core.service

data class TimerState(
    val isRunning: Boolean = false,
    val isWorkPhase: Boolean = true,
    val secondsRemaining: Int = 0,
    val currentTaskIndex: Int = 0,
    val currentSubTaskIndex: Int = 0,
    val currentRepetition: Int = 0,
    val currentTaskName: String = "",
    val currentSubTaskName: String = "",
    val totalTasks: Int = 0,
    val totalRepetitions: Int = 0
)