package com.smart.docat.core.service

import android.app.Service
import android.content.Intent
import android.os.Binder
import android.os.IBinder
import com.smart.docat.core.alarm.AlarmScheduler
import com.smart.docat.core.alarm.AlarmType
import com.smart.docat.core.audio.AmbientSoundPlayer
import com.smart.docat.core.notification.NotificationHelper
import com.smart.docat.core.utils.TimeFormatter
import com.smart.docat.data.repository.SessionHistoryRepository
import com.smart.docat.data.repository.TaskRepository
import com.smart.docat.domain.model.SessionHistory
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class TimerService : Service() {

    @Inject lateinit var taskRepository: TaskRepository
    @Inject lateinit var sessionHistoryRepository: SessionHistoryRepository
    @Inject lateinit var alarmScheduler: AlarmScheduler
    @Inject lateinit var notificationHelper: NotificationHelper
    @Inject lateinit var ambientSoundPlayer: AmbientSoundPlayer
    @Inject lateinit var timeFormatter: TimeFormatter

    private val serviceScope = CoroutineScope(Dispatchers.Default + SupervisorJob())
    private val _state = MutableStateFlow(TimerState())
    val state: StateFlow<TimerState> = _state.asStateFlow()

    private var timerJob: Job? = null

    inner class TimerBinder : Binder() {
        fun getService(): TimerService = this@TimerService
    }

    companion object {
        const val ACTION_START = "com.smart.docat.action.START"
        const val ACTION_STOP = "com.smart.docat.action.STOP"
        const val EXTRA_DATE = "extra_date"
    }

    override fun onBind(intent: Intent): IBinder = TimerBinder()

    override fun onCreate() {
        super.onCreate()
        notificationHelper.createNotificationChannels()
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        when (intent?.action) {
            ACTION_START -> intent.getStringExtra(EXTRA_DATE)?.let { startTimer(it) }
            ACTION_STOP -> stopTimer()
        }
        return START_STICKY
    }

    private fun startTimer(date: String) {
        timerJob?.cancel()
        timerJob = serviceScope.launch {
            val tasks = taskRepository.getTasksForDate(date).first()
            if (tasks.isEmpty()) { stopSelf(); return@launch }

            startForeground(
                NotificationHelper.NOTIFICATION_ID_TIMER,
                notificationHelper.buildTimerNotification(
                    tasks.first().nombre,
                    timeFormatter.formatSeconds(tasks.first().subTareas.first().tiempoAsignado * 60)
                )
            )

            _state.update { it.copy(isRunning = true, totalTasks = tasks.size) }

            for ((taskIndex, task) in tasks.withIndex()) {
                _state.update { it.copy(
                    currentTaskIndex = taskIndex,
                    currentTaskName = task.nombre,
                    totalRepetitions = task.repeticiones
                ) }

                val sessionStart = System.currentTimeMillis()

                for (rep in 0 until task.repeticiones) {
                    _state.update { it.copy(currentRepetition = rep) }

                    for ((subIndex, subTask) in task.subTareas.withIndex()) {

                        // --- FASE DE TRABAJO ---
                        _state.update { it.copy(
                            isWorkPhase = true,
                            currentSubTaskIndex = subIndex,
                            currentSubTaskName = subTask.nombre,
                            secondsRemaining = subTask.tiempoAsignado * 60
                        ) }

                        notificationHelper.showAlarmNotification(AlarmType.WORK_START)
                        ambientSoundPlayer.resume()

                        countDown(subTask.tiempoAsignado * 60)

                        val isLastRep = rep == task.repeticiones - 1
                        val isLastSubTask = subIndex == task.subTareas.size - 1

                        // --- FASE DE DESCANSO ---
                        if (!(isLastRep && isLastSubTask) && task.tiempoDescanso > 0) {

                            _state.update { it.copy(
                                isWorkPhase = false,
                                secondsRemaining = task.tiempoDescanso * 60
                            ) }

                            notificationHelper.showAlarmNotification(AlarmType.REST_START)
                            ambientSoundPlayer.pause()

                            countDown(task.tiempoDescanso * 60)
                        }
                    }
                }

                val elapsedSeconds = ((System.currentTimeMillis() - sessionStart) / 1000).toInt()
                sessionHistoryRepository.saveSession(
                    SessionHistory(tareaId = task.id, fecha = date, tiempoReal = elapsedSeconds)
                )

                val isLastTask = taskIndex == tasks.size - 1
                if (!isLastTask) {
                    notificationHelper.showAlarmNotification(AlarmType.SERIES_COMPLETE)
                }
            }

            // --- FINALIZACIÓN DE TODO ---
            notificationHelper.showAlarmNotification(AlarmType.ALL_DONE)
            ambientSoundPlayer.stop()
            _state.update { it.copy(isRunning = false) }
            stopForeground(STOP_FOREGROUND_REMOVE)
            stopSelf()
        }
    }

    private suspend fun countDown(totalSeconds: Int) {
        val endTime = System.currentTimeMillis() + (totalSeconds * 1000L)

        while (System.currentTimeMillis() < endTime) {
            if (!_state.value.isRunning) break

            val remainingMillis = endTime - System.currentTimeMillis()
            val remainingSeconds = (remainingMillis / 1000).toInt()

            if (remainingSeconds != _state.value.secondsRemaining) {
                _state.update { it.copy(secondsRemaining = remainingSeconds) }
                notificationHelper.updateTimerNotification(
                    _state.value.currentTaskName,
                    timeFormatter.formatSeconds(remainingSeconds)
                )
            }

            delay(100L)
        }
        _state.update { it.copy(secondsRemaining = 0) }
    }

    fun stopTimer() {
        timerJob?.cancel()
        ambientSoundPlayer.stop()
        _state.update { it.copy(isRunning = false) }
        stopForeground(STOP_FOREGROUND_REMOVE)
        stopSelf()
    }

    override fun onDestroy() {
        super.onDestroy()
        serviceScope.cancel()
    }
}