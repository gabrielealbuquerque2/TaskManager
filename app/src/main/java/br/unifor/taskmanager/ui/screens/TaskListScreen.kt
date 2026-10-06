package br.unifor.taskmanager.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ExitToApp
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import br.unifor.taskmanager.ui.components.TaskListItem
import br.unifor.taskmanager.ui.viewmodel.AuthViewModel
import br.unifor.taskmanager.ui.viewmodel.TaskViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TaskListScreen(
    authViewModel: AuthViewModel,
    taskViewModel: TaskViewModel,
    onAddTaskClick: () -> Unit,
    onEditTaskClick: (String) -> Unit,
    onLogout: () -> Unit,
    modifier: Modifier = Modifier
) {
    val tasks by taskViewModel.tasks.collectAsState()
    val userId = authViewModel.getUserId()

    if (userId == null) {
        Box(modifier = modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Text("Usuário não autenticado")
        }
        return
    }

    LaunchedEffect(userId) {
        if (userId.isNotEmpty()) {
            taskViewModel.getTasks(userId)
        }
    }

    Scaffold(
        modifier = modifier,
        topBar = {
            TopAppBar(
                title = { Text("Minhas Tarefas") },
                actions = {
                    IconButton(onClick = {
                        authViewModel.logout()
                        onLogout()
                    }) {
                        Icon(Icons.AutoMirrored.Filled.ExitToApp, contentDescription = "Sair")
                    }
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(onClick = onAddTaskClick) {
                Icon(Icons.Default.Add, contentDescription = "Nova Tarefa")
            }
        }
    ) { innerPadding ->
        if (tasks.isEmpty()) {
            Box(
                modifier = Modifier
                    .padding(innerPadding)
                    .fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Text("Nenhuma tarefa cadastrada.")
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .padding(innerPadding)
                    .fillMaxSize()
                    .padding(horizontal = 16.dp),
                contentPadding = PaddingValues(vertical = 8.dp)
            ) {
                items(tasks) { task ->
                    TaskListItem(
                        task = task,
                        onToggleCompletion = {
                            taskViewModel.toggleTaskCompletion(userId, task)
                        },
                        onDelete = {
                            taskViewModel.deleteTask(userId, task.id)
                        },
                        onClick = {
                            onEditTaskClick(task.id)
                        }
                    )
                }
            }
        }
    }
}
