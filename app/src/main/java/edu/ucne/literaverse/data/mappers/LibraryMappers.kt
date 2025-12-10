package edu.ucne.literaverse.data.mappers

import edu.ucne.literaverse.data.local.entities.StoryEntity
import edu.ucne.literaverse.data.remote.dto.ReadingProgressRequest
import edu.ucne.literaverse.data.remote.dto.ReadingProgressResponse
import edu.ucne.literaverse.domain.model.ReadingProgress
import edu.ucne.literaverse.domain.model.StoryWithProgress

fun ReadingProgressResponse.toDomain(): ReadingProgress = ReadingProgress(
    progressId = progressId,
    userId = userId,
    storyId = storyId,
    chapterId = chapterId,
    scrollPosition = scrollPosition,
    lastReadAt = lastReadAt,
    storyTitle = storyTitle,
    chapterTitle = chapterTitle
)

fun ReadingProgress.toRequest(): ReadingProgressRequest = ReadingProgressRequest(
    userId = userId,
    storyId = storyId,
    chapterId = chapterId,
    scrollPosition = scrollPosition
)

fun StoryEntity.applyReadingProgress(progress: ReadingProgress): StoryEntity = copy(
    lastReadChapterId = progress.chapterId,
    scrollPosition = progress.scrollPosition,
    lastReadAt = System.currentTimeMillis(),
    isReading = true
)

fun StoryEntity.toStoryWithProgress(
    progressList: List<ReadingProgress>,
    totalChapters: Int = 0
): StoryWithProgress {
    val progress = progressList.find { it.storyId == this.storyId }
    return StoryWithProgress(
        story = this.toDomain(),
        progress = progress,
        isFavorite = this.isFavorite,
        isReading = this.isReading,
        isCompleted = this.isCompleted,
        totalChapters = totalChapters
    )
}

fun List<StoryEntity>.toStoryWithProgressList(
    progressList: List<ReadingProgress>,
    chaptersCount: Map<Int, Int> = emptyMap()
): List<StoryWithProgress> {
    return this.map { entity ->
        entity.toStoryWithProgress(
            progressList = progressList,
            totalChapters = chaptersCount[entity.storyId] ?: 0
        )
    }
}