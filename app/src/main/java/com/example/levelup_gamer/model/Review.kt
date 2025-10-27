package com.example.levelup_gamer.model


import java.util.Date

data class Review(
    val id: String = "",
    val userId: String = "",
    val userName: String = "",
    val userEmail: String = "",
    val rating: Float = 0f,
    val comment: String = "",
    val timestamp: Date = Date(),
    val productId: String = "", // Opcional, si es para productos
    val isVerified: Boolean = false
) {
    // Constructor sin parámetros para Firebase
    constructor() : this("", "", "", "", 0f, "", Date(), "", false)
}