package br.unifor.taskmanager.ui.viewmodel

import androidx.lifecycle.ViewModel
import br.unifor.taskmanager.data.model.Task
import br.unifor.taskmanager.data.repository.TaskRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class TaskViewModel(private val repository: TaskRepository = TaskRepository()) : ViewModel() {

    private val _tasks = MutableStateFlow<List<Task>>(emptyList())
    val tasks: StateFlow<List<Task>> = _tasks

    fun getTasks(userId: String) {
        repository.getTasks(userId) { taskList ->
            _tasks.value = taskList
        }
    }

    fun saveTask(userId: String, task: Task, onResult: (Boolean) -> Unit) {
        repository.saveTask(userId, task, onResult)
    }

    fun deleteTask(userId: String, taskId: String) {
        repository.deleteTask(userId, taskId) { }
    }

    fun toggleTaskCompletion(userId: String, task: Task) {
        repository.toggleTaskCompletion(userId, task) { }
    }
    
    fun getTaskById(userId: String, taskId: String, onResult: (Task?) -> Unit) {
        repository.getTaskById(userId, taskId, onResult)
    }
}
