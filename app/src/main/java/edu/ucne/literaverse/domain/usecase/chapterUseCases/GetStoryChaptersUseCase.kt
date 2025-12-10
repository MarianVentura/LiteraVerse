package edu.ucne.literaverse.domain.usecase.chapterUseCases

import edu.ucne.literaverse.domain.model.Chapter
import edu.ucne.literaverse.domain.repository.ChapterRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetStoryChaptersUseCase @Inject constructor(
    private val repository: ChapterRepository
) {
     operator fun invoke(storyId: Int): Flow<List<Chapter>> {
        return repository.getChaptersByStory(storyId)
    }
}