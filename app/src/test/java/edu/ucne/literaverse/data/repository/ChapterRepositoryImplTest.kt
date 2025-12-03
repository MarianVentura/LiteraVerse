package edu.ucne.literaverse.data.repository

import edu.ucne.literaverse.data.local.dao.ChapterDao
import edu.ucne.literaverse.data.local.entities.ChapterEntity
import edu.ucne.literaverse.data.remote.RemoteDataSource
import edu.ucne.literaverse.data.remote.Resource
import edu.ucne.literaverse.data.remote.dto.ChapterResponse
import edu.ucne.literaverse.data.remote.dto.CreateChapterRequest
import edu.ucne.literaverse.data.remote.dto.UpdateChapterRequest
import io.mockk.*
import kotlinx.coroutines.test.runTest
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test

class ChapterRepositoryImplTest {

    private lateinit var repository: ChapterRepositoryImpl
    private lateinit var remoteDataSource: RemoteDataSource
    private lateinit var chapterDao: ChapterDao

    @Before
    fun setup() {
        remoteDataSource = mockk()
        chapterDao = mockk(relaxed = true)
        repository = ChapterRepositoryImpl(remoteDataSource, chapterDao)
    }

    @Test
    fun `createChapter retorna Success cuando se crea correctamente`() = runTest {
        // Given
        val storyId = 10
        val title = "Chapter 1"
        val content = "Content of chapter"
        val chapterNumber = 1
        val isDraft = true

        val mockResponse = ChapterResponse(
            chapterId = 1,
            storyId = storyId,
            title = title,
            content = content,
            chapterNumber = chapterNumber,
            isDraft = isDraft,
            isPublished = false,
            createdAt = "2024-01-01",
            publishedAt = null,
            updatedAt = "2024-01-01"
        )

        coEvery {
            remoteDataSource.createChapter(storyId, any())
        } returns Resource.Success(mockResponse)

        // When
        val result = repository.createChapter(storyId, title, content, chapterNumber, isDraft)

        // Then
        assertTrue(result is Resource.Success)
        assertEquals(title, (result as Resource.Success).data?.title)
        coVerify { remoteDataSource.createChapter(storyId, any()) }
        coVerify { chapterDao.upsert(any()) }
    }

    @Test
    fun `createChapter retorna Error cuando falla la creación`() = runTest {
        // Given
        val storyId = 10
        val title = "Chapter 1"
        val content = "Content"
        val chapterNumber = 1
        val isDraft = true

        coEvery {
            remoteDataSource.createChapter(storyId, any())
        } returns Resource.Error("Error al crear capítulo")

        // When
        val result = repository.createChapter(storyId, title, content, chapterNumber, isDraft)

        // Then
        assertTrue(result is Resource.Error)
        assertNotNull((result as Resource.Error).message)
    }

    @Test
    fun `getChapterById retorna Success desde remoto y guarda en local`() = runTest {
        // Given
        val storyId = 10
        val chapterId = 1

        val mockResponse = ChapterResponse(
            chapterId = chapterId,
            storyId = storyId,
            title = "Chapter 1",
            content = "Content",
            chapterNumber = 1,
            isDraft = false,
            isPublished = true,
            createdAt = "2024-01-01",
            publishedAt = "2024-01-01",
            updatedAt = "2024-01-01"
        )

        coEvery {
            remoteDataSource.getChapterById(storyId, chapterId)
        } returns Resource.Success(mockResponse)

        // When
        val result = repository.getChapterById(storyId, chapterId)

        // Then
        assertTrue(result is Resource.Success)
        assertEquals(chapterId, (result as Resource.Success).data?.chapterId)
        coVerify { remoteDataSource.getChapterById(storyId, chapterId) }
        coVerify { chapterDao.upsert(any()) }
    }

    @Test
    fun `getChapterById retorna desde local cuando falla remoto`() = runTest {
        // Given
        val storyId = 10
        val chapterId = 1

        val mockEntity = ChapterEntity(
            chapterId = chapterId,
            storyId = storyId,
            title = "Local Chapter",
            content = "Local Content",
            chapterNumber = 1,
            isDraft = false,
            isPublished = true,
            createdAt = "2024-01-01",
            publishedAt = "2024-01-01",
            updatedAt = "2024-01-01",
            needsSync = false
        )

        coEvery {
            remoteDataSource.getChapterById(storyId, chapterId)
        } returns Resource.Error("Network error")

        coEvery {
            chapterDao.getChapterById(chapterId)
        } returns mockEntity

        // When
        val result = repository.getChapterById(storyId, chapterId)

        // Then
        assertTrue(result is Resource.Success)
        assertEquals("Local Chapter", (result as Resource.Success).data?.title)
    }

