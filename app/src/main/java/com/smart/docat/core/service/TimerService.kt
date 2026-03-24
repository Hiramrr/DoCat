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
        const val EXTRA_TASK_ID = "extra_task_id"
    }

    override fun onBind(intent: Intent): IBinder = TimerBinder()

    override fun onCreate() {
        super.onCreate()
        // NOTA: Asegúrate de que NotificationHelper tenga este método.
        notificationHelper.createNotificationChannels()
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        when (intent?.action) {
            ACTION_START -> {
                val date = intent.getStringExtra(EXTRA_DATE)
                val taskId = intent.getLongExtra(EXTRA_TASK_ID, -1L) // Leemos el ID
                if (date != null) startTimer(date, taskId)
            }
            ACTION_STOP -> stopTimer()
        }
        return START_STICKY
    }

    private fun startTimer(date: String, targetTaskId: Long) {
        timerJob?.cancel()
        timerJob = serviceScope.launch {
            val allTasks = taskRepository.getTasksForDate(date).first()

            // NUEVO: Filtramos si se mandó un ID específico. Si es -1, agarra todas.
            val tasks = if (targetTaskId != -1L) allTasks.filter { it.id == targetTaskId } else allTasks

            if (tasks.isEmpty()) { stopSelf(); return@launch }

            startForeground(
                NotificationHelper.NOTIFICATION_ID_TIMER,
                notificationHelper.buildTimerNotification(
                    tasks.first().nombre,
                    // YA NO SE MULTIPLICA POR 60
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
                            // YA NO SE MULTIPLICA POR 60
                            secondsRemaining = subTask.tiempoAsignado
                        ) }

                        notificationHelper.showAlarmNotification(AlarmType.WORK_START)
                        ambientSoundPlayer.resume()

                        // YA NO SE MULTIPLICA POR 60
                        countDown(subTask.tiempoAsignado)

                        val isLastRep = rep == task.repeticiones - 1
                        val isLastSubTask = subIndex == task.subTareas.size - 1

                        if (!(isLastRep && isLastSubTask) && task.tiempoDescanso > 0) {
                            _state.update { it.copy(
                                isWorkPhase = false,
                                // YA NO SE MULTIPLICA POR 60
                                secondsRemaining = task.tiempoDescanso
                            ) }

                            notificationHelper.showAlarmNotification(AlarmType.REST_START)
                            ambientSoundPlayer.pause()

                            // YA NO SE MULTIPLICA POR 60
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
                    notificationHelper.showAlarmNotification(AlarmType.SERIES_COMPLETE)
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