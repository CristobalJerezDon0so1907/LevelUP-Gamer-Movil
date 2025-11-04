package com.example.levelup_gamer.repository

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await

data class AuthUser(
    val uid: String,
    val email: String?,
    val nombre: String?,
    val rol: String?
)

sealed class ResultWrapper<out T> {
    data class Success<out T>(val data: T) : ResultWrapper<T>()
    data class Error(val message: String) : ResultWrapper<Nothing>()
}

class AuthRepository {

    private val firebaseAuth: FirebaseAuth = FirebaseAuth.getInstance()
    private val firestore: FirebaseFirestore = FirebaseFirestore.getInstance()
    private val userCollection = firestore.collection("usuario") // ¡COLECCIÓN UNIFICADA Y CORREGIDA!

    suspend fun login(email: String, password: String): ResultWrapper<AuthUser> {
        return try {
            // 1. SIEMPRE usamos Firebase Auth para verificar la contraseña de forma segura.
            val authResult = firebaseAuth.signInWithEmailAndPassword(email, password).await()
            val firebaseUser = authResult.user

            if (firebaseUser != null) {
                val authUser: AuthUser

                if (email.lowercase() == "admin@levelup.cl") {
                    // Para el admin, el nombre y rol son fijos. No se consulta la DB para esto.
                    authUser = AuthUser(
                        uid = firebaseUser.uid,
                        email = firebaseUser.email,
                        nombre = "Admin", // Nombre fijo como pediste.
                        rol = "admin"
                    )
                } else {
                    // Para el cliente, SÍ consultamos Firestore en la colección correcta.
                    val userDoc = userCollection.document(firebaseUser.uid).get().await()
                    val finalUserName = if (userDoc.exists()) {
                        userDoc.getString("nombre")?.takeIf { it.isNotBlank() } ?: firebaseUser.email?.split('@')?.get(0) ?: "Cliente"
                    } else {
                        // Fallback por si el documento no se creó (no debería pasar con el repo de registro arreglado)
                        firebaseUser.email?.split('@')?.get(0) ?: "Cliente"
                    }
                    
                    authUser = AuthUser(
                        uid = firebaseUser.uid,
                        email = firebaseUser.email,
                        nombre = finalUserName,
                        rol = "cliente"
                    )
                }
                ResultWrapper.Success(authUser)
            } else {
                ResultWrapper.Error("Error desconocido durante el inicio de sesión.")
            }
        } catch (e: Exception) {
            ResultWrapper.Error(e.message ?: "Error al iniciar sesión. Verifique sus credenciales.")
        }
    }

    fun logout() {
        firebaseAuth.signOut()
    }
}
