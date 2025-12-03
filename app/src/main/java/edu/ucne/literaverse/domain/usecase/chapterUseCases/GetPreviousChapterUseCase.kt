package edu.ucne.literaverse.domain.usecase.chapterUseCases

import edu.ucne.literaverse.data.remote.Resource
import edu.ucne.literaverse.domain.model.Chapter
import edu.ucne.literaverse.domain.repository.ChapterRepository
import javax.inject.Inject

class GetPreviousChapterUseCase @Inject constructor(
    private val repository: ChapterRepository
) {
    suspend operator fun invoke(storyId: Int, currentChapterNumber: Int): Resource<Chapter?> {
        if (storyId <= 0) {
            return Resource.Error("ID de historia inválido")
        }
        if (currentChapterNumber <= 0) {
            return Resource.Error("Número de capítulo inválido")
        }

        return repository.getPreviousChapter(storyId, currentChapterNumber)
    }
}