    @Test
    fun `getChapterById retorna Error cuando no hay datos locales ni remotos`() = runTest {
        // Given
        val storyId = 10
        val chapterId = 1

        coEvery {
            remoteDataSource.getChapterById(storyId, chapterId)
        } returns Resource.Error("Not found")

        coEvery {
            chapterDao.getChapterById(chapterId)
        } returns null

        // When
        val result = repository.getChapterById(storyId, chapterId)

        // Then
        assertTrue(result is Resource.Error)
    }

    @Test
    fun `updateChapter actualiza capítulo correctamente`() = runTest {
        // Given
        val storyId = 10
        val chapterId = 1
        val title = "Updated Title"
        val content = "Updated Content"
        val isDraft = false

        val mockResponse = ChapterResponse(
            chapterId = chapterId,
            storyId = storyId,
            title = title,
            content = content,
            chapterNumber = 1,
            isDraft = isDraft,
            isPublished = true,
            createdAt = "2024-01-01",
            publishedAt = "2024-01-01",
            updatedAt = "2024-01-02"
        )

        coEvery {
            remoteDataSource.updateChapter(storyId, chapterId, any())
        } returns Resource.Success(mockResponse)

        // When
        val result = repository.updateChapter(storyId, chapterId, title, content, isDraft)

        // Then
        assertTrue(result is Resource.Success)
        assertEquals(title, (result as Resource.Success).data?.title)
        coVerify { remoteDataSource.updateChapter(storyId, chapterId, any()) }
        coVerify { chapterDao.upsert(any()) }
    }

    @Test
    fun `updateChapter retorna Error cuando falla`() = runTest {
        // Given
        val storyId = 10
        val chapterId = 1
        val title = "Title"
        val content = "Content"
        val isDraft = false

        coEvery {
            remoteDataSource.updateChapter(storyId, chapterId, any())
        } returns Resource.Error("Error al actualizar")

        // When
        val result = repository.updateChapter(storyId, chapterId, title, content, isDraft)

        // Then
        assertTrue(result is Resource.Error)
    }

    @Test
    fun `deleteChapter elimina de remoto y local`() = runTest {
        // Given
        val storyId = 10
        val chapterId = 1

        coEvery {
            remoteDataSource.deleteChapter(storyId, chapterId)
        } returns Resource.Success(Unit)

        // When
        val result = repository.deleteChapter(storyId, chapterId)

        // Then
        assertTrue(result is Resource.Success)
        coVerify { remoteDataSource.deleteChapter(storyId, chapterId) }
        coVerify { chapterDao.delete(chapterId) }
    }

    @Test
    fun `deleteChapter retorna Error cuando falla`() = runTest {
        // Given
        val storyId = 10
        val chapterId = 1

        coEvery {
            remoteDataSource.deleteChapter(storyId, chapterId)
        } returns Resource.Error("No se puede eliminar")

        // When
        val result = repository.deleteChapter(storyId, chapterId)

        // Then
        assertTrue(result is Resource.Error)
        coVerify(exactly = 0) { chapterDao.delete(any()) }
    }

    @Test
    fun `publishChapter actualiza estado correctamente`() = runTest {
        // Given
        val storyId = 10
        val chapterId = 1

        val mockEntity = ChapterEntity(
            chapterId = chapterId,
            storyId = storyId,
            title = "Chapter",
            content = "Content",
            chapterNumber = 1,
            isDraft = true,
            isPublished = false,
            createdAt = "2024-01-01",
            publishedAt = null,
            updatedAt = "2024-01-01",
            needsSync = false
        )

        coEvery {
            remoteDataSource.publishChapter(storyId, chapterId)
        } returns Resource.Success(Unit)

        coEvery {
            chapterDao.getChapterById(chapterId)
        } returns mockEntity

        // When
        val result = repository.publishChapter(storyId, chapterId)

        // Then
        assertTrue(result is Resource.Success)
        coVerify { remoteDataSource.publishChapter(storyId, chapterId) }
        coVerify {
            chapterDao.upsert(match {
                it.isPublished == true && it.isDraft == false
            })
        }
    }

    @Test
    fun `unpublishChapter actualiza estado correctamente`() = runTest {
        // Given
        val storyId = 10
        val chapterId = 1

        val mockEntity = ChapterEntity(
            chapterId = chapterId,
            storyId = storyId,
            title = "Chapter",
            content = "Content",
            chapterNumber = 1,
            isDraft = false,
            isPublished = true,
            createdAt = "2024-01-01",
            publishedAt = "2024-01-01",
            updatedAt = "2024-01-01",
            needsSync = false
        )

        coEvery {
            remoteDataSource.unpublishChapter(storyId, chapterId)
        } returns Resource.Success(Unit)

        coEvery {
            chapterDao.getChapterById(chapterId)
        } returns mockEntity

        // When
        val result = repository.unpublishChapter(storyId, chapterId)

        // Then
        assertTrue(result is Resource.Success)
        coVerify { remoteDataSource.unpublishChapter(storyId, chapterId) }
        coVerify {
            chapterDao.upsert(match {
                it.isPublished == false && it.isDraft == true
            })
        }
    }

