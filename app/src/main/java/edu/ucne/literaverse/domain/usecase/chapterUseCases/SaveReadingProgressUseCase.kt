package edu.ucne.literaverse.domain.usecase.chapterUseCases

import edu.ucne.literaverse.data.remote.Resource
import edu.ucne.literaverse.domain.repository.LibraryRepository
import javax.inject.Inject

class SaveReadingProgressUseCase @Inject constructor(
    private val repository: LibraryRepository
) {
    suspend operator fun invoke(
        userId: Int,
        storyId: Int,
        chapterId: Int,
        scrollPosition: Double
    ): Resource<Unit> {
        if (userId <= 0) {
            return Resource.Error("Usuario no autenticado")
        }
        if (storyId <= 0) {
            return Resource.Error("ID de historia inválido")
        }
        if (chapterId <= 0) {
            return Resource.Error("ID de capítulo inválido")
        }

        return repository.saveReadingProgress(userId, storyId, chapterId, scrollPosition)
    }
}