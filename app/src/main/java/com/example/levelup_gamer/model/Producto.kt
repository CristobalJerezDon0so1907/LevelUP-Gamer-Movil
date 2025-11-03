package com.example.levelup_gamer.model

data class Producto(
    val id: String = "",
    val nombre: String = "",
    val descripcion: String = "",
    val precio: Double = 0.0,
    val imagenUrl: String = "",
    val stock: Int = 0
)
