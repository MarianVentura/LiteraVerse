package edu.ucne.literaverse.domain.usecase.libraryUseCases

import edu.ucne.literaverse.data.remote.Resource
import edu.ucne.literaverse.domain.repository.LibraryRepository
import javax.inject.Inject

class UpdateReadingStatusUseCase @Inject constructor(
    private val repository: LibraryRepository
) {
    suspend operator fun invoke(userId: Int, storyId: Int, isReading: Boolean): Resource<Unit> {
        return repository.updateReadingStatus(userId, storyId, isReading)
    }
}