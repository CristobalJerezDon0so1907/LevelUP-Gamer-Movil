package com.example.levelup_gamer.repository

import android.util.Log
import com.example.levelup_gamer.model.Usuario
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await

class UsuarioRepository {

    private val db = FirebaseFirestore.getInstance()

    suspend fun obtenerUsuarioPorUid(uid: String): Usuario? {
        return try {
            val doc = db.collection("usuario").document(uid).get().await()
            doc.toObject(Usuario::class.java)?.copy(id = doc.id)
        } catch (e: Exception) {
            null
        }
    }

    suspend fun actualizarNombre(uid: String, nuevoNombre: String): Boolean {
        return try {
            db.collection("usuario")
                .document(uid)
                .update("nombre", nuevoNombre)
                .await()
            true
        } catch (e: Exception) {
            Log.e("UsuarioRepository", "Error actualizando nombre", e)
            false
        }
    }

    suspend fun actualizarNombreRobusto(uid: String, correo: String?, nuevoNombre: String): Boolean {
        // 1) Intentar por UID (docId = uid)
        val okUid = try {
            db.collection("usuario")
                .document(uid)
                .update("nombre", nuevoNombre)
                .await()
            true
        } catch (_: Exception) {
            false
        }

        if (okUid) return true

        // 2) Fallback por correo (docId aleatorio)
        if (correo.isNullOrBlank()) return false

        return try {
            val snap = db.collection("usuario")
                .whereEqualTo("correo", correo)
                .limit(1)
                .get()
                .await()

            val docRef = snap.documents.firstOrNull()?.reference ?: return false

            docRef.update("nombre", nuevoNombre).await()
            true
        } catch (e: Exception) {
            Log.e("UsuarioRepository", "Error actualizando nombre (fallback correo)", e)
            false
        }
    }

}
