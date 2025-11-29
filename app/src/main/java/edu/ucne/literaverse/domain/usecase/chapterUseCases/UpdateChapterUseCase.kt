package edu.ucne.literaverse.domain.usecase.chapterUseCases

import edu.ucne.literaverse.data.remote.Resource
import edu.ucne.literaverse.domain.model.Chapter
import edu.ucne.literaverse.domain.repository.ChapterRepository
import javax.inject.Inject

class UpdateChapterUseCase @Inject constructor(
    private val repository: ChapterRepository
) {
    suspend operator fun invoke(
        storyId: Int,
        chapterId: Int,
        title: String,
        content: String,
        isDraft: Boolean = true
    ): Resource<Chapter> {
        if (title.isBlank()) {
            return Resource.Error("El título del capítulo es obligatorio")
        }
        if (content.isBlank()) {
            return Resource.Error("El contenido del capítulo es obligatorio")
        }

        return repository.updateChapter(storyId, chapterId, title, content, isDraft)
    }
}