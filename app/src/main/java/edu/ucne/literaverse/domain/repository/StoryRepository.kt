package edu.ucne.literaverse.domain.repository

import edu.ucne.literaverse.data.remote.Resource
import edu.ucne.literaverse.domain.model.StoryDetail
import kotlinx.coroutines.flow.Flow

interface StoryRepository {
    suspend fun createStory(userId: Int, title: String, synopsis: String, genre: String, tags: String?): Resource<StoryDetail>
    suspend fun getStoriesByUser(userId: Int): Flow<List<StoryDetail>>
    suspend fun getStoryById(storyId: Int): Resource<StoryDetail>
    suspend fun updateStory(storyId: Int, title: String, synopsis: String, genre: String, tags: String?): Resource<StoryDetail>
    suspend fun deleteStory(storyId: Int): Resource<Unit>
    suspend fun publishStory(storyId: Int): Resource<Unit>
    suspend fun unpublishStory(storyId: Int): Resource<Unit>
    suspend fun syncStories()
}