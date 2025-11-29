package edu.ucne.literaverse.domain.usecase.storyUseCases

import edu.ucne.literaverse.data.remote.Resource
import edu.ucne.literaverse.domain.model.StoryDetail
import edu.ucne.literaverse.domain.repository.StoryRepository
import javax.inject.Inject

class GetStoryDetailUseCase @Inject constructor(
    private val repository: StoryRepository
) {
    suspend operator fun invoke(storyId: Int): Resource<StoryDetail> {
        return repository.getStoryById(storyId)
    }
}