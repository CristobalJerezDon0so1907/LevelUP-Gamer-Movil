package com.example.levelup_gamer.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.levelup_gamer.model.ItemCarrito
import com.example.levelup_gamer.model.Producto
import com.example.levelup_gamer.repository.ProductoRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class CarritoViewModel : ViewModel() {

    private val repository = ProductoRepository()

    private val _productos = MutableStateFlow<List<Producto>>(emptyList())
    val productos: StateFlow<List<Producto>> = _productos

    private val _carrito = MutableStateFlow<List<ItemCarrito>>(emptyList())
    val carrito: StateFlow<List<ItemCarrito>> = _carrito

    private val _cargando = MutableStateFlow(false)
    val cargando: StateFlow<Boolean> = _cargando

    // 🔔 NUEVO → Notificaciones generales
    private val _notificacion = MutableStateFlow<String?>(null)
    val notificacion: StateFlow<String?> = _notificacion

    private fun enviarNotificacion(msg: String) {
        _notificacion.value = msg
    }

    private var ultimoDocumento: Any? = null
    private val limiteProductos = 10

    init {
        cargarProductos()
    }

    fun cargarProductos() {
        _cargando.value = true
        viewModelScope.launch {
            try {
                val resultado = repository.obtenerProductos(limite = limiteProductos)
                _productos.value = resultado.productos
                ultimoDocumento = resultado.ultimoDocumento
            } catch (e: Exception) {
                enviarNotificacion("Error al cargar productos")
            } finally {
                _cargando.value = false
            }
        }
    }

    fun cargarMasProductos() {
        if (_cargando.value) return

        _cargando.value = true
        viewModelScope.launch {
            try {
                val resultado = repository.obtenerMasProductos(
                    limite = limiteProductos,
                    ultimoDocumento = ultimoDocumento
                )

                if (resultado.productos.isNotEmpty()) {
                    _productos.value = _productos.value + resultado.productos
                    ultimoDocumento = resultado.ultimoDocumento
                }
            } catch (e: Exception) {
                enviarNotificacion("No se pudieron cargar más productos")
            } finally {
                _cargando.value = false
            }
        }
    }

    fun agregarAlCarrito(producto: Producto) {
        viewModelScope.launch {
            try {
                val productoActualizado = repository.obtenerProductoPorId(producto.id)
                if (productoActualizado == null || productoActualizado.stock <= 0) {
                    enviarNotificacion("Sin stock disponible")
                    return@launch
                }

                val carritoActual = _carrito.value.toMutableList()
                val itemExistente = carritoActual.find { it.producto.id == producto.id }

                val stockDisponible =
                    productoActualizado.stock - (itemExistente?.cantidad ?: 0)
                if (stockDisponible <= 0) {
                    enviarNotificacion("No quedan unidades disponibles")
                    return@launch
                }

                if (itemExistente != null) {
                    itemExistente.cantidad++
                } else {
                    carritoActual.add(ItemCarrito(productoActualizado, 1))
                }

                repository.actualizarStock(producto.id, productoActualizado.stock - 1)

                _carrito.value = carritoActual
                actualizarProductosEnCatalogo()

                enviarNotificacion("Producto agregado al carrito")

            } catch (e: Exception) {
                enviarNotificacion("Error al agregar al carrito")
            }
        }
    }

    fun removerDelCarrito(producto: Producto) {
        viewModelScope.launch {
            try {
                val carritoActual = _carrito.value.toMutableList()
                val item = carritoActual.find { it.producto.id == producto.id }

                if (item != null) {
                    val productoActualizado = repository.obtenerProductoPorId(producto.id)

                    if (productoActualizado != null) {
                        repository.actualizarStock(
                            producto.id,
                            productoActualizado.stock + 1
                        )
                    }

                    if (item.cantidad > 1) {
                        item.cantidad--
                        enviarNotificacion("Cantidad reducida")
                    } else {
                        carritoActual.remove(item)
                        enviarNotificacion("Producto eliminado del carrito")
                    }

                    _carrito.value = carritoActual
                    actualizarProductosEnCatalogo()
                }

            } catch (e: Exception) {
                enviarNotificacion("Error al remover producto")
            }
        }
    }

    fun eliminarProductoDelCarrito(producto: Producto) {
        viewModelScope.launch {
            try {
                val carritoActual = _carrito.value.toMutableList()
                val item = carritoActual.find { it.producto.id == producto.id }

                if (item != null) {
                    val productoActualizado = repository.obtenerProductoPorId(producto.id)

                    if (productoActualizado != null) {
                        repository.actualizarStock(
                            producto.id,
                            productoActualizado.stock + item.cantidad
                        )
                    }

                    carritoActual.remove(item)
                    _carrito.value = carritoActual

                    actualizarProductosEnCatalogo()

                    enviarNotificacion("Producto eliminado completamente")

                }

            } catch (e: Exception) {
                enviarNotificacion("Error al eliminar producto del carrito")
            }
        }
    }

    fun vaciarCarrito() {
        viewModelScope.launch {
            try {
                _carrito.value.forEach { item ->
                    val productoActualizado =
                        repository.obtenerProductoPorId(item.producto.id)
                    if (productoActualizado != null) {
                        repository.actualizarStock(
                            item.producto.id,
                            productoActualizado.stock + item.cantidad
                        )
                    }
                }

                _carrito.value = emptyList()
                actualizarProductosEnCatalogo()

                enviarNotificacion("Carrito vaciado")

            } catch (e: Exception) {
                enviarNotificacion("Error al vaciar el carrito")
            }
        }
    }

    fun confirmarCompra() {
        viewModelScope.launch {
            try {
                _carrito.value = emptyList()
                cargarProductos()

                enviarNotificacion("Compra confirmada 🎉")

            } catch (e: Exception) {
                enviarNotificacion("Error al confirmar la compra")
            }
        }
    }

    private suspend fun actualizarProductosEnCatalogo() {
        val resultado =
            repository.obtenerProductos(limite = _productos.value.size + limiteProductos)
        _productos.value = resultado.productos
        ultimoDocumento = resultado.ultimoDocumento
    }

    fun obtenerTotal(): Double {
        return _carrito.value.sumOf { it.producto.precio * it.cantidad }
    }
}
