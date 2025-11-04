package com.example.levelup_gamer.repository

import com.example.levelup_gamer.model.User
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.map // 👈 ¡Asegúrate de que este import esté presente!

interface UserRepository {
    suspend fun createUser(username: String, email: String, password: String): Result<User>
    fun getUserById(id: String): Flow<User?>
    suspend fun findUserByEmail(email: String): User?
}

// Implementación de juguete mejorada para demostración
class DummyUserRepository : UserRepository {
    // Almacén de usuarios
    private val users = mutableListOf<User>()

    // Un Flow que emitirá la lista completa cada vez que cambie.
    // Lo hacemos privado para controlar las actualizaciones desde dentro del repositorio.
    private val userListFlow = MutableStateFlow<List<User>>(emptyList())

    private var nextId = 1

    init {
        // Para pruebas, podemos añadir usuarios de ejemplo desde el inicio
        val initialUsers = listOf(
            User(id = nextId++.toString(), username = "Admin", email = "admin@levelup.cl", role = "admin"),
            User(id = nextId++.toString(), username = "ClienteUP", email = "cliente@levelup.cl", role = "cliente"),
            User(id = nextId++.toString(), username = "Cris", email = "cris@levelup.cl", role = "cliente")
        )
        users.addAll(initialUsers)
        // Actualizamos el Flow con la lista inicial
        userListFlow.value = users.toList() // Usamos toList() para crear una copia inmutable
    }

    override suspend fun createUser(username: String, email: String, password: String): Result<User> {
        // En una app real, aquí hashearías la contraseña
        if (users.any { it.email == email }) {
            return Result.failure(Exception("El correo electrónico ya está en uso."))
        }
        // Creamos un nuevo usuario, por defecto con rol "cliente"
        val newUser = User(id = nextId++.toString(), username = username, email = email)
        users.add(newUser)
        // Notificamos al Flow que la lista ha cambiado, emitiendo una nueva copia
        userListFlow.value = users.toList()
        return Result.success(newUser)
    }

    override fun getUserById(id: String): Flow<User?> {
        // ¡Esta es la versión mejorada!
        // Usamos el operador 'map' sobre nuestro Flow de la lista de usuarios.
        // Cada vez que 'userListFlow' emita una nueva lista, este código se ejecutará.
        return userListFlow.map { userList ->
            // Busca el usuario en la lista más reciente.
            userList.find { it.id == id }
        }
    }

    override suspend fun findUserByEmail(email: String): User? {
        return users.find { it.email == email }
    }
}