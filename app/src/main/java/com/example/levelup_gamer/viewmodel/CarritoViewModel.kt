package com.example.levelup_gamer.viewmodel

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import com.example.levelup_gamer.model.Producto

class CarritoViewModel : ViewModel() {

    // 🔹 Mapa del carrito: Producto → Cantidad
    private val _carrito = MutableStateFlow<Map<Producto, Int>>(emptyMap())
    val carrito: StateFlow<Map<Producto, Int>> = _carrito.asStateFlow()

    // 🔹 Total del carrito
    private val _total = MutableStateFlow(0.0)
    val total: StateFlow<Double> = _total.asStateFlow()

    // 🔹 Agregar producto al carrito
    fun agregarAlCarrito(producto: Producto) {
        _carrito.update { carritoActual ->
            val cantidadActual = carritoActual[producto] ?: 0
            val nuevoCarrito = carritoActual.toMutableMap()
            nuevoCarrito[producto] = cantidadActual + 1
            calcularTotal(nuevoCarrito)
            nuevoCarrito
        }
    }

    // 🔹 Eliminar producto del carrito
    fun eliminarDelCarrito(producto: Producto) {
        _carrito.update { carritoActual ->
            val cantidadActual = carritoActual[producto] ?: return
            val nuevoCarrito = carritoActual.toMutableMap()
            if (cantidadActual > 1) {
                nuevoCarrito[producto] = cantidadActual - 1
            } else {
                nuevoCarrito.remove(producto)
            }
            calcularTotal(nuevoCarrito)
            nuevoCarrito
        }
    }

    // 🔹 Calcular total
    private fun calcularTotal(carrito: Map<Producto, Int>) {
        _total.value = carrito.entries.sumOf { (producto, cantidad) ->
            producto.precio * cantidad
        }
    }

    // 🔹 Vaciar carrito
    fun vaciarCarrito() {
        _carrito.value = emptyMap()
        _total.value = 0.0
    }
}
