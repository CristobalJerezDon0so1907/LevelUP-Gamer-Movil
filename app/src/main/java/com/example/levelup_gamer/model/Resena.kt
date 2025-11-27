package com.example.levelup_gamer.model

import com.google.firebase.Timestamp

data class Resena (
    val id: String = "",
    val producto: String = "",
    val productId: String = "",
    val comment: String = "",
    val rating: Int = 0,
    val timestamp: Timestamp? = null,
    val userEmail: String = "",
    val userId: String = "",
    val userName: String,
    val verified: Boolean = false
)