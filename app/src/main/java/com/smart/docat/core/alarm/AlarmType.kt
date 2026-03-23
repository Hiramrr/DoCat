package com.smart.docat.core.alarm

enum class AlarmType {
    WORK_START,       // Inicio de trabajo
    REST_START,       // Inicio de descanso
    SERIES_COMPLETE,  // Terminó una tarea completa
    ALL_DONE          // Terminó todo el día
}