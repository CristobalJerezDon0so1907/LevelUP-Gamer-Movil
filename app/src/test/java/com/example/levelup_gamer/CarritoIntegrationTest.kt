package com.example.levelup_gamer

import com.example.levelup_gamer.model.ItemCarrito
import com.example.levelup_gamer.model.Producto
import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.matchers.shouldBe

class CarritoIntegrationTest : BehaviorSpec({

    given("un flujo de compra completo") {

        val productos = listOf(
            Producto(
                id = "1",
                nombre = "PC Gamer",
                precio = 1000.0,
                descripcion = "Computador potente ideal para jugar sin lag",
                imagenUrl = "img1.jpg",
                stock = 5
            ),
            Producto(
                id = "2",
                nombre = "Mouse Gamer",
                precio = 25.0,
                descripcion = "Mouse preciso y cómodo para juegos.",
                imagenUrl = "img2.jpg",
                stock = 10
            ),
            Producto(
                id = "3",
                nombre = "Control Xbox",
                precio = 75.0,
                descripcion = "Control ergonómico con buena precisión.",
                imagenUrl = "img3.jpg",
                stock = 8
            )
        )

        `when`("se agregan múltiples productos al carrito") {
            val carrito = mutableListOf<ItemCarrito>()
            productos.forEach { producto ->
                carrito.add(ItemCarrito(producto, 1))
            }

            then("el carrito debe contener todos los productos") {
                carrito.size shouldBe 3
                carrito.map { it.producto.nombre } shouldBe listOf("PC Gamer", "Mouse Gamer", "Control Xbox")
            }

            then("el total debe ser la suma de todos los productos") {
                val total = carrito.sumOf { it.producto.precio * it.cantidad }
                total shouldBe 1100.0 // 1000 + 25 + 75
            }
        }

        `when`("se modifica la cantidad de productos") {
            val carrito = mutableListOf<ItemCarrito>()
            val producto = productos.first() // Laptop
            carrito.add(ItemCarrito(producto, 2)) // Cantidad 2

            then("el subtotal debe reflejar la nueva cantidad") {
                val subtotal = carrito.first().producto.precio * carrito.first().cantidad
                subtotal shouldBe 2000.0 // 1000 * 2
            }
        }

        `when`("se vacía el carrito") {
            val carrito = mutableListOf<ItemCarrito>()
            productos.forEach { producto ->
                carrito.add(ItemCarrito(producto, 1))
            }
            carrito.clear()

            then("el carrito debe estar vacío") {
                carrito.isEmpty() shouldBe true
            }
        }
    }
})
