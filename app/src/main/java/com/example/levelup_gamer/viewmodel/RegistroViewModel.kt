package com.example.levelup_gamer.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.levelup_gamer.components.validation.ValidationResult
import com.example.levelup_gamer.model.User
import com.example.levelup_gamer.repository.UserRepository
import com.example.levelup_gamer.utils.FormValidator
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class RegistroViewModel(private val userRepository: UserRepository) : ViewModel() {

    private val _cargando = MutableStateFlow(false)
    val cargando: StateFlow<Boolean> = _cargando

    // 1. Reemplazamos el booleano por un estado que contendrá el usuario creado.
    private val _usuarioRegistrado = MutableStateFlow<User?>(null)
    val usuarioRegistrado: StateFlow<User?> = _usuarioRegistrado

    private val _errorMensaje = MutableStateFlow("")
    val errorMensaje: StateFlow<String> = _errorMensaje

    private val _emailError = MutableStateFlow("")
    val emailError: StateFlow<String> = _emailError

    private val _passwordError = MutableStateFlow("")
    val passwordError: StateFlow<String> = _passwordError

    private val _confirmPasswordError = MutableStateFlow("")
    val confirmPasswordError: StateFlow<String> = _confirmPasswordError

    private val _nameError = MutableStateFlow("")
    val nameError: StateFlow<String> = _nameError

    fun validateEmail(email: String): Boolean {
        val result = FormValidator.isValidEmail(email)
        _emailError.value = if (result is ValidationResult.Error) result.message else ""
        return result is ValidationResult.Success
    }

    fun validatePassword(password: String): Boolean {
        val result = FormValidator.isValidPassword(password)
        _passwordError.value = if (result is ValidationResult.Error) result.message else ""
        return result is ValidationResult.Success
    }

    fun validateConfirmPassword(password: String, confirmPassword: String): Boolean {
        val result = FormValidator.doPasswordsMatch(password, confirmPassword)
        _confirmPasswordError.value = if (result is ValidationResult.Error) result.message else ""
        return result is ValidationResult.Success
    }

    fun validateName(name: String): Boolean {
        val result = FormValidator.isValidName(name)
        _nameError.value = if (result is ValidationResult.Error) result.message else ""
        return result is ValidationResult.Success
    }

    fun registroUsuario(correo: String, clave: String, confirmarClave: String, nombre: String) {
        val isEmailValid = validateEmail(correo)
        val isPasswordValid = validatePassword(clave)
        val isConfirmValid = validateConfirmPassword(clave, confirmarClave)
        val isNameValid = validateName(nombre)

        if (!isEmailValid || !isPasswordValid || !isConfirmValid || !isNameValid) {
            _errorMensaje.value = "Por favor corrige los errores del formulario"
            return
        }

        _cargando.value = true
        _errorMensaje.value = ""

        viewModelScope.launch {
            val result = userRepository.createUser(username = nombre, email = correo, password = clave)
            _cargando.value = false
            
            // 2. En caso de éxito, guardamos el usuario completo en el nuevo estado.
            result.onSuccess { user ->
                _usuarioRegistrado.value = user
            }.onFailure { exception ->
                _errorMensaje.value = exception.message ?: "Error desconocido durante el registro"
            }
        }
    }

    fun limpiarRegistro() {
        // 3. Limpiamos el nuevo estado.
        _usuarioRegistrado.value = null
        _errorMensaje.value = ""
        _emailError.value = ""
        _passwordError.value = ""
        _confirmPasswordError.value = ""
        _nameError.value = ""
    }
}