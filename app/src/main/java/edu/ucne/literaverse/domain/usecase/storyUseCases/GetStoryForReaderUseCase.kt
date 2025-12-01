package edu.ucne.literaverse.domain.usecase.storyUseCases

import edu.ucne.literaverse.data.remote.Resource
import edu.ucne.literaverse.domain.model.StoryReader
import edu.ucne.literaverse.domain.repository.StoryRepository
import javax.inject.Inject

class GetStoryForReaderUseCase @Inject constructor(
    private val repository: StoryRepository
) {
    suspend operator fun invoke(storyId: Int): Resource<StoryReader> {
        return repository.getStoryForReader(storyId)
    }
}