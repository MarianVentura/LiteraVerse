package edu.ucne.literaverse.data.mappers

import edu.ucne.literaverse.data.local.entities.ChapterEntity
import edu.ucne.literaverse.data.remote.dto.ChapterResponse
import edu.ucne.literaverse.data.remote.dto.CreateChapterRequest
import edu.ucne.literaverse.data.remote.dto.UpdateChapterRequest
import edu.ucne.literaverse.domain.model.Chapter

fun ChapterResponse.toDomain(): Chapter = Chapter(
    chapterId = chapterId,
    storyId = storyId,
    title = title,
    content = content,
    chapterNumber = chapterNumber,
    isDraft = isDraft,
    isPublished = isPublished,
    createdAt = createdAt,
    publishedAt = publishedAt,
    updatedAt = updatedAt
)

fun ChapterResponse.toEntity(): ChapterEntity = ChapterEntity(
    chapterId = chapterId,
    storyId = storyId,
    title = title,
    content = content,
    chapterNumber = chapterNumber,
    isDraft = isDraft,
    isPublished = isPublished,
    createdAt = createdAt,
    publishedAt = publishedAt,
    updatedAt = updatedAt,
    needsSync = false
)

fun ChapterEntity.toDomain(): Chapter = Chapter(
    chapterId = chapterId,
    storyId = storyId,
    title = title,
    content = content,
    chapterNumber = chapterNumber,
    isDraft = isDraft,
    isPublished = isPublished,
    createdAt = createdAt,
    publishedAt = publishedAt,
    updatedAt = updatedAt
)

fun Chapter.toEntity(): ChapterEntity = ChapterEntity(
    chapterId = chapterId,
    storyId = storyId,
    title = title,
    content = content,
    chapterNumber = chapterNumber,
    isDraft = isDraft,
    isPublished = isPublished,
    createdAt = createdAt,
    publishedAt = publishedAt,
    updatedAt = updatedAt,
    needsSync = false
)

fun Chapter.toCreateRequest(isDraft: Boolean): CreateChapterRequest = CreateChapterRequest(
    title = title,
    content = content,
    chapterNumber = chapterNumber,
    isDraft = isDraft
)

fun Chapter.toUpdateRequest(isDraft: Boolean): UpdateChapterRequest = UpdateChapterRequest(
    title = title,
    content = content,
    isDraft = isDraft
)