package com.example.levelup_gamer.viewmodel

import android.util.Patterns
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.levelup_gamer.repository.AuthRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class RegistroViewModel(
    private val repositorio: AuthRepository = AuthRepository()
) : ViewModel() {

    private val _cargando = MutableStateFlow(false)
    val cargando: StateFlow<Boolean> = _cargando

    private val _registroExitoso = MutableStateFlow(false)
    val registroExitoso: StateFlow<Boolean> = _registroExitoso

    private val _errorMensaje = MutableStateFlow("")
    val errorMensaje: StateFlow<String> = _errorMensaje

    fun registroUsuario(correo: String, clave: String, confirmarClave: String, nombre: String) {

        //Validacion de campos vacios
        if (correo.isBlank() || clave.isBlank() || confirmarClave.isBlank() || nombre.isBlank()) {
            _errorMensaje.value = "Todos los campos son obligatorios"
            return
        }

        //Validacion del nombre
        if (nombre.length < 3) {
            _errorMensaje.value = "El nombre debe tener al menos 3 caracteres"
            return
        }

        //Validacion de correo
        if (!Patterns.EMAIL_ADDRESS.matcher(correo).matches()) {
            _errorMensaje.value = "El correo no es válido"
            return
        }

        val dominiosPermitidos = listOf("gmail.com", "hotmail.com", "outlook.com")

        val dominioCorreo = correo.substringAfterLast("@")

        if (dominioCorreo !in dominiosPermitidos) {
            _errorMensaje.value = "Solo se permiten correos Gmail, Hotmail u Outlook"
            return
        }


        //Validacion de clave
        if (clave.length < 6) {
            _errorMensaje.value = "La contraseña debe tener mínimo 6 caracteres"
            return
        }

        if (clave != confirmarClave) {
            _errorMensaje.value = "Las contraseñas no coinciden"
            return
        }

        _cargando.value = true
        _errorMensaje.value = ""

        viewModelScope.launch {
            val exitoso = repositorio.registroUsuario(correo, clave, nombre)

            _cargando.value = false
            _registroExitoso.value = exitoso

            if (!exitoso) {
                _errorMensaje.value = "Error al registrar usuario"
            }
        }
    }


    fun limpiarRegistro() {
        _registroExitoso.value = false
        _errorMensaje.value = ""
    }
}
