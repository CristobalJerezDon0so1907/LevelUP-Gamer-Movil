package com.example.levelup_gamer.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.levelup_gamer.model.Producto
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class GestionProductosViewModel : ViewModel() {

    private val db = FirebaseFirestore.getInstance()

    private val _productos = MutableStateFlow<List<Producto>>(emptyList())
    val productos: StateFlow<List<Producto>> = _productos

    private val _productoSeleccionado = MutableStateFlow<Producto?>(null)
    val productoSeleccionado: StateFlow<Producto?> = _productoSeleccionado

    private val _cargando = MutableStateFlow(false)
    val cargando: StateFlow<Boolean> = _cargando

    private val _mensaje = MutableStateFlow<String?>(null)
    val mensaje: StateFlow<String?> = _mensaje

    fun limpiarMensaje() {
        _mensaje.value = null
    }

    fun cargarProductos() {
        _cargando.value = true
        viewModelScope.launch {
            db.collection("producto")
                .get()
                .addOnSuccessListener { query ->
                    val lista = query.documents.map { doc ->
                        // Mapear doc -> Producto y añadir el id del documento
                        val p = doc.toObject(Producto::class.java) ?: Producto()
                        p.copy(id = doc.id)
                    }
                    _productos.value = lista
                    _cargando.value = false
                }
                .addOnFailureListener {
                    _cargando.value = false
                    _mensaje.value = "Error al cargar productos"
                }
        }
    }

    fun eliminarProducto(idProducto: String) {
        if (idProducto.isBlank()) return

        viewModelScope.launch {
            db.collection("producto")
                .document(idProducto)
                .delete()
                .addOnSuccessListener {
                    _mensaje.value = "Producto eliminado"
                    // recargar lista
                    cargarProductos()
                }
                .addOnFailureListener {
                    _mensaje.value = "Error al eliminar producto"
                }
        }
    }

    fun seleccionarProducto(producto: Producto?) {
        _productoSeleccionado.value = producto
    }

    fun guardarProducto(producto: Producto) {
        viewModelScope.launch {
            if (producto.id.isBlank()) {
                // Crear nuevo
                db.collection("producto")
                    .add(
                        mapOf(
                            "nombre" to producto.nombre,
                            "precio" to producto.precio,
                            "descripcion" to producto.descripcion,
                            "imagenUrl" to producto.imagenUrl,
                            "stock" to producto.stock
                        )
                    )
                    .addOnSuccessListener {
                        _mensaje.value = "Producto creado"
                        cargarProductos()
                    }
                    .addOnFailureListener {
                        _mensaje.value = "Error al crear producto"
                    }
            } else {
                // Actualizar existente
                db.collection("producto")
                    .document(producto.id)
                    .update(
                        mapOf(
                            "nombre" to producto.nombre,
                            "precio" to producto.precio,
                            "descripcion" to producto.descripcion,
                            "imagenUrl" to producto.imagenUrl,
                            "stock" to producto.stock
                        )
                    )
                    .addOnSuccessListener {
                        _mensaje.value = "Producto actualizado"
                        cargarProductos()
                    }
                    .addOnFailureListener {
                        _mensaje.value = "Error al actualizar producto"
                    }
            }
        }
    }
}
