package com.example.levelup_gamer.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.levelup_gamer.model.ItemCarrito
import com.example.levelup_gamer.model.Producto
import com.example.levelup_gamer.repository.ProductoRepository
import com.example.levelup_gamer.services.FCMClient
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

class CarritoViewModel(
    private val repository: ProductoRepository = ProductoRepository()
) : ViewModel() {

    private val _productos = MutableStateFlow<List<Producto>>(emptyList())
    val productos: StateFlow<List<Producto>> = _productos

    private val _carrito = MutableStateFlow<List<ItemCarrito>>(emptyList())
    val carrito: StateFlow<List<ItemCarrito>> = _carrito

    private val _cargando = MutableStateFlow(false)
    val cargando: StateFlow<Boolean> = _cargando

    // Notificaciones generales (para Snackbar / Toast)
    private val _notificacion = MutableStateFlow<String?>(null)
    val notificacion: StateFlow<String?> = _notificacion

    private fun enviarNotificacion(msg: String) {
        _notificacion.value = msg
    }

    fun limpiarNotificacion() {
        _notificacion.value = null
    }

    init {
        cargarProductos()
    }

    fun cargarProductos() {
        viewModelScope.launch {
            _cargando.value = true
            try {
                val lista = repository.obtenerProductos()
                _productos.value = lista
            } catch (e: Exception) {
                _productos.value = emptyList()
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
                val lista = repository.obtenerProductos()
                _productos.value = lista
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

    fun confirmarCompra(correoUsuario: String) {
        viewModelScope.launch {
            try {
                val db = FirebaseFirestore.getInstance()

                //Calcular el total
                val totalCompra = carrito.value.sumOf { it.producto.precio * it.cantidad }

                //Buscar el usuario por el correo
                val usuarioQuery = db.collection("usuario")
                    .whereEqualTo("correo", correoUsuario)
                    .limit(1)
                    .get()
                    .await()

                val usuarioDoc = usuarioQuery.documents.firstOrNull()
                val token = usuarioDoc?.getString("fcmToken")

                //total
                val pedido = hashMapOf(
                    "correoUsuario" to correoUsuario,
                    "estado" to "Pendiente",
                    "fecha" to System.currentTimeMillis(),
                    "total" to totalCompra
                )

                db.collection("pedidos").add(pedido).await()

                //Enviar la notificacion solo si se aprobo
                if (token != null) {
                    FCMClient.enviarNotificacion(
                        token,
                        titulo = "¡Compra exitosa!",
                        mensaje = "Tu pedido ha sido comprado con éxito. Total: $$totalCompra"
                    )
                }

                //Vaciar el carrito
                _carrito.value = emptyList()
                cargarProductos()

                //Notificacion en la app
                enviarNotificacion("Compra confirmada 🎉")

            } catch (e: Exception) {
                enviarNotificacion("Error al confirmar la compra")
            }
        }
    }


    private suspend fun actualizarProductosEnCatalogo() {
        try {
            val productosFirebase = repository.obtenerProductos()
            _productos.value = productosFirebase
        } catch (e: Exception) {
            enviarNotificacion("Error al actualizar el catálogo")
        }
    }

    fun obtenerTotal(): Double {
        return _carrito.value.sumOf { it.producto.precio * it.cantidad }
    }
}
