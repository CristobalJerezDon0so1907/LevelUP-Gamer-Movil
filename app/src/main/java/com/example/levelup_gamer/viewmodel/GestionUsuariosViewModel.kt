package com.example.levelup_gamer.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.levelup_gamer.model.Usuario
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class GestionUsuariosViewModel : ViewModel() {

    private val db = FirebaseFirestore.getInstance()

    private val _usuarios = MutableStateFlow<List<Usuario>>(emptyList())
    val usuarios: StateFlow<List<Usuario>> = _usuarios

    private val _cargando = MutableStateFlow(false)
    val cargando: StateFlow<Boolean> = _cargando

    private val _mensaje = MutableStateFlow<String?>(null)
    val mensaje: StateFlow<String?> = _mensaje

    private val _usuarioSeleccionado = MutableStateFlow<Usuario?>(null)
    val usuarioSeleccionado: StateFlow<Usuario?> = _usuarioSeleccionado

    fun limpiarMensaje() {
        _mensaje.value = null
    }

    fun seleccionarUsuario(usuario: Usuario?) {
        _usuarioSeleccionado.value = usuario
    }

    fun cargarUsuarios() {
        _cargando.value = true

        viewModelScope.launch {
            db.collection("usuario")
                .get()
                .addOnSuccessListener { query ->
                    val lista = query.documents.map { doc ->
                        val nombre = doc.getString("nombre") ?: ""
                        val correo = doc.getString("correo") ?: ""
                        val rol = doc.getString("rol") ?: ""

                        Usuario(
                            id = doc.id,
                            nombre = nombre,
                            correo = correo,
                            rol = rol
                        )
                    }

                    _usuarios.value = lista
                    _cargando.value = false
                }
                .addOnFailureListener {
                    _cargando.value = false
                    _mensaje.value = "Error al cargar usuarios"
                }
        }
    }

    fun eliminarUsuario(idUsuario: String) {
        if (idUsuario.isBlank()) return

        viewModelScope.launch {
            db.collection("usuario")
                .document(idUsuario)
                .delete()
                .addOnSuccessListener {
                    _mensaje.value = "Usuario eliminado"
                    cargarUsuarios()
                }
                .addOnFailureListener {
                    _mensaje.value = "Error al eliminar usuario"
                }
        }
    }

    fun guardarUsuario(usuario: Usuario) {
        viewModelScope.launch {
            if (usuario.id.isBlank()) {
                // Crear nuevo
                db.collection("usuario")
                    .add(
                        mapOf(
                            "nombre" to usuario.nombre,
                            "correo" to usuario.correo,
                            "rol" to usuario.rol
                        )
                    )
                    .addOnSuccessListener {
                        _mensaje.value = "Usuario creado"
                        cargarUsuarios()
                    }
                    .addOnFailureListener {
                        _mensaje.value = "Error al crear usuario"
                    }
            } else {
                // Actualizar existente
                db.collection("usuario")
                    .document(usuario.id)
                    .update(
                        mapOf(
                            "nombre" to usuario.nombre,
                            "correo" to usuario.correo,
                            "rol" to usuario.rol
                        )
                    )
                    .addOnSuccessListener {
                        _mensaje.value = "Usuario actualizado"
                        cargarUsuarios()
                    }
                    .addOnFailureListener {
                        _mensaje.value = "Error al actualizar usuario"
                    }
            }
        }
    }
}
