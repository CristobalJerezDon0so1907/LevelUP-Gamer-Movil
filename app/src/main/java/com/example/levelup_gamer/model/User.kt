package com.example.levelup_gamer.model

data class User(
    val id: String,
    val username: String,
    val email: String,
    val role: String = "cliente"
)