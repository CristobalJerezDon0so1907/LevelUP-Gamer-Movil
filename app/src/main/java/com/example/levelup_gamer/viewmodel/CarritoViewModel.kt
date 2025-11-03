package com.example.levelup_gamer.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.levelup_gamer.model.ItemCarrito
import com.example.levelup_gamer.model.Producto
import com.example.levelup_gamer.repository.ProductoRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class CarritoViewModel(private val repository: ProductoRepository) : ViewModel() {

    private val _productos = MutableStateFlow<List<Producto>>(emptyList())
    val productos: StateFlow<List<Producto>> = _productos.asStateFlow()

    private val _carrito = MutableStateFlow<List<ItemCarrito>>(emptyList())
    val carrito: StateFlow<List<ItemCarrito>> = _carrito.asStateFlow()

    private val _cargando = MutableStateFlow(false)
    val cargando: StateFlow<Boolean> = _cargando.asStateFlow()

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage.asStateFlow()

    init {
        cargarProductos()
    }

    private fun cargarProductos() {
        viewModelScope.launch {
            _cargando.value = true
            try {
                val resultado = repository.obtenerProductos()
                _productos.value = resultado.productos
            } catch (e: Exception) {
                _errorMessage.value = "Error al cargar productos: ${e.message}"
            } finally {
                _cargando.value = false
            }
        }
    }

    fun agregarAlCarrito(producto: Producto) {
        val carritoActual = _carrito.value.toMutableList()
        val itemExistente = carritoActual.find { it.producto.id == producto.id }

        if (itemExistente != null) {
            itemExistente.cantidad++
        } else {
            carritoActual.add(ItemCarrito(producto = producto, cantidad = 1))
        }
        _carrito.value = carritoActual
    }

    fun removerDelCarrito(item: ItemCarrito) {
        val carritoActual = _carrito.value.toMutableList()
        val itemExistente = carritoActual.find { it.producto.id == item.producto.id }

        if (itemExistente != null) {
            if (itemExistente.cantidad > 1) {
                itemExistente.cantidad--
            } else {
                carritoActual.remove(itemExistente)
            }
        }
        _carrito.value = carritoActual
    }

    fun eliminarProductoDelCarrito(producto: Producto) {
        val carritoActual = _carrito.value.toMutableList()
        carritoActual.removeAll { it.producto.id == producto.id }
        _carrito.value = carritoActual
    }

    fun vaciarCarrito() {
        _carrito.value = emptyList()
    }
    
    fun confirmarCompra() {
        viewModelScope.launch {
            // TODO: Añadir lógica para registrar la compra en la base de datos
            vaciarCarrito()
        }
    }

    fun obtenerTotal(): Double {
        return _carrito.value.sumOf { it.producto.precio * it.cantidad }
    }

    fun clearError() {
        _errorMessage.value = null
    }
}

class CarritoViewModelFactory(private val repository: ProductoRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(CarritoViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return CarritoViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}