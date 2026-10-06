package br.unifor.taskmanager.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import br.unifor.taskmanager.data.model.Task
import br.unifor.taskmanager.ui.viewmodel.AuthViewModel
import br.unifor.taskmanager.ui.viewmodel.TaskViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TaskFormScreen(
    authViewModel: AuthViewModel,
    taskViewModel: TaskViewModel,
    taskId: String?,
    onNavigateBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    var name by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var isCompleted by remember { mutableStateOf(false) }
    var createdAt by remember { mutableLongStateOf(System.currentTimeMillis()) }
    var isLoading by remember { mutableStateOf(false) }
    
    val userId = authViewModel.getUserId() ?: ""

    LaunchedEffect(taskId) {
        if (taskId != null && userId.isNotEmpty()) {
            isLoading = true
            taskViewModel.getTaskById(userId, taskId) { task ->
                isLoading = false
                task?.let {
                    name = it.name
                    description = it.description
                    isCompleted = it.completed
                    createdAt = it.createdAt
                }
            }
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(if (taskId == null) "Nova Tarefa" else "Editar Tarefa") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Voltar")
                    }
                }
            )
        }
    ) { innerPadding ->
        if (isLoading) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = androidx.compose.ui.Alignment.Center) {
                CircularProgressIndicator()
            }
        } else {
            Column(
                modifier = Modifier
                    .padding(innerPadding)
                    .fillMaxSize()
                    .padding(16.dp)
            ) {
                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text("Nome da Tarefa") },
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(8.dp))

                OutlinedTextField(
                    value = description,
                    onValueChange = { description = it },
                    label = { Text("Descrição") },
                    modifier = Modifier.fillMaxWidth(),
                    minLines = 3
                )

                Spacer(modifier = Modifier.height(24.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    OutlinedButton(
                        onClick = onNavigateBack,
                        modifier = Modifier.weight(1f)
                    ) {
                        Text("Cancelar")
                    }

                    Button(
                        onClick = {
                            if (name.isBlank()) return@Button
                            
                            val task = Task(
                                id = taskId ?: "",
                                name = name,
                                description = description,
                                completed = isCompleted,
                                createdAt = createdAt
                            )
                            
                            taskViewModel.saveTask(userId, task) { success ->
                                if (success) {
                                    onNavigateBack()
                                }
                            }
                        },
                        modifier = Modifier.weight(1f)
                    ) {
                        Text("Salvar")
                    }
                }
            }
        }
    }
}
