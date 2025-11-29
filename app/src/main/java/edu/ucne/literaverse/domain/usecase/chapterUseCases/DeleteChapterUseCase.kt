package edu.ucne.literaverse.domain.usecase.chapterUseCases

import edu.ucne.literaverse.data.remote.Resource
import edu.ucne.literaverse.domain.repository.ChapterRepository
import javax.inject.Inject

class DeleteChapterUseCase @Inject constructor(
    private val repository: ChapterRepository
) {
    suspend operator fun invoke(storyId: Int, chapterId: Int): Resource<Unit> {
        return repository.deleteChapter(storyId, chapterId)
    }
}