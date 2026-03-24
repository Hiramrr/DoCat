package com.smart.docat.ui.timer

import androidx.compose.animation.Crossfade
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import com.smart.docat.core.utils.TimeFormatter


@Composable
fun TimerScreen(
    taskIds: List<Long> = emptyList(),
    interTaskRest: Int = 0,
    viewModel: TimerViewModel = hiltViewModel(),
    onNavigateBack: () -> Unit
) {
    val state by viewModel.timerState.collectAsState()
    val timeFormatter = TimeFormatter()

    LaunchedEffect(Unit) {
        if (!state.isRunning) {
            viewModel.startDailyTimer(timeFormatter.formatDate(), taskIds.toLongArray(), interTaskRest)
        }
    }

    Surface(modifier = Modifier.fillMaxSize()) {
        Crossfade(
            targetState = state.isWorkPhase,
            animationSpec = tween(durationMillis = 800),
            label = "Timer Phase Transition"
        ) { isWorkPhase ->
            if (isWorkPhase) {
                WorkTimerScreen(
                    state = state,
                    onStopClick = {
                        viewModel.stopTimer()
                        onNavigateBack()
                    },
                    onPauseToggle = { viewModel.togglePause() }
                )
            } else {
                RestTimerScreen(
                    state = state,
                    onStopClick = {
                        viewModel.stopTimer()
                        onNavigateBack()
                    },
                    onPauseToggle = { viewModel.togglePause() }
                )
            }
        }
    }
}