    @Test
    fun `getNextChapter retorna siguiente capítulo publicado`() = runTest {
        // Given
        val storyId = 10
        val currentChapterNumber = 1

        val mockChapters = listOf(
            ChapterEntity(
                chapterId = 1,
                storyId = storyId,
                title = "Chapter 1",
                content = "Content 1",
                chapterNumber = 1,
                isDraft = false,
                isPublished = true,
                createdAt = "2024-01-01",
                publishedAt = "2024-01-01",
                updatedAt = "2024-01-01",
                needsSync = false
            ),
            ChapterEntity(
                chapterId = 2,
                storyId = storyId,
                title = "Chapter 2",
                content = "Content 2",
                chapterNumber = 2,
                isDraft = false,
                isPublished = true,
                createdAt = "2024-01-02",
                publishedAt = "2024-01-02",
                updatedAt = "2024-01-02",
                needsSync = false
            ),
            ChapterEntity(
                chapterId = 3,
                storyId = storyId,
                title = "Chapter 3",
                content = "Content 3",
                chapterNumber = 3,
                isDraft = true,
                isPublished = false,
                createdAt = "2024-01-03",
                publishedAt = null,
                updatedAt = "2024-01-03",
                needsSync = false
            )
        )

        coEvery {
            chapterDao.getChaptersByStorySync(storyId)
        } returns mockChapters

        // When
        val result = repository.getNextChapter(storyId, currentChapterNumber)

        // Then
        assertTrue(result is Resource.Success)
        assertNotNull((result as Resource.Success).data)
        assertEquals(2, result.data?.chapterNumber)
        assertEquals("Chapter 2", result.data?.title)
    }

    @Test
    fun `getNextChapter retorna null cuando no hay siguiente capítulo`() = runTest {
        // Given
        val storyId = 10
        val currentChapterNumber = 2

        val mockChapters = listOf(
            ChapterEntity(
                chapterId = 1,
                storyId = storyId,
                title = "Chapter 1",
                content = "Content 1",
                chapterNumber = 1,
                isDraft = false,
                isPublished = true,
                createdAt = "2024-01-01",
                publishedAt = "2024-01-01",
                updatedAt = "2024-01-01",
                needsSync = false
            ),
            ChapterEntity(
                chapterId = 2,
                storyId = storyId,
                title = "Chapter 2",
                content = "Content 2",
                chapterNumber = 2,
                isDraft = false,
                isPublished = true,
                createdAt = "2024-01-02",
                publishedAt = "2024-01-02",
                updatedAt = "2024-01-02",
                needsSync = false
            )
        )

        coEvery {
            chapterDao.getChaptersByStorySync(storyId)
        } returns mockChapters

        // When
        val result = repository.getNextChapter(storyId, currentChapterNumber)

        // Then
        assertTrue(result is Resource.Success)
        assertNull((result as Resource.Success).data)
    }

    @Test
    fun `getNextChapter ignora capítulos en borrador`() = runTest {
        // Given
        val storyId = 10
        val currentChapterNumber = 1

        val mockChapters = listOf(
            ChapterEntity(
                chapterId = 1,
                storyId = storyId,
                title = "Chapter 1",
                content = "Content 1",
                chapterNumber = 1,
                isDraft = false,
                isPublished = true,
                createdAt = "2024-01-01",
                publishedAt = "2024-01-01",
                updatedAt = "2024-01-01",
                needsSync = false
            ),
            ChapterEntity(
                chapterId = 2,
                storyId = storyId,
                title = "Chapter 2 Draft",
                content = "Content 2",
                chapterNumber = 2,
                isDraft = true,
                isPublished = false,
                createdAt = "2024-01-02",
                publishedAt = null,
                updatedAt = "2024-01-02",
                needsSync = false
            ),
            ChapterEntity(
                chapterId = 3,
                storyId = storyId,
                title = "Chapter 3",
                content = "Content 3",
                chapterNumber = 3,
                isDraft = false,
                isPublished = true,
                createdAt = "2024-01-03",
                publishedAt = "2024-01-03",
                updatedAt = "2024-01-03",
                needsSync = false
            )
        )

        coEvery {
            chapterDao.getChaptersByStorySync(storyId)
        } returns mockChapters


        val result = repository.getNextChapter(storyId, currentChapterNumber)


        assertTrue(result is Resource.Success)
        assertEquals(3, (result as Resource.Success).data?.chapterNumber)
        assertEquals("Chapter 3", result.data?.title)
    }

