package edu.ucne.literaverse.domain.usecase.libraryUseCases

import edu.ucne.literaverse.data.remote.Resource
import edu.ucne.literaverse.domain.repository.LibraryRepository
import javax.inject.Inject

class IsFavoriteUseCase @Inject constructor(
    private val repository: LibraryRepository
) {
    suspend operator fun invoke(userId: Int, storyId: Int): Resource<Boolean> {
        return repository.isFavorite(userId, storyId)
    }
}