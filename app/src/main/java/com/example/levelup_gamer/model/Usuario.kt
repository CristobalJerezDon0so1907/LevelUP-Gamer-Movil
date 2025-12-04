package com.example.levelup_gamer.model

data class Usuario (
    val id: String = "",
    val correo: String = "",
    val clave: String = "",
    val nombre: String = "",
    val rol: String = "", //Variable local va a establecer si el usuario es admin o cliente
    val fechaRegistro: String = "",
    val fcmToken: String = ""
)