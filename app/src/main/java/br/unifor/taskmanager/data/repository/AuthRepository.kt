package br.unifor.taskmanager.data.repository

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase

class AuthRepository {

    private val auth = FirebaseAuth.getInstance()
    private val database = FirebaseDatabase.getInstance().reference

    fun login(email: String, password: String, onResult: (Boolean, String?) -> Unit) {
        auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener {
                if (it.isSuccessful) {
                    onResult(true, null)
                } else {
                    onResult(false, it.exception?.message)
                }
            }
            .addOnFailureListener { e ->
                onResult(false, e.message)
            }
    }

    fun register(name: String, email: String, password: String, onResult: (Boolean, String?) -> Unit) {
        auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val uid = auth.currentUser?.uid
                    if (uid != null) {
                        database.child("users").child(uid).child("name").setValue(name)
                            .addOnCompleteListener { dbTask ->
                                if (dbTask.isSuccessful) {
                                    onResult(true, null)
                                } else {
                                    onResult(false, dbTask.exception?.message)
                                }
                            }
                    } else {
                        onResult(true, null)
                    }
                } else {
                    onResult(false, task.exception?.message)
                }
            }
    }

    fun logout() {
        auth.signOut()
    }

    fun getUserId(): String? {
        return auth.currentUser?.uid
    }
    
    fun isUserLoggedIn(): Boolean {
        return auth.currentUser != null
    }
}
