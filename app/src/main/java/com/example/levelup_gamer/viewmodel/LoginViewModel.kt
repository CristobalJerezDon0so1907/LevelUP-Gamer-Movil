package com.example.levelup_gamer.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.levelup_gamer.model.Usuario
import com.example.levelup_gamer.repository.AutRepository
import com.example.levelup_gamer.ui.components.validation.ValidationResult
import com.example.levelup_gamer.utils.FormValidator
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class LoginViewModel(private val repository: AutRepository) : ViewModel() {

    private val _user = MutableStateFlow<Usuario?>(null)
    val user: StateFlow<Usuario?> = _user.asStateFlow()

    private val _carga = MutableStateFlow(false)
    val carga: StateFlow<Boolean> = _carga.asStateFlow()

    private val _emailError = MutableStateFlow("")
    val emailError: StateFlow<String> = _emailError.asStateFlow()

    private val _passwordError = MutableStateFlow("")
    val passwordError: StateFlow<String> = _passwordError.asStateFlow()

    private val _loginError = MutableStateFlow("")
    val loginError: StateFlow<String> = _loginError.asStateFlow()

    fun validateEmail(email: String): Boolean {
        val result = FormValidator.isValidEmail(email)
        // CORREGIDO: Se comprueba el tipo de resultado con 'is' en lugar de acceder a propiedades inexistentes.
        if (result is ValidationResult.Error) {
            _emailError.value = result.message
            return false
        }
        _emailError.value = ""
        return true
    }

    fun validatePassword(password: String): Boolean {
        val isValid = password.isNotBlank()
        _passwordError.value = if (!isValid) "La contraseña es obligatoria" else ""
        return isValid
    }

    fun login(correo: String, clave: String) {
        if (!validateEmail(correo) || !validatePassword(clave)) {
            _loginError.value = "Por favor corrige los errores del formulario"
            return
        }

        _carga.value = true
        _loginError.value = ""

        viewModelScope.launch {
            try {
                val usuario = repository.login(correo, clave)
                _user.value = usuario
            } catch (e: Exception) {
                _loginError.value = "Error al iniciar sesión: ${e.message}"
            } finally {
                _carga.value = false
            }
        }
    }

    fun clearErrors() {
        _emailError.value = ""
        _passwordError.value = ""
        _loginError.value = ""
    }
}

// Factory para crear el LoginViewModel con su repositorio
class LoginViewModelFactory(private val repository: AutRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(LoginViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return LoginViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}