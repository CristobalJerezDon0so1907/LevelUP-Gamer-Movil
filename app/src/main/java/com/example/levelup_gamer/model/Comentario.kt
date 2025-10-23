package com.example.levelup_gamer.model

import java.util.Date

data class Comentario(
    val id: String = "",
    val productId: String = "",
    val userId: String = "",
    val author: String = "",
    val text: String = "",
    val timestamp: Date = Date() // 👈 AGREGAR ESTE CAMPO
)