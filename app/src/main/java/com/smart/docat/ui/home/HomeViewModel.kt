package com.smart.docat.ui.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.smart.docat.core.utils.TimeFormatter
import com.smart.docat.domain.model.Task
import com.smart.docat.domain.usecase.GetTasksUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    getTasksUseCase: GetTasksUseCase,
    timeFormatter: TimeFormatter
) : ViewModel() {

    val tasksToday: StateFlow<List<Task>> = getTasksUseCase(timeFormatter.formatDate())
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val hasTasksToday: StateFlow<Boolean> = tasksToday.map { it.isNotEmpty() }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), false)
}