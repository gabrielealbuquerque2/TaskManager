package br.unifor.taskmanager.data.model

data class Task(
    var id: String = "",
    var name: String = "",
    var description: String = "",
    var completed: Boolean = false,
    var createdAt: Long = System.currentTimeMillis()
)