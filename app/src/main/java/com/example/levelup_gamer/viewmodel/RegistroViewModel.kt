package com.example.levelup_gamer.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.levelup_gamer.repository.UsuarioRepository
import com.example.levelup_gamer.utils.FormValidator
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class RegistroViewModel : ViewModel() {
    private val repositorio = UsuarioRepository()

    private val _cargando = MutableStateFlow(false)
    val cargando: StateFlow<Boolean> = _cargando

    private val _registroExitoso = MutableStateFlow(false)
    val registroExitoso: StateFlow<Boolean> = _registroExitoso

    private val _errorMensaje = MutableStateFlow("")
    val errorMensaje: StateFlow<String> = _errorMensaje

    // Nuevos estados para errores específicos
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
        _emailError.value = if (result is com.example.levelup_gamer.ui.components.validation.ValidationResult.Error)
            result.message else ""
        return result.isValid
    }

    fun validatePassword(password: String): Boolean {
        val result = FormValidator.isValidPassword(password)
        _passwordError.value = if (result is com.example.levelup_gamer.ui.components.validation.ValidationResult.Error)
            result.message else ""
        return result.isValid
    }

    fun validateConfirmPassword(password: String, confirmPassword: String): Boolean {
        val result = FormValidator.doPasswordsMatch(password, confirmPassword)
        _confirmPasswordError.value = if (result is com.example.levelup_gamer.ui.components.validation.ValidationResult.Error)
            result.message else ""
        return result.isValid
    }

    fun validateName(name: String): Boolean {
        val result = FormValidator.isValidName(name)
        _nameError.value = if (result is com.example.levelup_gamer.ui.components.validation.ValidationResult.Error)
            result.message else ""
        return result.isValid
    }

    fun registroUsuario(correo: String, clave: String, confirmarClave: String, nombre: String) {
        // Validar todos los campos
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
            val exitoso = repositorio.registroUsuario(correo, clave, nombre)
            _cargando.value = false
            _registroExitoso.value = exitoso
            if (!exitoso) {
                _errorMensaje.value = "El correo ya está registrado. Intenta con otro."
            }
        }
    }

    fun limpiarRegistro() {
        _registroExitoso.value = false
        _errorMensaje.value = ""
        _emailError.value = ""
        _passwordError.value = ""
        _confirmPasswordError.value = ""
        _nameError.value = ""
    }
}