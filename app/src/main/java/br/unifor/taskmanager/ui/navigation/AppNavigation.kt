package br.unifor.taskmanager.ui.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import br.unifor.taskmanager.ui.screens.*
import br.unifor.taskmanager.ui.viewmodel.AuthViewModel
import br.unifor.taskmanager.ui.viewmodel.TaskViewModel

sealed class Screen(val route: String) {
    object Login : Screen("login")
    object Register : Screen("register")
    object TaskList : Screen("task_list")
    object TaskForm : Screen("task_form?taskId={taskId}") {
        fun createRoute(taskId: String?) = if (taskId != null) "task_form?taskId=$taskId" else "task_form"
    }
}

@Composable
fun AppNavigation(
    modifier: Modifier = Modifier,
    navController: NavHostController = rememberNavController(),
    authViewModel: AuthViewModel = viewModel(),
    taskViewModel: TaskViewModel = viewModel()
) {
    val startDestination = if (authViewModel.userLoggedIn.value) Screen.TaskList.route else Screen.Login.route

    NavHost(
        navController = navController,
        startDestination = startDestination,
        modifier = modifier
    ) {
        composable(Screen.Login.route) {
            LoginScreen(
                viewModel = authViewModel,
                onLoginSuccess = {
                    navController.navigate(Screen.TaskList.route) {
                        popUpTo(Screen.Login.route) { inclusive = true }
                    }
                },
                onNavigateToRegister = {
                    navController.navigate(Screen.Register.route)
                }
            )
        }

        composable(Screen.Register.route) {
            RegisterScreen(
                viewModel = authViewModel,
                onRegisterSuccess = {
                    navController.navigate(Screen.TaskList.route) {
                        popUpTo(Screen.Login.route) { inclusive = true }
                    }
                },
                onNavigateToLogin = {
                    navController.navigate(Screen.Login.route)
                }
            )
        }

        composable(Screen.TaskList.route) {
            TaskListScreen(
                authViewModel = authViewModel,
                taskViewModel = taskViewModel,
                onAddTaskClick = {
                    navController.navigate(Screen.TaskForm.createRoute(null))
                },
                onEditTaskClick = { taskId ->
                    navController.navigate(Screen.TaskForm.createRoute(taskId))
                },
                onLogout = {
                    navController.navigate(Screen.Login.route) {
                        popUpTo(Screen.TaskList.route) { inclusive = true }
                    }
                }
            )
        }

        composable(
            route = Screen.TaskForm.route,
            arguments = listOf(navArgument("taskId") { 
                type = NavType.StringType
                nullable = true
                defaultValue = null
            })
        ) { backStackEntry ->
            val taskId = backStackEntry.arguments?.getString("taskId")
            TaskFormScreen(
                authViewModel = authViewModel,
                taskViewModel = taskViewModel,
                taskId = taskId,
                onNavigateBack = {
                    navController.popBackStack()
                }
            )
        }
    }
}
