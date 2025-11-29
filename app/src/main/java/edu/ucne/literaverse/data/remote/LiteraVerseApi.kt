package edu.ucne.literaverse.data.remote

import edu.ucne.literaverse.data.remote.dto.ChapterResponse
import edu.ucne.literaverse.data.remote.dto.CreateChapterRequest
import edu.ucne.literaverse.data.remote.dto.CreateStoryRequest
import edu.ucne.literaverse.data.remote.dto.LoginRequest
import edu.ucne.literaverse.data.remote.dto.LoginResponse
import edu.ucne.literaverse.data.remote.dto.RegisterRequest
import edu.ucne.literaverse.data.remote.dto.GenreResponse
import edu.ucne.literaverse.data.remote.dto.StoryDetailResponse
import edu.ucne.literaverse.data.remote.dto.StoryResponse
import edu.ucne.literaverse.data.remote.dto.UpdateChapterRequest
import edu.ucne.literaverse.data.remote.dto.UpdateStoryRequest
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT
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

    @GET("api/Stories")
    suspend fun getAllStories(): Response<List<StoryResponse>>

    @POST("api/Stories")
    suspend fun createStory(@Body request: CreateStoryRequest): Response<StoryResponse>

    @GET("api/Stories/{id}")
    suspend fun getStoryById(@Path("id") id: Int): Response<StoryDetailResponse>

    @PUT("api/Stories/{id}")
    suspend fun updateStory(@Path("id") id: Int, @Body request: UpdateStoryRequest): Response<StoryResponse>

    @DELETE("api/Stories/{id}")
    suspend fun deleteStory(@Path("id") id: Int): Response<Unit>

    @GET("api/Stories/user/{userId}")
    suspend fun getStoriesByUser(@Path("userId") userId: Int): Response<List<StoryResponse>>

    @POST("api/Stories/{id}/publish")
    suspend fun publishStory(@Path("id") id: Int): Response<Unit>

    @POST("api/Stories/{id}/unpublish")
    suspend fun unpublishStory(@Path("id") id: Int): Response<Unit>

    @GET("api/Stories/{storyId}/Chapters")
    suspend fun getChaptersByStory(@Path("storyId") storyId: Int): Response<List<ChapterResponse>>

    @POST("api/Stories/{storyId}/Chapters")
    suspend fun createChapter(@Path("storyId") storyId: Int, @Body request: CreateChapterRequest): Response<ChapterResponse>

    @GET("api/Stories/{storyId}/Chapters/{chapterId}")
    suspend fun getChapterById(@Path("storyId") storyId: Int, @Path("chapterId") chapterId: Int): Response<ChapterResponse>

    @PUT("api/Stories/{storyId}/Chapters/{chapterId}")
    suspend fun updateChapter(@Path("storyId") storyId: Int, @Path("chapterId") chapterId: Int, @Body request: UpdateChapterRequest): Response<ChapterResponse>

    @DELETE("api/Stories/{storyId}/Chapters/{chapterId}")
    suspend fun deleteChapter(@Path("storyId") storyId: Int, @Path("chapterId") chapterId: Int): Response<Unit>

    @POST("api/Stories/{storyId}/Chapters/{chapterId}/publish")
    suspend fun publishChapter(@Path("storyId") storyId: Int, @Path("chapterId") chapterId: Int): Response<Unit>

    @POST("api/Stories/{storyId}/Chapters/{chapterId}/unpublish")
    suspend fun unpublishChapter(@Path("storyId") storyId: Int, @Path("chapterId") chapterId: Int): Response<Unit>
}