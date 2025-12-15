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

                val stockActualEnCatalogo = _productos.value.find { it.id == producto.id }?.stock ?: productoActualizado.stock
                val stockDisponible = stockActualEnCatalogo - (itemExistente?.cantidad ?: 0)

                if (stockDisponible <= 0) {
                    enviarNotificacion("No quedan unidades disponibles")
                    return@launch
                }

                if (itemExistente != null) {
                    itemExistente.cantidad++
                } else {
                    carritoActual.add(ItemCarrito(productoActualizado, 1))
                }

                //ACTUALIZAR EL STOCK EN LA LISTA LOCAL (_productos)
                val nuevaListaProductos = _productos.value.map { p ->
                    if (p.id == producto.id) {
                        p.copy(stock = p.stock - 1)
                    } else {
                        p
                    }
                }
                _productos.value = nuevaListaProductos

                //ACTUALIZAR EL CARRITO
                _carrito.value = carritoActual

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

                    if (item.cantidad > 1) {
                        item.cantidad--
                        enviarNotificacion("Cantidad reducida")
                    } else {
                        carritoActual.remove(item)
                        enviarNotificacion("Producto eliminado del carrito")
                    }

                    // ACTUALIZAR EL STOCK EN LA LISTA LOCAL
                    val nuevaListaProductos = _productos.value.map { p ->
                        if (p.id == producto.id) {
                            p.copy(stock = p.stock + 1) // Sumar de vuelta a la lista local
                        } else {
                            p
                        }
                    }
                    _productos.value = nuevaListaProductos

                    //ACTUALIZAR EL CARRITO
                    _carrito.value = carritoActual
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
                    //Devolver el stock a la lista local
                    val cantidadDevuelta = item.cantidad
                    val nuevaListaProductos = _productos.value.map { p ->
                        if (p.id == producto.id) {
                            p.copy(stock = p.stock + cantidadDevuelta)
                        } else {
                            p
                        }
                    }
                    _productos.value = nuevaListaProductos

                    // 2. Eliminar del carrito
                    carritoActual.remove(item)
                    _carrito.value = carritoActual

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
                //Devolver el stock de todos los items a la lista local
                val itemsACancelar = _carrito.value
                var nuevaListaProductos = _productos.value

                itemsACancelar.forEach { item ->
                    nuevaListaProductos = nuevaListaProductos.map { p ->
                        if (p.id == item.producto.id) {
                            p.copy(stock = p.stock + item.cantidad)
                        } else {
                            p
                        }
                    }
                }
                _productos.value = nuevaListaProductos

                //Vaciar el carrito
                _carrito.value = emptyList()
                enviarNotificacion("Carrito vaciado")

            } catch (e: Exception) {
                enviarNotificacion("Error al vaciar el carrito")
            }
        }
    }

    fun confirmarCompra(correoUsuario: String) {
        if (_carrito.value.isEmpty()) {
            enviarNotificacion("El carrito está vacío.")
            return
        }

        val itemsAComprar = _carrito.value // Carrito actual
        val totalCompra = itemsAComprar.sumOf { it.producto.precio * it.cantidad }

        viewModelScope.launch {
            _cargando.value = true

            // Llama a la función transaccional en el repositorio
            val resultado = repository.realizarCheckoutYDescontarStock(
                itemsAComprar = itemsAComprar,
                correoUsuario = correoUsuario,
                totalCompra = totalCompra
            )

            _cargando.value = false

            resultado.fold(
                onSuccess = { pedidoId ->
                    //Éxito: Se descontó el stock y se guardó el pedido.
                    //Buscar Token FCM (mantenemos tu lógica original)
                    val db = FirebaseFirestore.getInstance()
                    val usuarioQuery = db.collection("usuario")
                        .whereEqualTo("correo", correoUsuario)
                        .limit(1)
                        .get()
                        .await()
                    val token = usuarioQuery.documents.firstOrNull()?.getString("fcmToken")

                    //Enviar la notificacion
                    if (token != null) {
                        FCMClient.enviarNotificacion(
                            token,
                            titulo = "¡Compra exitosa!",
                            mensaje = "Tu pedido ($pedidoId) ha sido comprado con éxito. Total: $$totalCompra"
                        )
                    }

                    //Limpiar el carrito en el ViewModel y refrescar catálogo
                    _carrito.value = emptyList()
                    cargarProductos() // Recarga productos para mostrar el stock actualizado

                    enviarNotificacion("Compra confirmada 🎉")
                },
                onFailure = { exception ->
                    //Fallo: Ocurrió un error (ej. Stock insuficiente)
                    val msg = exception.message ?: "Error desconocido al confirmar la compra."
                    enviarNotificacion("Error de compra: $msg")
                }
            )
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