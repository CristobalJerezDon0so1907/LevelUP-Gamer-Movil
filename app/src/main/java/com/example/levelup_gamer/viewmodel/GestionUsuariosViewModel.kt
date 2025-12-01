package com.example.levelup_gamer.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.levelup_gamer.model.Usuario
import com.example.levelup_gamer.repository.UsuarioRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class GestionUsuariosViewModel : ViewModel() {
    private val usuarioRepository = UsuarioRepository()

    private val _usuarios = MutableStateFlow<List<Usuario>>(emptyList())
    val usuarios: StateFlow<List<Usuario>> = _usuarios.asStateFlow()

    private val _cargando = MutableStateFlow(false)
    val cargando: StateFlow<Boolean> = _cargando.asStateFlow()

    private val _mensaje = MutableStateFlow<String?>(null)
    val mensaje: StateFlow<String?> = _mensaje.asStateFlow()

    fun cargarUsuarios() {
        viewModelScope.launch {
            _cargando.value = true
            _mensaje.value = null

            try {
                val usuariosList = usuarioRepository.obtenerTodosLosUsuarios()
                _usuarios.value = usuariosList
            } catch (e: Exception) {
                _mensaje.value = "Error al cargar usuarios: ${e.message}"
            }

            _cargando.value = false
        }
    }

    fun crearUsuario(usuario: Usuario) {
        viewModelScope.launch {
            _cargando.value = true
            _mensaje.value = null

            try {
                val resultado = usuarioRepository.crearUsuario(usuario)
                if (resultado) {
                    _mensaje.value = "Usuario creado exitosamente"
                    cargarUsuarios() // Recargar la lista
                } else {
                    _mensaje.value = "Error al crear usuario: El correo ya existe"
                }
            } catch (e: Exception) {
                _mensaje.value = "Error al crear usuario: ${e.message}"
            }

            _cargando.value = false
        }
    }

    fun actualizarUsuario(correo: String, usuario: Usuario) {
        viewModelScope.launch {
            _cargando.value = true
            _mensaje.value = null

            try {
                val resultado = usuarioRepository.actualizarUsuario(correo, usuario)
                if (resultado) {
                    _mensaje.value = "Usuario actualizado exitosamente"
                    cargarUsuarios() // Recargar la lista
                } else {
                    _mensaje.value = "Error al actualizar usuario"
                }
            } catch (e: Exception) {
                _mensaje.value = "Error al actualizar usuario: ${e.message}"
            }

            _cargando.value = false
        }
    }

    fun eliminarUsuario(correo: String) {
        viewModelScope.launch {
            _cargando.value = true
            _mensaje.value = null

            try {
                val resultado = usuarioRepository.eliminarUsuario(correo)
                if (resultado) {
                    _mensaje.value = "Usuario eliminado exitosamente"
                    cargarUsuarios() // Recargar la lista
                } else {
                    _mensaje.value = "Error al eliminar usuario"
                }
            } catch (e: Exception) {
                _mensaje.value = "Error al eliminar usuario: ${e.message}"
            }

            _cargando.value = false
        }
    }

    fun limpiarMensaje() {
        _mensaje.value = null
    }
}