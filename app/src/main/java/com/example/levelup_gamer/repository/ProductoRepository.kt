package com.example.levelup_gamer.repository

import com.example.levelup_gamer.model.ItemCarrito
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

    suspend fun realizarCheckoutYDescontarStock(
        itemsAComprar: List<ItemCarrito>,
        correoUsuario: String,
        totalCompra: Double
    ): Result<String> {
        val db = FirebaseFirestore.getInstance()

        return try {
            val pedidoId = db.runTransaction { transaction ->
                val productosComprados = java.util.ArrayList<java.util.HashMap<String, Any>>()
                for (item in itemsAComprar) {
                    val productoRef = db.collection("producto").document(item.producto.id)
                    val productoDoc = transaction.get(productoRef)

                    if (!productoDoc.exists()) {
                        throw Exception("Producto no encontrado: ${item.producto.nombre}")
                    }

                    val stockActual = productoDoc.getLong("stock")?.toInt() ?: 0
                    val cantidadDescontar = item.cantidad

                    if (stockActual < cantidadDescontar) {
                        //Abortar transacción si el stock es insuficiente
                        throw Exception("Stock insuficiente para ${item.producto.nombre}")
                    }

                    val nuevoStock = stockActual - cantidadDescontar
                    transaction.update(productoRef, "stock", nuevoStock)

                    // Mapear el producto a un HashMap
                    val itemMap = java.util.HashMap<String, Any>().apply {
                        put("productoId", item.producto.id)
                        put("nombre", item.producto.nombre)
                        put("cantidad", item.cantidad)
                        put("precioUnitario", item.producto.precio)
                    }
                    productosComprados.add(itemMap)
                }

                // Crear el objeto Pedido Final
                val nuevoPedido = java.util.HashMap<String, Any>().apply {
                    put("correoUsuario", correoUsuario)
                    put("estado", "Pendiente")
                    put("fecha", System.currentTimeMillis())
                    put("total", totalCompra)
                    put("items", productosComprados) // Agregar la lista de productos comprados
                }

                val nuevoPedidoRef = db.collection("pedidos").document()
                transaction.set(nuevoPedidoRef, nuevoPedido)

                nuevoPedidoRef.id
            }.await()

            Result.success(pedidoId ?: "Error ID")

        } catch (e: Exception) {
            // Loguear el error de la transacción para depuración
            android.util.Log.e("Checkout", "Error crítico en transacción: ${e.message}", e)
            Result.failure(e)
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


    suspend fun descontarStockUnidad(productoId: String, cantidad: Int): Result<Int> {
        return try {
            val productoRef = db.collection("producto").document(productoId)

            // 1. Obtener el documento actual
            val document = productoRef.get().await()

            val stockActual = document.getLong("stock")?.toInt() ?: 0

            if (stockActual < cantidad) {
                return Result.failure(Exception("Stock insuficiente en Firestore."))
            }

            val nuevoStock = stockActual - cantidad

            // 2. Actualizar el stock en la base de datos
            productoRef.update("stock", nuevoStock).await()

            Result.success(nuevoStock)

        } catch (e: Exception) {
            android.util.Log.e("Repository", "Error al descontar stock: ${e.message}")
            Result.failure(e)
        }
    }
}