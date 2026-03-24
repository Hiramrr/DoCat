package com.smart.docat.ui.navigation

import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.MusicNote
import androidx.compose.material.icons.filled.TaskAlt
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.smart.docat.ui.calendar.CalendarScreen
import com.smart.docat.ui.calendar.CalendarViewModel

import com.smart.docat.ui.home.HomeScreen
import com.smart.docat.ui.home.HomeViewModel
import com.smart.docat.ui.tasklist.TaskListScreen
import com.smart.docat.ui.tasklist.TaskListViewModel
import com.smart.docat.ui.newtask.NewTaskScreen
import com.smart.docat.ui.newtask.NewTaskViewModel
import com.smart.docat.ui.ambient.AmbientSoundScreen

import com.smart.docat.ui.timer.TimerScreen
import com.smart.docat.ui.timer.TimerViewModel

sealed class Screen(val route: String) {
    object Calendar : Screen("calendar")
    object Home : Screen("home")
    object TaskList : Screen("tasklist")
    object Ambientsound : Screen("ambient")
    object NewTask : Screen("newtask?taskId={taskId}") {
        fun createRoute(taskId: Long? = null) =
            if (taskId == null) "newtask" else "newtask?taskId=$taskId"
    }

    object Timer : Screen("timer?taskIds={taskIds}&rest={rest}") {
        fun createRoute(taskIds: List<Long>, rest: Int = 0): String {
            val idsParam = taskIds.joinToString(",")
            return "timer?taskIds=$idsParam&rest=$rest"
        }
    }
}

private data class BottomNavItem(
    val screen: Screen,
    val label: String,
    val icon: androidx.compose.ui.graphics.vector.ImageVector
)

private val bottomNavItems = listOf(
    BottomNavItem(Screen.Calendar, "Calendario", Icons.Filled.CalendarMonth),
    BottomNavItem(Screen.Home, "Inicio", Icons.Filled.Home),
    BottomNavItem(Screen.TaskList, "Tareas", Icons.Filled.TaskAlt),
    BottomNavItem(Screen.Ambientsound, "Sonidos", Icons.Filled.MusicNote)
)

private val bottomNavRoutes = bottomNavItems.map { it.screen.route }.toSet()

@Composable
fun AppNavGraph() {
    val navController = rememberNavController()
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentDestination = navBackStackEntry?.destination

    val showBottomBar = currentDestination?.route in bottomNavRoutes

    Scaffold(
        bottomBar = {
            if (showBottomBar) {
                NavigationBar {
                    bottomNavItems.forEach { item ->
                        NavigationBarItem(
                            selected = currentDestination?.hierarchy
                                ?.any { it.route == item.screen.route } == true,
                            onClick = {
                                navController.navigate(item.screen.route) {
                                    popUpTo(navController.graph.findStartDestination().id) {
                                        saveState = true
                                    }
                                    launchSingleTop = true
                                    restoreState = true
                                }
                            },
                            icon = {
                                Icon(
                                    imageVector = item.icon,
                                    contentDescription = item.label
                                )
                            },
                            label = { Text(item.label) }
                        )
                    }
                }
            }
        }
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = Screen.Home.route,
            modifier = Modifier.padding(innerPadding)
        ) {

            composable(Screen.Home.route) {
                val viewModel = hiltViewModel<HomeViewModel>()
                HomeScreen(
                    viewModel = viewModel,
                    onStartActivityClick = { ids, rest ->
                        navController.navigate(Screen.Timer.createRoute(ids, rest))
                    },
                    onNavigateToTasksClick = { navController.navigate(Screen.TaskList.route) }
                )
            }

            composable(Screen.TaskList.route) {
                val viewModel = hiltViewModel<TaskListViewModel>()
                TaskListScreen(
                    viewModel = viewModel,
                    onNavigateToNewTask = { taskId ->
                        navController.navigate(Screen.NewTask.createRoute(taskId))
                    },
                    onNavigateToTimer = { taskId ->
                        navController.navigate(Screen.Timer.createRoute(listOf(taskId), 0))
                    },
                    onBackClick = { navController.popBackStack() }
                )
            }

            composable(Screen.Ambientsound.route) {
                AmbientSoundScreen()
            }

            composable(
                route = Screen.NewTask.route,
                arguments = listOf(
                    navArgument("taskId") {
                        type = NavType.LongType
                        defaultValue = -1L
                    }
                )
            ) { backStackEntry ->
                val taskIdArg = backStackEntry.arguments?.getLong("taskId") ?: -1L
                val taskId = if (taskIdArg == -1L) null else taskIdArg
                val viewModel = hiltViewModel<NewTaskViewModel>()
                NewTaskScreen(
                    taskId = taskId,
                    viewModel = viewModel,
                    onNavigateBack = { navController.popBackStack() }
                )
            }

            composable(
                route = Screen.Timer.route,
                arguments = listOf(
                    navArgument("taskIds") { type = NavType.StringType; defaultValue = "" },
                    navArgument("rest") { type = NavType.IntType; defaultValue = 0 }
                )
            ) { backStackEntry ->
                val idsString = backStackEntry.arguments?.getString("taskIds") ?: ""
                val rest = backStackEntry.arguments?.getInt("rest") ?: 0
                val taskIds = if (idsString.isNotBlank()) idsString.split(",").mapNotNull { it.toLongOrNull() } else emptyList()

                val viewModel = hiltViewModel<TimerViewModel>()
                TimerScreen(
                    taskIds = taskIds,
                    interTaskRest = rest,
                    viewModel = viewModel,
                    onNavigateBack = { navController.popBackStack() }
                )
            }

            composable(Screen.Calendar.route) {
                val viewModel = hiltViewModel<CalendarViewModel>()
                CalendarScreen(viewModel = viewModel)
            }
        }
    }
}