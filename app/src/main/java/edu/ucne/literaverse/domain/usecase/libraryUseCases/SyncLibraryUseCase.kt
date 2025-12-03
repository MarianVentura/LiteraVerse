package edu.ucne.literaverse.domain.usecase.libraryUseCases

import edu.ucne.literaverse.domain.repository.LibraryRepository
import javax.inject.Inject

class SyncLibraryUseCase @Inject constructor(
    private val repository: LibraryRepository
) {
    suspend operator fun invoke(userId: Int) {
        repository.syncLibrary(userId)
    }
}