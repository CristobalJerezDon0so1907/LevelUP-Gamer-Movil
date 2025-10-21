package com.example.levelup_gamer.viewmodel

import androidx.lifecycle.ViewModel
import com.example.levelup_gamer.model.Comentario
import com.google.firebase.Firebase
import com.google.firebase.firestore.firestore
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import org.w3c.dom.Comment

class ComentarioViewModel : ViewModel() {
    private val firestore = Firebase.firestore
    private val _comments = MutableStateFlow<List<Comment>>(emptyList())
    val comments: StateFlow<List<Comment>> = _comments.asStateFlow()

    fun fetchComments(productId: String) {
        firestore.collection("comments")
            .whereEqualTo("productId", productId)
            .orderBy("timestamp", com.google.firebase.firestore.Query.Direction.DESCENDING)
            .addSnapshotListener { snapshot, e ->
                if (e != null) {
                    // Manejar error
                    return@addSnapshotListener
                }
                if (snapshot != null) {
                    val commentsList = snapshot.toObjects(Comment::class.java)
                    _comments.value = commentsList
                }
            }
    }

    fun addComment(productId: String, userId: String, author: String, text: String) {
        val newComment = Comentario(
            productId = productId,
            userId = userId,
            author = author,
            text = text
        )
        firestore.collection("comments").add(newComment)
            .addOnSuccessListener { documentReference ->
                // Éxito
            }
            .addOnFailureListener { e ->
                // Manejar error
            }
    }
}
