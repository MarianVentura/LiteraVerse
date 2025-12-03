package edu.ucne.literaverse.data.repository

import edu.ucne.literaverse.data.remote.RemoteDataSource
import edu.ucne.literaverse.data.remote.Resource
import edu.ucne.literaverse.data.remote.dto.GenreResponse
import edu.ucne.literaverse.data.remote.dto.StoryResponse
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test

class ExploreRepositoryImplTest {

    private lateinit var repository: ExploreRepositoryImpl
    private lateinit var remoteDataSource: RemoteDataSource

    @Before
    fun setup() {
        remoteDataSource = mockk()
        repository = ExploreRepositoryImpl(remoteDataSource)
    }

    @Test
    fun `getFeaturedStories retorna Success cuando RemoteDataSource responde correctamente`() = runTest {
        // Given
        val mockStories = listOf(
            StoryResponse(
                storyId = 1,
                userId = 1,
                userName = "Author1",
                title = "Featured Story",
                synopsis = "Synopsis",
                coverImageUrl = null,
                genre = "Fantasy",
                tags = null,
                viewCount = 500,
                createdAt = "2024-01-01",
                isDraft = false,
                isPublished = true,
                publishedAt = "2024-01-01",
                updatedAt = "2024-01-01"
            )
        )

        coEvery { remoteDataSource.getFeaturedStories() } returns Resource.Success(mockStories)

        // When
        val result = repository.getFeaturedStories()

        // Then
        assertTrue(result is Resource.Success)
        assertEquals(1, (result as Resource.Success).data?.size)
        coVerify { remoteDataSource.getFeaturedStories() }
    }

    @Test
    fun `getFeaturedStories retorna Error cuando falla`() = runTest {
        // Given
        coEvery {
            remoteDataSource.getFeaturedStories()
        } returns Resource.Error("Network error")

        // When
        val result = repository.getFeaturedStories()

        // Then
        assertTrue(result is Resource.Error)
        assertNotNull((result as Resource.Error).message)
    }

    @Test
    fun `getPopularStories retorna Success con lista`() = runTest {
        // Given
        val mockStories = listOf(
            StoryResponse(
                storyId = 2,
                userId = 2,
                userName = "Author2",
                title = "Popular Story",
                synopsis = "Synopsis",
                coverImageUrl = null,
                genre = "Romance",
                tags = null,
                viewCount = 2000,
                createdAt = "2024-01-01",
                isDraft = false,
                isPublished = true,
                publishedAt = "2024-01-01",
                updatedAt = "2024-01-01"
            )
        )

        coEvery { remoteDataSource.getPopularStories() } returns Resource.Success(mockStories)

        // When
        val result = repository.getPopularStories()

        // Then
        assertTrue(result is Resource.Success)
        assertEquals(1, (result as Resource.Success).data?.size)
    }

    @Test
    fun `getNewStories retorna Success con lista`() = runTest {
        // Given
        val mockStories = listOf(
            StoryResponse(
                storyId = 3,
                userId = 3,
                userName = "Author3",
                title = "New Story",
                synopsis = "Synopsis",
                coverImageUrl = null,
                genre = "Sci-Fi",
                tags = null,
                viewCount = 10,
                createdAt = "2024-12-01",
                isDraft = false,
                isPublished = true,
                publishedAt = "2024-12-01",
                updatedAt = "2024-12-01"
            )
        )

        coEvery { remoteDataSource.getNewStories() } returns Resource.Success(mockStories)

        // When
        val result = repository.getNewStories()

        // Then
        assertTrue(result is Resource.Success)
        assertEquals(1, (result as Resource.Success).data?.size)
    }

    @Test
    fun `getGenres retorna Success con lista de géneros`() = runTest {
        // Given
        val mockGenres = listOf(
            GenreResponse(genreId = 1, name = "Fantasy"),
            GenreResponse(genreId = 2, name = "Romance")
        )

        coEvery { remoteDataSource.getGenres() } returns Resource.Success(mockGenres)

        // When
        val result = repository.getGenres()

        // Then
        assertTrue(result is Resource.Success)
        assertEquals(2, (result as Resource.Success).data?.size)
    }

    @Test
    fun `getStoriesByGenre retorna Success con historias filtradas`() = runTest {
        // Given
        val genreName = "Fantasy"
        val mockStories = listOf(
            StoryResponse(
                storyId = 1,
                userId = 1,
                userName = "Author1",
                title = "Fantasy Story",
                synopsis = "Synopsis",
                coverImageUrl = null,
                genre = genreName,
                tags = null,
                viewCount = 300,
                createdAt = "2024-01-01",
                isDraft = false,
                isPublished = true,
                publishedAt = "2024-01-01",
                updatedAt = "2024-01-01"
            )
        )

        coEvery {
            remoteDataSource.getStoriesByGenre(genreName)
        } returns Resource.Success(mockStories)

        // When
        val result = repository.getStoriesByGenre(genreName)

        // Then
        assertTrue(result is Resource.Success)
        assertEquals(1, (result as Resource.Success).data?.size)
        coVerify { remoteDataSource.getStoriesByGenre(genreName) }
    }

    @Test
    fun `getStoriesByGenre retorna Error cuando falla`() = runTest {
        // Given
        val genreName = "Fantasy"

        coEvery {
            remoteDataSource.getStoriesByGenre(genreName)
        } returns Resource.Error("Error al obtener historias")

        // When
        val result = repository.getStoriesByGenre(genreName)

        // Then
        assertTrue(result is Resource.Error)
    }
}