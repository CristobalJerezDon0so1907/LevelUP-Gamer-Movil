package com.example.levelup_gamer.viewmodel

import android.util.Patterns
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.levelup_gamer.repository.UsuarioRepository
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

    fun registroUsuario(correo: String, clave: String, confirmarClave: String, nombre: String) {

        // ===== VALIDACIÓN CAMPOS OBLIGATORIOS =====
        if (correo.isBlank() || clave.isBlank() || confirmarClave.isBlank() || nombre.isBlank()) {
            _errorMensaje.value = "Todos los campos son obligatorios"
            return
        }

        // ===== VALIDAR NOMBRE SIN NÚMEROS =====
        if (nombre.any { it.isDigit() }) {
            _errorMensaje.value = "El nombre no puede contener números"
            return
        }

        // ===== VALIDACIÓN DE CORREO =====
        if (!Patterns.EMAIL_ADDRESS.matcher(correo).matches()) {
            _errorMensaje.value = "El correo no es válido"
            return
        }

        val dominiosPermitidos = listOf("@duoc.cl", "@gmail.com", "@hotmail.com", "@outlook.com")

        if (!dominiosPermitidos.any { correo.endsWith(it) }) {
            _errorMensaje.value = "Solo se permiten correos Duoc, Gmail, Hotmail u Outlook"
            return
        }

        // ===== VALIDACIÓN DE CONTRASEÑA FUERTE =====
        val passwordRegex =
            Regex("^(?=.*[A-Z])(?=.*\\d)(?=.*[@#\$%^&+=!¿?*._-]).{8,}\$")

        if (!passwordRegex.matches(clave)) {
            _errorMensaje.value =
                "La contraseña debe tener:\n• Mínimo 8 caracteres\n• 1 mayúscula\n• 1 número\n• 1 símbolo"
            return
        }

        // ===== VALIDAR QUE COINCIDAN =====
        if (clave != confirmarClave) {
            _errorMensaje.value = "Las contraseñas no coinciden"
            return
        }

        // ===== CONTINUAR SI TODO ES VÁLIDO =====
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
