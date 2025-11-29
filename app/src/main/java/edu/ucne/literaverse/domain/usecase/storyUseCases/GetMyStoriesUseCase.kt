package edu.ucne.literaverse.domain.usecase.storyUseCases

import edu.ucne.literaverse.domain.model.StoryDetail
import edu.ucne.literaverse.domain.repository.StoryRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetMyStoriesUseCase @Inject constructor(
    private val repository: StoryRepository
) {
    suspend operator fun invoke(userId: Int): Flow<List<StoryDetail>> {
        return repository.getStoriesByUser(userId)
    }
}