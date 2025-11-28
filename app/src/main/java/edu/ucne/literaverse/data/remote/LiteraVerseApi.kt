package edu.ucne.literaverse.data.remote

import edu.ucne.literaverse.data.remote.dto.LoginRequest
import edu.ucne.literaverse.data.remote.dto.LoginResponse
import edu.ucne.literaverse.data.remote.dto.RegisterRequest
import edu.ucne.literaverse.data.remote.dto.GenreResponse
import edu.ucne.literaverse.data.remote.dto.StoryResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path

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
}