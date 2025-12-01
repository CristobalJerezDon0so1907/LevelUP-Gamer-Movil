package com.example.levelup_gamer.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.levelup_gamer.model.Producto
import com.example.levelup_gamer.repository.ProductoRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class GestionProductosViewModel : ViewModel() {
    private val productoRepository = ProductoRepository()

    private val _productos = MutableStateFlow<List<Producto>>(emptyList())
    val productos: StateFlow<List<Producto>> = _productos.asStateFlow()

    private val _cargando = MutableStateFlow(false)
    val cargando: StateFlow<Boolean> = _cargando.asStateFlow()

    private val _mensaje = MutableStateFlow<String?>(null)
    val mensaje: StateFlow<String?> = _mensaje.asStateFlow()

    fun cargarProductos() {
        viewModelScope.launch {
            _cargando.value = true
            _mensaje.value = null

            try {
                val productosList = productoRepository.obtenerProductos()
                _productos.value = productosList
            } catch (e: Exception) {
                _mensaje.value = "Error al cargar productos: ${e.message}"
            }

            _cargando.value = false
        }
    }

    fun crearProducto(producto: Producto) {
        viewModelScope.launch {
            _cargando.value = true
            _mensaje.value = null

            try {
                val resultado = productoRepository.crearProducto(producto)
                if (resultado) {
                    _mensaje.value = "Producto creado exitosamente"
                    cargarProductos() // Recargar la lista
                } else {
                    _mensaje.value = "Error al crear producto"
                }
            } catch (e: Exception) {
                _mensaje.value = "Error al crear producto: ${e.message}"
            }

            _cargando.value = false
        }
    }

    fun actualizarProducto(id: String, producto: Producto) {
        viewModelScope.launch {
            _cargando.value = true
            _mensaje.value = null

            try {
                val resultado = productoRepository.actualizarProducto(id, producto)
                if (resultado) {
                    _mensaje.value = "Producto actualizado exitosamente"
                    cargarProductos() // Recargar la lista
                } else {
                    _mensaje.value = "Error al actualizar producto"
                }
            } catch (e: Exception) {
                _mensaje.value = "Error al actualizar producto: ${e.message}"
            }

            _cargando.value = false
        }
    }

    fun eliminarProducto(id: String) {
        viewModelScope.launch {
            _cargando.value = true
            _mensaje.value = null

            try {
                val resultado = productoRepository.eliminarProducto(id)
                if (resultado) {
                    _mensaje.value = "Producto eliminado exitosamente"
                    cargarProductos() // Recargar la lista
                } else {
                    _mensaje.value = "Error al eliminar producto"
                }
            } catch (e: Exception) {
                _mensaje.value = "Error al eliminar producto: ${e.message}"
            }

            _cargando.value = false
        }
    }

    fun limpiarMensaje() {
        _mensaje.value = null
    }
}