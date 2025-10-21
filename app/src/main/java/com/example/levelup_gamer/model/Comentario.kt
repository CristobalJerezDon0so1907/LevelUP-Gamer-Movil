package com.example.levelup_gamer.model

data class Comentario (
    val id: String = "",
    val productId: String = "",
    val userId: String = "",
    val author: String = "",
    val text: String = "",
    val timestamp: Long = System.currentTimeMillis()
)

