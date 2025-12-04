package com.example.levelup_gamer.repository

import android.util.Log
import  com.example.levelup_gamer.model.Usuario
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await


class AuthRepository {
    private val auth = FirebaseAuth.getInstance()
    private val db = FirebaseFirestore.getInstance()

    suspend fun login(correo: String, clave: String): Usuario? {
        return try {
            //Intentar autenticar con auth
            when {
                correo == "admin@levelup.cl" -> {
                    //Autenticación con Firebase Auth
                    val resultado = auth.signInWithEmailAndPassword(correo, clave).await()
                    Usuario(
                        correo = correo,
                        nombre = "Administrador",
                        rol = "admin"
                    )
                }
                else -> {
                    //Autenticación con la colección usuario de Firestore
                    loginWithFirestore(correo,clave)
                }
            }
        } catch (e: Exception){
            Log.e("AuthRepository", "Error al iniciar sesión", e)
            null
        }
    }


    suspend fun guardarTokenFcm(token: String) {
        val uid = auth.currentUser?.uid ?: return
        try {
            db.collection("usuario")
                .document(uid)
                .update("fcmToken", token)
                .await()
        } catch (_: Exception) {
            // puedes loguear el error si quieres
        }
    }



    private suspend fun loginWithFirestore(correo: String, clave: String): Usuario? {
        return try {
            val query = db.collection("usuario")
                .whereEqualTo("correo", correo)
                .whereEqualTo("clave", clave)
                .get()
                .await()

            if (!query.isEmpty) {
                val doc = query.documents[0]
                Usuario(
                    correo = doc.getString("correo") ?: "",
                    clave = doc.getString("clave") ?: "",
                    nombre = doc.getString("nombre") ?: "Cliente",
                    rol = doc.getString("rol") ?: "cliente"
                )
            } else null
        } catch (e: Exception) {
            null
        }
    }


    //Crear usuario en firebase
    suspend fun registroUsuario(correo: String, clave: String, nombre: String): Boolean {
        return try {

            val authResult = auth.createUserWithEmailAndPassword(correo, clave).await()
            val userId = authResult.user?.uid
            if (userId == null) {
                Log.e("AuthRepository", "UserId nulo después de createUser")
                return false
            }


            // Guardar datos en firestore
            val userMap = mapOf(
                "correo" to correo,
                "clave" to clave,
                "nombre" to nombre,
                "rol" to "cliente",
                "fechaRegistro" to System.currentTimeMillis()
            )

            db.collection("usuario")
                .document(userId)
                .set(userMap)
                .await()

            true
        } catch (e: Exception) {
            Log.e("AuthRepository", "Error al registrar usuario", e)
            false
        }
    }


}