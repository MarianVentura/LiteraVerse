package edu.ucne.literaverse.data.mappers

import edu.ucne.literaverse.data.remote.dto.StoryDetailResponse
import edu.ucne.literaverse.data.remote.dto.ChapterResponse
import edu.ucne.literaverse.domain.model.StoryReader
import edu.ucne.literaverse.domain.model.Chapter

fun StoryDetailResponse.toStoryReader(chapters: List<ChapterResponse>): StoryReader {
    return StoryReader(
        storyId = storyId,
        title = title,
        synopsis = synopsis,
        coverImageUrl = coverImageUrl,
        author = "Usuario $userId",
        authorId = userId,
        genre = genre,
        tags = tags,
        viewCount = viewCount,
        likeCount = 0,
        publishedChapters = chapters
            .filter { it.isPublished && !it.isDraft }
            .map { it.toDomain() }
    )
}