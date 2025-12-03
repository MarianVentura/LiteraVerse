package edu.ucne.literaverse.data.repository

import edu.ucne.literaverse.data.mappers.toDomain
import edu.ucne.literaverse.data.remote.LiteraVerseApi
import edu.ucne.literaverse.data.remote.Resource
import edu.ucne.literaverse.data.remote.dto.StoryResponse
import edu.ucne.literaverse.domain.model.SearchFilters
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import retrofit2.Response

class SearchRepositoryImplTest {

    private lateinit var repository: SearchRepositoryImpl
    private lateinit var api: LiteraVerseApi

    @Before
    fun setup() {
        api = mockk()
        repository = SearchRepositoryImpl(api)
    }

    @Test
    fun `searchStories retorna Success cuando API responde correctamente`() = runTest {
        // Given
        val filters = SearchFilters(
            query = "fantasia",
            genre = "Fantasy",
            status = null
        )

        val mockStories = listOf(
            StoryResponse(
                storyId = 1,
                userId = 1,
                userName = "Author1",
                title = "Test Story",
                synopsis = "Synopsis",
                coverImageUrl = null,
                genre = "Fantasy",
                tags = null,
                viewCount = 100,
                createdAt = "2024-01-01",
                isDraft = false,
                isPublished = true,
                publishedAt = "2024-01-01",
                updatedAt = "2024-01-01"
            )
        )

        coEvery {
            api.searchStories(any(), any(), any())
        } returns Response.success(mockStories)


        val result = repository.searchStories(filters)


        assertTrue(result is Resource.Success)
        assertEquals(1, (result as Resource.Success).data?.size)
        coVerify { api.searchStories(any(), any(), any()) }
    }

    @Test
    fun `searchStories retorna Error cuando API falla`() = runTest {

        val filters = SearchFilters(query = "test")

        coEvery {
            api.searchStories(any(), any(), any())
        } returns Response.error(500, mockk(relaxed = true))


        val result = repository.searchStories(filters)


        assertTrue(result is Resource.Error)
        assertNotNull((result as Resource.Error).message)
    }

    @Test
    fun `searchStories retorna Error cuando hay excepción de red`() = runTest {

        val filters = SearchFilters(query = "test")

        coEvery {
            api.searchStories(any(), any(), any())
        } throws Exception("Network error")


        val result = repository.searchStories(filters)


        assertTrue(result is Resource.Error)
        assertEquals("Network error", (result as Resource.Error).message)
    }

    @Test
    fun `getPopularStories retorna Success con lista de historias`() = runTest {

        val mockStories = listOf(
            StoryResponse(
                storyId = 1,
                userId = 1,
                userName = "Author1",
                title = "Popular Story",
                synopsis = "Synopsis",
                coverImageUrl = null,
                genre = "Romance",
                tags = null,
                viewCount = 1000,
                createdAt = "2024-01-01",
                isDraft = false,
                isPublished = true,
                publishedAt = "2024-01-01",
                updatedAt = "2024-01-01"
            )
        )

        coEvery { api.getPopularStories() } returns Response.success(mockStories)


        val result = repository.getPopularStories()


        assertTrue(result is Resource.Success)
        assertEquals(1, (result as Resource.Success).data?.size)
    }

    @Test
    fun `getRecentStories retorna Success con lista de historias`() = runTest {

        val mockStories = listOf(
            StoryResponse(
                storyId = 1,
                userId = 1,
                userName = "Author1",
                title = "New Story",
                synopsis = "Synopsis",
                coverImageUrl = null,
                genre = "Mystery",
                tags = null,
                viewCount = 50,
                createdAt = "2024-12-01",
                isDraft = false,
                isPublished = true,
                publishedAt = "2024-12-01",
                updatedAt = "2024-12-01"
            )
        )

        coEvery { api.getNewStories() } returns Response.success(mockStories)


        val result = repository.getRecentStories()


        assertTrue(result is Resource.Success)
        assertEquals(1, (result as Resource.Success).data?.size)
    }

    @Test
    fun `searchStories con query vacío busca con null`() = runTest {

        val filters = SearchFilters(query = "")

        coEvery {
            api.searchStories(null, null, null)
        } returns Response.success(emptyList())


        repository.searchStories(filters)


        coVerify { api.searchStories(null, null, null) }
    }
}