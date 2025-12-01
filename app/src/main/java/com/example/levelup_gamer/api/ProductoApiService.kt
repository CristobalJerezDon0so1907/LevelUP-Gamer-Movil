package com.example.levelup_gamer.api

import com.example.levelup_gamer.model.Producto
import retrofit2.Response
import retrofit2.http.*

interface ProductoApiService {

    @GET("productos")
    suspend fun obtenerProductos(): Response<List<Producto>>

    @GET("productos/{id}")
    suspend fun obtenerProductoPorId(@Path("id") id: String): Response<Producto>

    @POST("productos")
    suspend fun crearProducto(@Body producto: Producto): Response<Producto>

    @PUT("productos/{id}")
    suspend fun actualizarProducto(
        @Path("id") id: String,
        @Body producto: Producto
    ): Response<Producto>

    @DELETE("productos/{id}")
    suspend fun eliminarProducto(@Path("id") id: String): Response<Void>

    @PATCH("productos/{id}/stock")
    suspend fun actualizarStock(
        @Path("id") id: String,
        @Body stockUpdate: StockUpdate
    ): Response<Producto>
}

data class StockUpdate(val stock: Int)