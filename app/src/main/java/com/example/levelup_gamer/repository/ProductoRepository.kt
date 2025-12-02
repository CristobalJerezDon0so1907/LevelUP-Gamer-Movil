package com.example.levelup_gamer.repository

import com.example.levelup_gamer.model.Producto
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await

class ProductoRepository {
    private val db = FirebaseFirestore.getInstance()

    // Obtener todos los productos sin límites
    suspend fun obtenerProductos(): List<Producto> {
        return try {
            val snapshot = db.collection("producto").get().await()

            snapshot.documents.map { document ->
                Producto(
                    id = document.id,
                    nombre = document.getString("nombre") ?: "",
                    descripcion = document.getString("descripcion") ?: "",
                    precio = document.getDouble("precio") ?: 0.0,
                    imagenUrl = document.getString("imagenUrl") ?: "",
                    stock = document.getLong("stock")?.toInt() ?: 0
                )
            }
        } catch (e: Exception) {
            emptyList()
        }
    }

    // Crear un producto
    suspend fun crearProducto(producto: Producto): Boolean {
        return try {
            db.collection("producto")
                .document() // genera ID automático
                .set(producto)
                .await()
            true
        } catch (e: Exception) {
            false
        }
    }

    // Actualizar un producto completo
    suspend fun actualizarProducto(id: String, producto: Producto): Boolean {
        return try {
            db.collection("producto")
                .document(id)
                .set(producto)
                .await()
            true
        } catch (e: Exception) {
            false
        }
    }

    // Eliminar un producto
    suspend fun eliminarProducto(id: String): Boolean {
        return try {
            db.collection("producto")
                .document(id)
                .delete()
                .await()
            true
        } catch (e: Exception) {
            false
        }
    }

    // Actualizar solo el stock
    suspend fun actualizarStock(productoId: String, nuevoStock: Int): Boolean {
        return try {
            db.collection("producto")
                .document(productoId)
                .update("stock", nuevoStock)
                .await()
            true
        } catch (e: Exception) {
            false
        }
    }

    // Obtener producto por ID
    suspend fun obtenerProductoPorId(productoId: String): Producto? {
        return try {
            val document = db.collection("producto")
                .document(productoId)
                .get()
                .await()

            if (document.exists()) {
                Producto(
                    id = document.id,
                    nombre = document.getString("nombre") ?: "",
                    descripcion = document.getString("descripcion") ?: "",
                    precio = document.getDouble("precio") ?: 0.0,
                    imagenUrl = document.getString("imagenUrl") ?: "",
                    stock = document.getLong("stock")?.toInt() ?: 0
                )
            } else null
        } catch (e: Exception) {
            null
        }
    }
}
