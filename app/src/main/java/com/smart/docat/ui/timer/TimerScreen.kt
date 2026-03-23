package com.smart.docat.ui.timer

import androidx.compose.animation.Crossfade
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel

@Composable
fun TimerScreen(
    viewModel: TimerViewModel = hiltViewModel(),
    onNavigateBack: () -> Unit
) {
    val state by viewModel.timerState.collectAsState()

    Surface(modifier = Modifier.fillMaxSize()) {
        // Crossfade crea una transición visual suave al cambiar de estado
        Crossfade(
            targetState = state.isWorkPhase,
            animationSpec = tween(durationMillis = 800), // 800ms de transición
            label = "Timer Phase Transition"
        ) { isWorkPhase ->
            if (isWorkPhase) {
                WorkTimerScreen(
                    state = state,
                    onStopClick = {
                        viewModel.stopTimer()
                        onNavigateBack()
                    }
                )
            } else {
                RestTimerScreen(
                    state = state,
                    onStopClick = {
                        viewModel.stopTimer()
                        onNavigateBack()
                    }
                )
            }
        }
    }
}