package com.example.levelup_gamer.model


import java.util.Date

data class Reseñas(
    val id: String = "",
    val userId: String = "",
    val userName: String = "",
    val userEmail: String = "",
    val rating: Float = 0f,
    val comment: String = "",
    val timestamp: Date = Date(),
    val productId: String = "", // Opcional para reseñas de productos específicos
    val isVerified: Boolean = false,
    val juego: String = "" // Nombre del juego reseñado
) {
    constructor() : this("", "", "", "", 0f, "", Date(), "", false, "")
}