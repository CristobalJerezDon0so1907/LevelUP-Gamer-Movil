package com.example.levelup_gamer.api
import com.example.levelup_gamer.model.Usuario
import retrofit2.Response
import retrofit2.http.*

interface UsuarioApiService {

    @GET("usuarios")
    suspend fun obtenerUsuarios(): Response<List<Usuario>>

    @GET("usuarios/{correo}")
    suspend fun obtenerUsuarioPorCorreo(@Path("correo") correo: String): Response<Usuario>

    @POST("usuarios")
    suspend fun crearUsuario(@Body usuario: Usuario): Response<Usuario>

    @PUT("usuarios/{correo}")
    suspend fun actualizarUsuario(
        @Path("correo") correo: String,
        @Body usuario: Usuario
    ): Response<Usuario>

    @DELETE("usuarios/{correo}")
    suspend fun eliminarUsuario(@Path("correo") correo: String): Response<Void>
}