package com.example.levelup_gamer.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.levelup_gamer.model.Usuario
import com.example.levelup_gamer.repository.AuthRepository
import com.example.levelup_gamer.repository.UsuarioRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import kotlinx.coroutines.tasks.await


class LoginViewModel(
    private val usuarioRepository: UsuarioRepository = UsuarioRepository()
) : ViewModel() {

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
        val correo = _user.value?.correo
        val uid = _user.value?.id

        // 🔎 LOG CLAVE
        android.util.Log.d(
            "PerfilDebug",
            "Intentando guardar nombre | uid=$uid | correo=$correo | nuevoNombre=$nuevoNombre"
        )

        if (correo.isNullOrBlank()) {
            android.util.Log.e("PerfilDebug", "Correo NULL o vacío, no se puede guardar")
            onResult(false)
            return
        }

        viewModelScope.launch {
            try {
                val snap = FirebaseFirestore.getInstance()
                    .collection("usuario")
                    .whereEqualTo("correo", correo)
                    .limit(1)
                    .get()
                    .await()

                if (snap.isEmpty) {
                    android.util.Log.e(
                        "PerfilDebug",
                        "No se encontró documento usuario para correo=$correo"
                    )
                    onResult(false)
                    return@launch
                }

                val docRef = snap.documents.first().reference

                docRef.update("nombre", nuevoNombre).await()

                // ✅ Actualiza estado local
                _user.value = _user.value?.copy(nombre = nuevoNombre)

                android.util.Log.d(
                    "PerfilDebug",
                    "Nombre actualizado CORRECTAMENTE en Firestore"
                )

                onResult(true)

            } catch (e: Exception) {
                android.util.Log.e(
                    "PerfilDebug",
                    "EXCEPCIÓN al guardar nombre en Firestore",
                    e
                )
                onResult(false)
            }
        }
    }
    fun actualizarNombreAdmin(nuevoNombre: String, onResult: (Boolean) -> Unit) {
        val nuevo = nuevoNombre.trim()
        val correo = _user.value?.correo

        if (nuevo.isBlank()) {
            onResult(false)
            return
        }

        viewModelScope.launch {
            try {
                val db = FirebaseFirestore.getInstance()

                //Guardar/crear en admin/perfil
                db.collection("admin")
                    .document("perfil")
                    .set(mapOf("nombre" to nuevo), SetOptions.merge())
                    .await()

                //actualizar también en colección usuario por correo
                if (!correo.isNullOrBlank()) {
                    val snap = db.collection("usuario")
                        .whereEqualTo("correo", correo)
                        .limit(1)
                        .get()
                        .await()

                    if (!snap.isEmpty) {
                        snap.documents.first().reference
                            .update("nombre", nuevo)
                            .await()
                    }
                }

                //Actualizar estado local
                _user.value = _user.value?.copy(nombre = nuevo)

                onResult(true)
            } catch (e: Exception) {
                //Verificar errores en logcat
                android.util.Log.e("PerfilDebug", "Error actualizando nombre admin", e)
                onResult(false)
            }
        }
    }

}

