package edu.ucne.literaverse.data.mappers

import edu.ucne.literaverse.data.local.entities.StoryEntity
import edu.ucne.literaverse.data.remote.dto.CreateStoryRequest
import edu.ucne.literaverse.data.remote.dto.StoryDetailResponse
import edu.ucne.literaverse.data.remote.dto.StoryResponse
import edu.ucne.literaverse.data.remote.dto.UpdateStoryRequest
import edu.ucne.literaverse.domain.model.StoryDetail

fun StoryDetailResponse.toDomain(): StoryDetail = StoryDetail(
    storyId = storyId,
    userId = userId,
    title = title,
    synopsis = synopsis,
    coverImageUrl = coverImageUrl,
    genre = genre,
    tags = tags,
    isDraft = isDraft,
    isPublished = isPublished,
    createdAt = createdAt,
    publishedAt = publishedAt,
    updatedAt = updatedAt,
    viewCount = viewCount,
    chapters = emptyList()
)

fun StoryResponse.toEntity(): StoryEntity = StoryEntity(
    storyId = storyId,
    userId = userId,
    title = title,
    synopsis = synopsis,
    coverImageUrl = coverImageUrl,
    genre = genre,
    tags = tags,
    isDraft = isDraft,
    isPublished = isPublished,
    createdAt = createdAt,
    publishedAt = publishedAt,
    updatedAt = updatedAt,
    viewCount = viewCount,
    needsSync = false
)

fun StoryEntity.toDomain(): StoryDetail = StoryDetail(
    storyId = storyId,
    userId = userId,
    title = title,
    synopsis = synopsis,
    coverImageUrl = coverImageUrl,
    genre = genre,
    tags = tags,
    isDraft = isDraft,
    isPublished = isPublished,
    createdAt = createdAt,
    publishedAt = publishedAt,
    updatedAt = updatedAt,
    viewCount = viewCount,
    chapters = emptyList()
)

fun StoryDetail.toEntity(): StoryEntity = StoryEntity(
    storyId = storyId,
    userId = userId,
    title = title,
    synopsis = synopsis,
    coverImageUrl = coverImageUrl,
    genre = genre,
    tags = tags,
    isDraft = isDraft,
    isPublished = isPublished,
    createdAt = createdAt,
    publishedAt = publishedAt,
    updatedAt = updatedAt,
    viewCount = viewCount,
    needsSync = false
)

fun StoryDetail.toCreateRequest(): CreateStoryRequest = CreateStoryRequest(
    userId = userId,
    title = title,
    synopsis = synopsis,
    genre = genre ?: "",
    tags = tags
)

fun StoryDetail.toUpdateRequest(): UpdateStoryRequest = UpdateStoryRequest(
    title = title,
    synopsis = synopsis,
    genre = genre ?: "",
    tags = tags
)