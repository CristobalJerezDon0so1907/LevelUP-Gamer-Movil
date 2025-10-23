package com.example.levelup_gamer.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.levelup_gamer.model.Comentario
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.util.Date

class ComentarioViewModel : ViewModel() {
    private val firestore = FirebaseFirestore.getInstance()

    private val _comments = MutableStateFlow<List<Comentario>>(emptyList())
    val comments: StateFlow<List<Comentario>> = _comments.asStateFlow()

    private val _cargando = MutableStateFlow(false)
    val cargando: StateFlow<Boolean> = _cargando

    private val _errorMensaje = MutableStateFlow("")
    val errorMensaje: StateFlow<String> = _errorMensaje

    private val _comentarioAgregado = MutableStateFlow(false)
    val comentarioAgregado: StateFlow<Boolean> = _comentarioAgregado

    fun fetchComments(productId: String) {
        _cargando.value = true
        _errorMensaje.value = ""

        firestore.collection("comments")
            .whereEqualTo("productId", productId)
            .orderBy("timestamp", Query.Direction.DESCENDING)
            .addSnapshotListener { snapshot, e ->
                _cargando.value = false

                if (e != null) {
                    _errorMensaje.value = when {
                        e.message?.contains("network", ignoreCase = true) == true ->
                            "Error de conexión. Verifica tu internet"
                        e.message?.contains("permission", ignoreCase = true) == true ->
                            "No tienes permisos para ver los comentarios"
                        else -> "Error al cargar comentarios: ${e.message}"
                    }
                    return@addSnapshotListener
                }

                if (snapshot != null && !snapshot.isEmpty) {
                    val commentsList = snapshot.toObjects(Comentario::class.java)
                    _comments.value = commentsList
                    _errorMensaje.value = ""
                } else {
                    _comments.value = emptyList()
                    _errorMensaje.value = "No hay comentarios para este producto"
                }
            }
    }

    fun addComment(productId: String, userId: String, author: String, text: String) {
        if (text.isBlank()) {
            _errorMensaje.value = "El comentario no puede estar vacío"
            return
        }

        if (text.length > 500) {
            _errorMensaje.value = "El comentario es demasiado largo (máximo 500 caracteres)"
            return
        }

        _cargando.value = true
        _errorMensaje.value = ""

        val newComment = Comentario(
            productId = productId,
            userId = userId,
            author = author,
            text = text.trim(),
            timestamp = Date() // 👈 AHORA FUNCIONA
        )

        firestore.collection("comments")
            .add(newComment)
            .addOnSuccessListener {
                _cargando.value = false
                _comentarioAgregado.value = true
                _errorMensaje.value = ""
                viewModelScope.launch {
                    kotlinx.coroutines.delay(2000)
                    _comentarioAgregado.value = false
                }
            }
            .addOnFailureListener { e ->
                _cargando.value = false
                _errorMensaje.value = when {
                    e.message?.contains("network", ignoreCase = true) == true ->
                        "Error de conexión. No se pudo publicar el comentario"
                    e.message?.contains("permission", ignoreCase = true) == true ->
                        "No tienes permisos para publicar comentarios"
                    else -> "Error al publicar comentario: ${e.message}"
                }
            }
    }

    fun limpiarErrores() {
        _errorMensaje.value = ""
        _comentarioAgregado.value = false
    }

    fun validarComentario(text: String): String {
        return when {
            text.isBlank() -> "El comentario no puede estar vacío"
            text.length > 500 -> "Máximo 500 caracteres permitidos"
            else -> ""
        }
    }
}