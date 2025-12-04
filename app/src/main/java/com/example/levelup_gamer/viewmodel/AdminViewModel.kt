package com.example.levelup_gamer.viewmodel

import androidx.lifecycle.ViewModel
import com.example.levelup_gamer.model.Producto
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

data class UsuarioAdmin(
    val id: String = "",
    val nombre: String = "",
    val correo: String = "",
    val rol: String = ""
)

class AdminViewModel : ViewModel() {

    private val db = FirebaseFirestore.getInstance()

    // ----- PRODUCTOS -----
    private val _productos = MutableStateFlow<List<Producto>>(emptyList())
    val productos: StateFlow<List<Producto>> = _productos

    private val _cargandoProductos = MutableStateFlow(false)
    val cargandoProductos: StateFlow<Boolean> = _cargandoProductos

    // ----- USUARIOS -----
    private val _usuarios = MutableStateFlow<List<UsuarioAdmin>>(emptyList())
    val usuarios: StateFlow<List<UsuarioAdmin>> = _usuarios

    private val _cargandoUsuarios = MutableStateFlow(false)
    val cargandoUsuarios: StateFlow<Boolean> = _cargandoUsuarios

    init {
        cargarProductos()
        cargarUsuarios()
    }

    // ---------------------- PRODUCTOS ----------------------

    fun cargarProductos() {
        _cargandoProductos.value = true
        db.collection("productos")
            .get()
            .addOnSuccessListener { snapshot ->
                val lista = snapshot.documents.map { doc ->
                    val p = doc.toObject(Producto::class.java) ?: Producto()
                    // usamos el id del documento como id del producto
                    p.copy(id = doc.id)
                }
                _productos.value = lista
                _cargandoProductos.value = false
            }
            .addOnFailureListener {
                _cargandoProductos.value = false
            }
    }

    fun agregarProducto(
        nombre: String,
        precio: Double,
        descripcion: String,
        imagenUrl: String,
        stock: Int
    ) {
        val docRef = db.collection("productos").document()
        val producto = Producto(
            id = docRef.id,
            nombre = nombre,
            precio = precio,
            descripcion = descripcion,
            imagenUrl = imagenUrl,
            stock = stock
        )

        docRef.set(producto)
            .addOnSuccessListener { cargarProductos() }
    }

    fun actualizarProducto(producto: Producto) {
        if (producto.id.isBlank()) return

        db.collection("productos")
            .document(producto.id)
            .set(producto)
            .addOnSuccessListener { cargarProductos() }
    }

    fun eliminarProducto(idProducto: String) {
        if (idProducto.isBlank()) return

        db.collection("productos")
            .document(idProducto)
            .delete()
            .addOnSuccessListener { cargarProductos() }
    }

    // ---------------------- USUARIOS ----------------------

    fun cargarUsuarios() {
        _cargandoUsuarios.value = true
        db.collection("usuario")
            .get()
            .addOnSuccessListener { snapshot ->
                val lista = snapshot.documents.map { doc ->
                    UsuarioAdmin(
                        id = doc.id,
                        nombre = doc.getString("nombre") ?: "",
                        correo = doc.getString("correo") ?: "",
                        rol = doc.getString("rol") ?: ""
                    )
                }
                _usuarios.value = lista
                _cargandoUsuarios.value = false
            }
            .addOnFailureListener {
                _cargandoUsuarios.value = false
            }
    }

    fun actualizarUsuarioNombre(idUsuario: String, nuevoNombre: String) {
        if (idUsuario.isBlank()) return

        db.collection("usuario")
            .document(idUsuario)
            .update("nombre", nuevoNombre)
            .addOnSuccessListener { cargarUsuarios() }
    }

    fun eliminarUsuario(idUsuario: String) {
        if (idUsuario.isBlank()) return

        db.collection("usuario")
            .document(idUsuario)
            .delete()
            .addOnSuccessListener { cargarUsuarios() }
    }
}
