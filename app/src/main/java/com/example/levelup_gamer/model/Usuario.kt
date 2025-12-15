package com.example.levelup_gamer.model

data class Usuario(
    val id: String = "",
    val correo: String = "",
    val nombre: String = "",
    val clave: String = "",
    val rol: String = "cliente",
    val fechaRegistro: Long = 0L,
    val fotoUrl: String = ""
)
