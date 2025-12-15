package com.example.levelup_gamer.repository

import android.util.Log
import com.example.levelup_gamer.model.Usuario
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await

class AuthRepository {
    private val auth = FirebaseAuth.getInstance()
    private val db = FirebaseFirestore.getInstance()
    private val usuarioRepository = UsuarioRepository() // Instancia para usar sus métodos

    suspend fun login(correo: String, clave: String): Usuario? {
        return try {
            //Intentar la autenticación con Firebase Auth (para todos: admin y clientes)
            val resultado = auth.signInWithEmailAndPassword(correo, clave).await()
            val uid = resultado.user?.uid ?: return null

            //Caso especial: Usuario Administrador (sin documento en Firestore)
            if (correo == "admin@levelup.cl") {
                //Creamos un objeto Usuario 'admin' temporal para la sesión
                return Usuario(
                    id = uid,
                    correo = correo,
                    nombre = "Administrador",
                    rol = "admin",
                    fechaRegistro = System.currentTimeMillis()
                )
            }

            //Usuarios normales: Obtener el resto de los datos del usuario desde Firestore
            return usuarioRepository.obtenerUsuarioPorUid(uid)

        } catch (e: Exception) {
            Log.e("AuthRepository", "Error al iniciar sesión o clave/correo incorrectos", e)
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
        }
    }


    //Crear usuario en firebase (usando el UID como ID del documento)
    suspend fun registroUsuario(correo: String, clave: String, nombre: String): Boolean {
        return try {
            //Crear el usuario en Firebase Auth
            val authResult = auth.createUserWithEmailAndPassword(correo, clave).await()
            val userId = authResult.user?.uid
            if (userId == null) {
                Log.e("AuthRepository", "UserId nulo después de createUser")
                return false
            }

            //Guardar datos en Firestore usando el UID como ID del documento
            val nuevoUsuario = Usuario(
                id = userId, // Usar UID como ID
                correo = correo.trim().lowercase(),
                nombre = nombre,
                clave = "",
                rol = "cliente",
                fechaRegistro = System.currentTimeMillis()
            )

            db.collection("usuario")
                .document(userId) // Usar el UID como ID del documento
                .set(nuevoUsuario)
                .await()

            true
        } catch (e: Exception) {
            Log.e("AuthRepository", "Error al registrar usuario", e)
            false
        }
    }


}