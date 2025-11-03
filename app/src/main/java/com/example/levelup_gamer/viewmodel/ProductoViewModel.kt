package com.example.levelup_gamer.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.levelup_gamer.model.Producto
import com.example.levelup_gamer.repository.ProductoRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class ProductoViewModel(
    private val repository: ProductoRepository = ProductoRepository()
) : ViewModel() {

    private val _productos = MutableStateFlow<List<Producto>>(emptyList())
    val productos: StateFlow<List<Producto>> = _productos

    // Estado para el producto cargado por id (detalle)
    private val _productoSeleccionado = MutableStateFlow<Producto?>(null)
    val productoSeleccionado: StateFlow<Producto?> = _productoSeleccionado

    init {
        cargarProductos()
    }

    private fun cargarProductos() {
        viewModelScope.launch {
            try {
                val resultado = repository.obtenerProductos()
                _productos.value = resultado.productos
            } catch (e: Exception) {
                _productos.value = emptyList()
            }
        }
    }

    /** Carga un producto por su id y lo deja en productoSeleccionado (null si no existe). */
    fun cargarProductoPorId(productoId: String) {
        viewModelScope.launch {
            try {
                val producto = repository.obtenerProductoPorId(productoId)
                _productoSeleccionado.value = producto
            } catch (e: Exception) {
                _productoSeleccionado.value = null
            }
        }
    }

    /** Limpia el producto seleccionado (opcional). */
    fun limpiarProductoSeleccionado() {
        _productoSeleccionado.value = null
    }
}
