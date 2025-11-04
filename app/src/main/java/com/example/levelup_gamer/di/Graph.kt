package com.example.levelup_gamer.di

import com.example.levelup_gamer.repository.DummyUserRepository
import com.example.levelup_gamer.repository.UserRepository

/**
 * Un contenedor de dependencias simple (Service Locator) para toda la aplicación.
 * Esto asegura que las dependencias, como el repositorio, sean singletons.
 */
object Graph {
    // Repositorio de usuarios. Usamos `lazy` para que se cree solo una vez, cuando se necesite.
    val userRepository: UserRepository by lazy {
        DummyUserRepository()
    }
}