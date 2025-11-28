package edu.ucne.literaverse.data.mappers

import edu.ucne.literaverse.data.remote.dto.StoryResponse
import edu.ucne.literaverse.domain.model.Story

fun StoryResponse.toDomain(): Story = Story(
    storyId = storyId,
    title = title,
    author = "Usuario $userId",
    description = synopsis,
    coverImageUrl = coverImageUrl,
    genres = genre?.split(",")?.map { it.trim() } ?: emptyList(),
    reads = viewCount,
    chapters = 0,
    status = when {
        isPublished -> "Publicado"
        isDraft -> "Borrador"
        else -> "Desconocido"
    }
)