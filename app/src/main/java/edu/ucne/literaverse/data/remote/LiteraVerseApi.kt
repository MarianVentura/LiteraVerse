package edu.ucne.literaverse.data.remote

import edu.ucne.literaverse.data.remote.dto.LoginRequest
import edu.ucne.literaverse.data.remote.dto.LoginResponse
import edu.ucne.literaverse.data.remote.dto.GenreResponse
import edu.ucne.literaverse.data.remote.dto.StoryResponse
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

    @GET("api/Explore/featured")
    suspend fun getFeaturedStories(): Response<List<StoryResponse>>

    @GET("api/Explore/popular")
    suspend fun getPopularStories(): Response<List<StoryResponse>>

    @GET("api/Explore/new")
    suspend fun getNewStories(): Response<List<StoryResponse>>

    @GET("api/Genres")
    suspend fun getGenres(): Response<List<GenreResponse>>

    @GET("api/Explore/genre/{genreName}")
    suspend fun getStoriesByGenre(@Path("genreName") genreName: String): Response<List<StoryResponse>>
}