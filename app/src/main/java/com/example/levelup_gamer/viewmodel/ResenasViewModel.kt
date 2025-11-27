package com.example.levelup_gamer.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.levelup_gamer.model.Resena
import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import com.google.firebase.firestore.Query


class ResenasViewModel : ViewModel() {

    private val db = FirebaseFirestore.getInstance()

    private val _resenas = MutableStateFlow<List<Resena>>(emptyList())
    val resenas: StateFlow<List<Resena>> = _resenas

    // Cargar reseñas de un producto (o todas si productId es null)
    fun cargarResenas(productId: String? = null) {
        val baseCollection = db.collection("resenas")

        val query: Query = if (productId.isNullOrBlank()) {
            baseCollection
        } else {
            baseCollection.whereEqualTo("productId", productId)
        }

        query
            .orderBy("timestamp")
            .addSnapshotListener { snapshot, error ->
                if (error != null || snapshot == null) return@addSnapshotListener

                val lista = snapshot.documents.map { doc ->
                    Resena(
                        id = doc.id,
                        producto = doc.getString("producto")
                            ?: doc.getString("juego") ?: "",
                        productId = doc.getString("productId") ?: "",
                        comment = doc.getString("comment") ?: "",
                        rating = doc.getLong("rating")?.toInt() ?: 0,
                        timestamp = doc.getTimestamp("timestamp"),
                        userEmail = doc.getString("userEmail") ?: "",
                        userId = doc.getString("userId") ?: "",
                        userName = doc.getString("userName") ?: "",
                        verified = doc.getBoolean("verified") ?: false
                    )
                }

                _resenas.value = lista
            }
    }


    fun agregarResena(
        productoNombre: String,
        productId: String,
        rating: Int,
        comment: String
    ) {
        val user = FirebaseAuth.getInstance().currentUser

        val data = hashMapOf(
            "producto" to productoNombre,
            "productId" to productId,
            "rating" to rating,
            "comment" to comment,
            "timestamp" to Timestamp.now(),
            "userEmail" to (user?.email ?: ""),
            "userId" to (user?.uid ?: ""),
            "userName" to (user?.displayName ?: ""),
            "verified" to false
        )

        db.collection("resenas").add(data)
    }
}
