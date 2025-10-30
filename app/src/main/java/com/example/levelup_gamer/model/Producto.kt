package com.example.levelup_gamer.model

import java.net.URL

data class Producto(
    val id: String = "",
    val nombre: String = "",
    val descripcion: String = "",
    val precio: Double = 0.0,
    val stock: Int = 0,
    val imagenUrl: String = ""
)