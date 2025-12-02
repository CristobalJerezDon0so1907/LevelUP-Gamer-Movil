package com.example.levelup_gamer.repository

import com.example.levelup_gamer.model.Usuario
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await

class UsuarioRepository {

    private val db = FirebaseFirestore.getInstance()

    // Obtener todos los usuarios
    suspend fun obtenerTodosLosUsuarios(): List<Usuario> {
        return try {
            val snapshot = db.collection("usuario").get().await()

            snapshot.documents.map { doc ->
                Usuario(
                    correo = doc.getString("correo") ?: "",
                    clave = doc.getString("clave") ?: "",
                    nombre = doc.getString("nombre") ?: "",
                    rol = doc.getString("rol") ?: "",
                    fechaRegistro = doc.getString("fechaRegistro") ?: ""
                )
            }
        } catch (e: Exception) {
            emptyList()
        }
    }

    // Crear usuario usando el correo como ID
    suspend fun crearUsuario(usuario: Usuario): Boolean {
        return try {
            val docRef = db.collection("usuario").document(usuario.correo)

            // Revisar si ya existe
            if (docRef.get().await().exists()) {
                return false
            }

            docRef.set(usuario).await()
            true
        } catch (e: Exception) {
            false
        }
    }

    // Actualizar usuario por correo (correo es el ID del doc)
    suspend fun actualizarUsuario(correo: String, usuario: Usuario): Boolean {
        return try {
            db.collection("usuario")
                .document(correo)
                .set(usuario)
                .await()

            true
        } catch (e: Exception) {
            false
        }
    }

    // Eliminar usuario
    suspend fun eliminarUsuario(correo: String): Boolean {
        return try {
            db.collection("usuario")
                .document(correo)
                .delete()
                .await()

            true
        } catch (e: Exception) {
            false
        }
    }
}
