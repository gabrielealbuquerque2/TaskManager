package br.unifor.taskmanager.data.repository

import br.unifor.taskmanager.data.model.Task
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class TaskRepository {

    private val database = FirebaseDatabase.getInstance().reference

    fun getTasks(userId: String, onResult: (List<Task>) -> Unit) {
        database.child("users").child(userId).child("tasks")
            .addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val tasks = mutableListOf<Task>()
                    snapshot.children.forEach { child ->
                        val task = child.getValue(Task::class.java)
                        if (task != null) {
                            task.id = child.key ?: ""
                            tasks.add(task)
                        }
                    }
                    onResult(tasks)
                }

                override fun onCancelled(error: DatabaseError) {
                    onResult(emptyList())
                }
            })
    }

    fun saveTask(userId: String, task: Task, onResult: (Boolean) -> Unit) {
        val taskId = if (task.id.isEmpty()) {
            database.child("users").child(userId).child("tasks").push().key ?: return
        } else {
            task.id
        }
        
        task.id = taskId
        database.child("users").child(userId).child("tasks").child(taskId).setValue(task)
            .addOnCompleteListener { onResult(it.isSuccessful) }
    }

    fun deleteTask(userId: String, taskId: String, onResult: (Boolean) -> Unit) {
        database.child("users").child(userId).child("tasks").child(taskId).removeValue()
            .addOnCompleteListener { onResult(it.isSuccessful) }
    }

    fun toggleTaskCompletion(userId: String, task: Task, onResult: (Boolean) -> Unit) {
        val updatedTask = task.copy(completed = !task.completed)
        database.child("users").child(userId).child("tasks").child(task.id).setValue(updatedTask)
            .addOnCompleteListener { onResult(it.isSuccessful) }
    }
    
    fun getTaskById(userId: String, taskId: String, onResult: (Task?) -> Unit) {
        database.child("users").child(userId).child("tasks").child(taskId)
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val task = snapshot.getValue(Task::class.java)
                    task?.id = snapshot.key ?: ""
                    onResult(task)
                }

                override fun onCancelled(error: DatabaseError) {
                    onResult(null)
                }
            })
    }
}
