package com.smart.docat.core.service

import android.app.Service
import android.content.Intent
import android.os.Binder
import android.os.IBinder
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
        const val EXTRA_TASK_IDS = "extra_task_ids"
        const val EXTRA_INTER_TASK_REST = "extra_inter_task_rest"
        const val ACTION_TOGGLE_PAUSE = "com.smart.docat.action.TOGGLE_PAUSE"
    }

    override fun onBind(intent: Intent): IBinder = TimerBinder()

    override fun onCreate() {
        super.onCreate()
        notificationHelper.createNotificationChannels()
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        when (intent?.action) {
            ACTION_START -> {
                val date = intent.getStringExtra(EXTRA_DATE)
                val taskIds = intent.getLongArrayExtra(EXTRA_TASK_IDS) ?: longArrayOf()
                val interTaskRest = intent.getIntExtra(EXTRA_INTER_TASK_REST, 0)
                if (date != null) startTimer(date, taskIds, interTaskRest)
            }
            ACTION_TOGGLE_PAUSE -> togglePause()
            ACTION_STOP -> stopTimer()
        }
        return START_STICKY
    }

    private fun togglePause() {
        val isCurrentlyPaused = _state.value.isPaused
        _state.update { it.copy(isPaused = !isCurrentlyPaused) }

        if (!isCurrentlyPaused) {
            ambientSoundPlayer.pause()
            notificationHelper.updateTimerNotification("Pausado", "La misión está en pausa")
        } else {
            if (_state.value.isWorkPhase) ambientSoundPlayer.resume()
        }
    }

    private fun startTimer(date: String, taskIds: LongArray, interTaskRest: Int) {
        timerJob?.cancel()
        timerJob = serviceScope.launch {
            val allTasks = taskRepository.getTasksForDate(date).first()

            // Filtramos las tareas según la lista que mandó el usuario desde el diálogo
            val tasks = if (taskIds.isNotEmpty()) allTasks.filter { taskIds.contains(it.id) } else allTasks

            if (tasks.isEmpty()) { stopSelf(); return@launch }

            startForeground(
                NotificationHelper.NOTIFICATION_ID_TIMER,
                notificationHelper.buildTimerNotification(
                    tasks.first().nombre,
                    timeFormatter.formatSeconds(tasks.first().subTareas.first().tiempoAsignado)
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
                        _state.update { it.copy(
                            isWorkPhase = true,
                            currentSubTaskIndex = subIndex,
                            currentSubTaskName = subTask.nombre,
                            secondsRemaining = subTask.tiempoAsignado
                        ) }

                        notificationHelper.showAlarmNotification(AlarmType.WORK_START)
                        ambientSoundPlayer.resume()

                        countDown(subTask.tiempoAsignado)

                        val isLastRep = rep == task.repeticiones - 1
                        val isLastSubTask = subIndex == task.subTareas.size - 1

                        if (!(isLastRep && isLastSubTask) && task.tiempoDescanso > 0) {
                            _state.update { it.copy(
                                isWorkPhase = false,
                                secondsRemaining = task.tiempoDescanso
                            ) }

                            notificationHelper.showAlarmNotification(AlarmType.REST_START)
                            ambientSoundPlayer.pause()
                            countDown(task.tiempoDescanso)
                        }
                    }
                }

                val elapsedSeconds = ((System.currentTimeMillis() - sessionStart) / 1000).toInt()
                sessionHistoryRepository.saveSession(
                    SessionHistory(tareaId = task.id, fecha = date, tiempoReal = elapsedSeconds)
                )

                val isLastTask = taskIndex == tasks.size - 1

                if (!isLastTask) {
                    // Terminó una tarea, pero aún faltan otras. Lanzamos alerta de completado.
                    notificationHelper.showAlarmNotification(AlarmType.SERIES_COMPLETE)

                    // NUEVO: Tiempo de descanso entre TAREAS DISTINTAS
                    if (interTaskRest > 0) {
                        _state.update { it.copy(
                            isWorkPhase = false,
                            secondsRemaining = interTaskRest,
                            currentSubTaskName = "Transición a siguiente misión" // Mensaje claro para el usuario
                        ) }
                        // Lanzamos alerta de descanso y pausamos el ruido blanco
                        notificationHelper.showAlarmNotification(AlarmType.REST_START)
                        ambientSoundPlayer.pause()
                        countDown(interTaskRest)
                    }
                }
            }

            notificationHelper.showAlarmNotification(AlarmType.ALL_DONE)
            ambientSoundPlayer.stop()
            _state.update { it.copy(isRunning = false) }
            stopForeground(STOP_FOREGROUND_REMOVE)
            stopSelf()
        }
    }

    private suspend fun countDown(totalSeconds: Int) {
        var remaining = totalSeconds
        var endTime = System.currentTimeMillis() + (remaining * 1000L)

        while (remaining > 0) {
            if (!_state.value.isRunning) break

            if (_state.value.isPaused) {
                delay(100L)
                endTime = System.currentTimeMillis() + (remaining * 1000L) // Empujamos el tiempo
                continue
            }

            val remainingMillis = endTime - System.currentTimeMillis()
            remaining = (remainingMillis / 1000).toInt()

            if (remaining != _state.value.secondsRemaining) {
                _state.update { it.copy(secondsRemaining = remaining) }
                notificationHelper.updateTimerNotification(
                    _state.value.currentTaskName,
                    timeFormatter.formatSeconds(remaining)
                )
            }
            delay(100L)
        }

        if (_state.value.isRunning) {
            _state.update { it.copy(secondsRemaining = 0) }
        }
    }
    private fun stopTimer() {
        timerJob?.cancel()
        ambientSoundPlayer.stop()
        _state.update { it.copy(isRunning = false, isPaused = false) }
        stopForeground(STOP_FOREGROUND_REMOVE)
        stopSelf()
    }
    override fun onDestroy() {
        super.onDestroy()
        serviceScope.cancel()
    }
}