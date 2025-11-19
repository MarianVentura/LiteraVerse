package edu.ucne.literaverse.data.remote

import edu.ucne.literaverse.data.remote.dto.LoginRequest
import edu.ucne.literaverse.data.remote.dto.LoginResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path

interface LiteraVerseApi {
    @GET("api/Usuarios")
    suspend fun getUsuarios(): Response<List<LoginResponse>>

    @POST("api/Usuarios")
    suspend fun createUsuario(@Body request: LoginRequest): Response<LoginResponse>

    @GET("api/Usuarios/{id}")
    suspend fun getUsuario(@Path("id") id: Int): Response<LoginResponse>

    @PUT("api/Usuarios/{id}")
    suspend fun updateUsuario(
        @Path("id") id: Int,
        @Body request: LoginRequest
    ): Response<Unit>
}

