package com.example.levelup_gamer.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.levelup_gamer.model.Usuario
import com.example.levelup_gamer.repository.AutRepository
import com.example.levelup_gamer.utils.FormValidator
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class LoginViewModel : ViewModel() {
    private val repositorio = AutRepository()

    private val _user = MutableStateFlow<Usuario?>(null)
    val user: StateFlow<Usuario?> = _user

    private val _carga = MutableStateFlow(false)
    val carga: StateFlow<Boolean> = _carga

    // Nuevos estados para manejo de errores
    private val _emailError = MutableStateFlow("")
    val emailError: StateFlow<String> = _emailError

    private val _passwordError = MutableStateFlow("")
    val passwordError: StateFlow<String> = _passwordError

    private val _loginError = MutableStateFlow("")
    val loginError: StateFlow<String> = _loginError

    fun validateEmail(email: String): Boolean {
        val result = FormValidator.isValidEmail(email)
        _emailError.value = if (result is com.example.levelup_gamer.ui.components.validation.ValidationResult.Error)
            result.message else ""
        return result.isValid
    }

    fun validatePassword(password: String): Boolean {
        val result = if (password.isEmpty()) {
            com.example.levelup_gamer.ui.components.validation.ValidationResult.Error("La contraseña es obligatoria")
        } else {
            com.example.levelup_gamer.ui.components.validation.ValidationResult.Success
        }
        _passwordError.value = if (result is com.example.levelup_gamer.ui.components.validation.ValidationResult.Error)
            result.message else ""
        return result.isValid
    }

    fun login(correo: String, clave: String) {
        // Validar campos antes de proceder
        val isEmailValid = validateEmail(correo)
        val isPasswordValid = validatePassword(clave)

        if (!isEmailValid || !isPasswordValid) {
            _loginError.value = "Por favor corrige los errores del formulario"
            return
        }

        _carga.value = true
        _loginError.value = ""

        viewModelScope.launch {
            try {
                val usuario = repositorio.login(correo, clave)
                if (usuario != null) {
                    _user.value = usuario
                    _loginError.value = ""
                } else {
                    _loginError.value = "Credenciales incorrectas. Verifica tu correo y contraseña"
                }
            } catch (e: Exception) {
                _loginError.value = when (e.message) {
                    "The password is invalid or the user does not have a password." ->
                        "Contraseña incorrecta"
                    "There is no user record corresponding to this identifier. The user may have been deleted." ->
                        "Usuario no encontrado"
                    "A network error (such as timeout, interrupted connection or unreachable host) has occurred." ->
                        "Error de conexión. Verifica tu internet"
                    else -> "Error al iniciar sesión: ${e.message}"
                }
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