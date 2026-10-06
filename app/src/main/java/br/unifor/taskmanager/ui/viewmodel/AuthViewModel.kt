package br.unifor.taskmanager.ui.viewmodel

import androidx.lifecycle.ViewModel
import br.unifor.taskmanager.data.repository.AuthRepository
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class AuthViewModel(private val repository: AuthRepository = AuthRepository()) : ViewModel() {

    private val _userLoggedIn = MutableStateFlow(repository.isUserLoggedIn())
    val userLoggedIn: StateFlow<Boolean> = _userLoggedIn

    fun login(email: String, senha: String, onResult: (Boolean, String?) -> Unit) {
        repository.login(email, senha) { sucesso, erro ->
            if (sucesso) _userLoggedIn.value = true
            onResult(sucesso, erro)
        }
    }

    fun register(nome: String, email: String, senha: String, onResult: (Boolean, String?) -> Unit) {
        repository.register(nome, email, senha) { sucesso, erro ->
            if (sucesso) _userLoggedIn.value = true
            onResult(sucesso, erro)
        }
    }

    fun logout() {
        repository.logout()
        _userLoggedIn.value = false
    }

    fun getUserId(): String? {
        return FirebaseAuth.getInstance().currentUser?.uid
    }
}
