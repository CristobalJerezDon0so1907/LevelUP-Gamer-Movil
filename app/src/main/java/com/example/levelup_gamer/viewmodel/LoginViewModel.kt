package com.example.levelup_gamer.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.levelup_gamer.model.Usuario
import com.example.levelup_gamer.repository.AuthRepository
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.messaging.FirebaseMessaging
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class LoginViewModel : ViewModel() {

    private val repositorio = AuthRepository()

    private val _user = MutableStateFlow<Usuario?>(null)
    val user: StateFlow<Usuario?> = _user

    private val _carga = MutableStateFlow(false)
    val carga: StateFlow<Boolean> = _carga

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error

    fun login(correo: String, clave: String) {
        _carga.value = true
        _error.value = null

        viewModelScope.launch {
            try {
                val usuario = repositorio.login(correo, clave)

                if (usuario != null) {
                    _user.value = usuario
                    _error.value = null
                } else {
                    _user.value = null
                    _error.value = "Correo o contraseña incorrectos"
                }
            } catch (e: Exception) {
                _user.value = null
                _error.value = "Error al iniciar sesión"
            } finally {
                _carga.value = false
            }
        }
    }

    fun logout() {
        _user.value = null
    }

    fun actualizarNombreUsuario(nuevoNombre: String, onResult: (Boolean) -> Unit) {
        val usuarioActual = _user.value
        if (usuarioActual == null) {
            onResult(false)
            return
        }

        val correo = usuarioActual.correo

        FirebaseFirestore.getInstance()
            .collection("usuario")
            .whereEqualTo("correo", correo)
            .get()
            .addOnSuccessListener { query ->
                if (!query.isEmpty) {
                    val docRef = query.documents[0].reference

                    docRef.update("nombre", nuevoNombre)
                        .addOnSuccessListener {
                            // Actualizar en memoria también
                            _user.value = usuarioActual.copy(nombre = nuevoNombre)
                            onResult(true)
                        }
                        .addOnFailureListener {
                            onResult(false)
                        }

                } else {
                    onResult(false)
                }
            }
            .addOnFailureListener {
                onResult(false)
            }
    }
}
