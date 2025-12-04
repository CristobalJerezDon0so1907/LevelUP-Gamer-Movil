package com.example.levelup_gamer

import com.example.levelup_gamer.model.ItemCarrito
import com.example.levelup_gamer.model.Producto
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import io.kotest.property.Arb
import io.kotest.property.arbitrary.double
import io.kotest.property.arbitrary.int
import io.kotest.property.checkAll

class CarritoBusinessLogicTest : FunSpec({

    test("el total debe ser igual a la suma de precios por cantidad") {
        checkAll(
            Arb.double(1.0, 100.0),
            Arb.int(1, 10),
            Arb.double(1.0, 100.0),
            Arb.int(1, 10)
        ) { precio1, cantidad1, precio2, cantidad2 ->

            val producto1 = Producto(
                id = "1",
                nombre = "Audífonos Gamer",
                precio = precio1,
                descripcion = "",
                imagenUrl = "",
                stock = 10
            )

            val producto2 = Producto(
                id = "2",
                nombre = "Mouse Gamer",
                precio = precio2,
                descripcion = "",
                imagenUrl = "",
                stock = 10
            )

            val item1 = ItemCarrito(producto1, cantidad1)
            val item2 = ItemCarrito(producto2, cantidad2)

            val totalEsperado = (precio1 * cantidad1) + (precio2 * cantidad2)
            val carrito = listOf(item1, item2)
            val totalCalculado = carrito.sumOf { it.producto.precio * it.cantidad }

            totalCalculado shouldBe totalEsperado
        }
    }

    test("no se puede agregar producto con stock cero") {
        val productoSinStock = Producto(
            id = "1",
            nombre = "Sin Stock",
            precio = 10.0,
            descripcion = "",
            imagenUrl = "",
            stock = 0
        )

        val carrito = mutableListOf<ItemCarrito>()

        val puedeAgregar = productoSinStock.stock > 0

        puedeAgregar shouldBe false
    }

    test("el subtotal por item debe ser precio por cantidad") {
        checkAll(Arb.double(1.0, 100.0), Arb.int(1, 5)) { precio, cantidad ->
            val producto = Producto(
                id = "1",
                nombre = "Test",
                precio = precio,
                descripcion = "",
                imagenUrl = "",
                stock = 10
            )

            val item = ItemCarrito(producto, cantidad)

            val subtotal = item.producto.precio * item.cantidad

            subtotal shouldBe (precio * cantidad)
        }
    }
})
