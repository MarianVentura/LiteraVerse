package edu.ucne.literaverse.domain.usecase.chapterUseCases

import edu.ucne.literaverse.data.remote.Resource
import edu.ucne.literaverse.domain.model.Chapter
import edu.ucne.literaverse.domain.repository.ChapterRepository
import javax.inject.Inject

class GetChapterUseCase @Inject constructor(
    private val repository: ChapterRepository
) {
    suspend operator fun invoke(storyId: Int, chapterId: Int): Resource<Chapter> {
        return repository.getChapterById(storyId, chapterId)
    }
}