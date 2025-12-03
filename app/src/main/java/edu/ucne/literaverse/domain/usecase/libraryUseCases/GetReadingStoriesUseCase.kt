package edu.ucne.literaverse.domain.usecase.libraryUseCases

import edu.ucne.literaverse.domain.model.StoryWithProgress
import edu.ucne.literaverse.domain.repository.LibraryRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetReadingStoriesUseCase @Inject constructor(
    private val repository: LibraryRepository
) {
    operator fun invoke(userId: Int): Flow<List<StoryWithProgress>> {
        return repository.getReadingStories(userId)
    }
}