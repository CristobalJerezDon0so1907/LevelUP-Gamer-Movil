package com.example.levelup_gamer.model

data class Producto(
    val id: String = "",
    val nombre: String = "",
    val precio: Double = 0.0,
    val descripcion: String = "",
    val imagenUrl: String = "",
    val stock: Int = 0
)