    @Test
    fun `getPreviousChapter retorna capítulo anterior publicado`() = runTest {

        val storyId = 10
        val currentChapterNumber = 3

        val mockChapters = listOf(
            ChapterEntity(
                chapterId = 1,
                storyId = storyId,
                title = "Chapter 1",
                content = "Content 1",
                chapterNumber = 1,
                isDraft = false,
                isPublished = true,
                createdAt = "2024-01-01",
                publishedAt = "2024-01-01",
                updatedAt = "2024-01-01",
                needsSync = false
            ),
            ChapterEntity(
                chapterId = 2,
                storyId = storyId,
                title = "Chapter 2",
                content = "Content 2",
                chapterNumber = 2,
                isDraft = false,
                isPublished = true,
                createdAt = "2024-01-02",
                publishedAt = "2024-01-02",
                updatedAt = "2024-01-02",
                needsSync = false
            ),
            ChapterEntity(
                chapterId = 3,
                storyId = storyId,
                title = "Chapter 3",
                content = "Content 3",
                chapterNumber = 3,
                isDraft = false,
                isPublished = true,
                createdAt = "2024-01-03",
                publishedAt = "2024-01-03",
                updatedAt = "2024-01-03",
                needsSync = false
            )
        )

        coEvery {
            chapterDao.getChaptersByStorySync(storyId)
        } returns mockChapters


        val result = repository.getPreviousChapter(storyId, currentChapterNumber)


        assertTrue(result is Resource.Success)
        assertNotNull((result as Resource.Success).data)
        assertEquals(2, result.data?.chapterNumber)
        assertEquals("Chapter 2", result.data?.title)
    }

    @Test
    fun `getPreviousChapter retorna null cuando no hay capítulo anterior`() = runTest {

        val storyId = 10
        val currentChapterNumber = 1

        val mockChapters = listOf(
            ChapterEntity(
                chapterId = 1,
                storyId = storyId,
                title = "Chapter 1",
                content = "Content 1",
                chapterNumber = 1,
                isDraft = false,
                isPublished = true,
                createdAt = "2024-01-01",
                publishedAt = "2024-01-01",
                updatedAt = "2024-01-01",
                needsSync = false
            )
        )

        coEvery {
            chapterDao.getChaptersByStorySync(storyId)
        } returns mockChapters


        val result = repository.getPreviousChapter(storyId, currentChapterNumber)


        assertTrue(result is Resource.Success)
        assertNull((result as Resource.Success).data)
    }

    @Test
    fun `getPreviousChapter ignora capítulos en borrador`() = runTest {

        val storyId = 10
        val currentChapterNumber = 3

        val mockChapters = listOf(
            ChapterEntity(
                chapterId = 1,
                storyId = storyId,
                title = "Chapter 1",
                content = "Content 1",
                chapterNumber = 1,
                isDraft = false,
                isPublished = true,
                createdAt = "2024-01-01",
                publishedAt = "2024-01-01",
                updatedAt = "2024-01-01",
                needsSync = false
            ),
            ChapterEntity(
                chapterId = 2,
                storyId = storyId,
                title = "Chapter 2 Draft",
                content = "Content 2",
                chapterNumber = 2,
                isDraft = true,
                isPublished = false,
                createdAt = "2024-01-02",
                publishedAt = null,
                updatedAt = "2024-01-02",
                needsSync = false
            ),
            ChapterEntity(
                chapterId = 3,
                storyId = storyId,
                title = "Chapter 3",
                content = "Content 3",
                chapterNumber = 3,
                isDraft = false,
                isPublished = true,
                createdAt = "2024-01-03",
                publishedAt = "2024-01-03",
                updatedAt = "2024-01-03",
                needsSync = false
            )
        )

        coEvery {
            chapterDao.getChaptersByStorySync(storyId)
        } returns mockChapters


        val result = repository.getPreviousChapter(storyId, currentChapterNumber)


        assertTrue(result is Resource.Success)
        assertEquals(1, (result as Resource.Success).data?.chapterNumber)
        assertEquals("Chapter 1", result.data?.title)
    }

    @Test
    fun `getNextChapter retorna Error cuando hay excepción`() = runTest {

        val storyId = 10
        val currentChapterNumber = 1

        coEvery {
            chapterDao.getChaptersByStorySync(storyId)
        } throws Exception("Database error")


        val result = repository.getNextChapter(storyId, currentChapterNumber)


        assertTrue(result is Resource.Error)
        assertEquals("Database error", (result as Resource.Error).message)
    }

    @Test
    fun `getPreviousChapter retorna Error cuando hay excepción`() = runTest {

        val storyId = 10
        val currentChapterNumber = 2

        coEvery {
            chapterDao.getChaptersByStorySync(storyId)
        } throws Exception("Database error")


        val result = repository.getPreviousChapter(storyId, currentChapterNumber)


        assertTrue(result is Resource.Error)
        assertEquals("Database error", (result as Resource.Error).message)
    }
}