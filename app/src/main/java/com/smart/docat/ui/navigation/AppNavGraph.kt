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
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument

sealed class Screen(val route: String){
    object Calendar : Screen("calendar")
    object Home : Screen("home")
    object TaskList : Screen("tasklist")
    object Ambientsound : Screen("ambient")
    object NewTask : Screen("newtask?taskId={taskId}"){
        fun createRoute(taskId: Long? = null) =
            if (taskId == null) "newtask?taskId=$taskId" else "newtask"
    }
    object Timer : Screen("timer")
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

private val bottomNavRoutes = bottomNavItems.map {it.screen.route}.toSet()

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
    ) { innerPading ->
        NavHost(
            navController = navController,
            startDestination = Screen.Calendar.route,
            modifier = androidx.compose.ui.Modifier.padding(innerPading)
        ) {
            composable(Screen.Calendar.route) {
                //CalendarScreen(navController)
            }
            composable(Screen.Home.route) {
                //HomeScreen(navController)
            }
            composable(Screen.TaskList.route) {
                //TaskListScreen(navController)
            }
            composable(Screen.Ambientsound.route) {
                //AmbientSoundScreen(navController)
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
                val taskId = backStackEntry.arguments?.getLong("taskId")
                //NewTaskScreen(navController, taskId)
            }

            composable(Screen.Timer.route) {
                //TimerScreen(navController)
            }
        }
    }
}