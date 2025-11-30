package edu.ucne.literaverse.data.remote

import edu.ucne.literaverse.data.remote.dto.LoginRequest
import edu.ucne.literaverse.data.remote.dto.LoginResponse
import edu.ucne.literaverse.data.remote.dto.RegisterRequest
import edu.ucne.literaverse.data.remote.dto.GenreResponse
import edu.ucne.literaverse.data.remote.dto.StoryResponse
import edu.ucne.literaverse.data.remote.dto.NovelDto
import edu.ucne.literaverse.data.remote.dto.SearchNovelsResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Query

interface LiteraVerseApi {
    @POST("api/Auth/Login")
    suspend fun login(@Body request: LoginRequest): Response<LoginResponse>

    @POST("api/Auth/Register")
    suspend fun register(@Body request: RegisterRequest): Response<LoginResponse>

    @POST("api/Auth/Logout")
    suspend fun logout(@Body token: String): Response<Unit>

    @POST("api/Auth/ValidateToken")
    suspend fun validateToken(@Body token: String): Response<Map<String, Any>>

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


    @GET("api/Novelas")
    suspend fun searchNovels(
        @Query("busqueda") query: String? = null,
        @Query("genero") genero: String? = null,
        @Query("categoria") categoria: String? = null,
        @Query("estado") estado: String? = null,
        @Query("ordenarPor") ordenarPor: String? = null,
        @Query("orden") orden: String? = null
    ): Response<SearchNovelsResponse>

    @GET("api/Novelas/{id}")
    suspend fun getNovelById(@Path("id") id: Int): Response<NovelDto>

    @GET("api/Novelas/Populares")
    suspend fun getPopularNovels(
        @Query("limite") limite: Int? = 20
    ): Response<SearchNovelsResponse>

    @GET("api/Novelas/Recientes")
    suspend fun getRecentNovels(
        @Query("limite") limite: Int? = 20
    ): Response<SearchNovelsResponse>
}