package com.example.levelup_gamer.model

import java.util.Date

data class Resena(
    val id: String = "",
    val userId: String = "",
    val userName: String = "",
    val userEmail: String = "",
    val rating: Float = 0f,
    val comment: String = "",
    val timestamp: Date = Date(),
    val productId: String = "",
    val isVerified: Boolean = false,
    val juego: String = ""
)
