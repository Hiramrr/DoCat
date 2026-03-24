package com.smart.docat.ui.timer

import android.app.Application
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.os.IBinder
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.smart.docat.core.service.TimerService
import com.smart.docat.core.service.TimerState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class TimerViewModel @Inject constructor(
    application: Application
) : AndroidViewModel(application) {

    private val _timerState = MutableStateFlow(TimerState())
    val timerState: StateFlow<TimerState> = _timerState.asStateFlow()

    private var isBound = false
    private var timerService: TimerService? = null

    private val connection = object : ServiceConnection {
        override fun onServiceConnected(className: ComponentName, service: IBinder) {
            val binder = service as TimerService.TimerBinder
            timerService = binder.getService()
            isBound = true

            viewModelScope.launch {
                timerService?.state?.collect { state ->
                    _timerState.value = state
                }
            }
        }

        override fun onServiceDisconnected(arg0: ComponentName) {
            isBound = false
            timerService = null
        }
    }

    init {
        Intent(application, TimerService::class.java).also { intent ->
            application.bindService(intent, connection, Context.BIND_AUTO_CREATE)
        }
    }

    override fun onCleared() {
        super.onCleared()
        if (isBound) {
            getApplication<Application>().unbindService(connection)
            isBound = false
        }
    }

    fun stopTimer() {
        timerService?.stopTimer()
    }

    fun startDailyTimer(date: String, taskIds: LongArray, interTaskRest: Int) {
        val intent = Intent(getApplication(), TimerService::class.java).apply {
            action = TimerService.ACTION_START
            putExtra(TimerService.EXTRA_DATE, date)
            putExtra(TimerService.EXTRA_TASK_IDS, taskIds) // Ahora mandamos el Array
            putExtra(TimerService.EXTRA_INTER_TASK_REST, interTaskRest) // El tiempo de transición
        }
        getApplication<Application>().startService(intent)
    }
}