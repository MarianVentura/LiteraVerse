package edu.ucne.literaverse.domain.model

data class StoryWithProgress(
    val story: StoryDetail,
    val progress: ReadingProgress?,
    val isFavorite: Boolean,
    val isReading: Boolean,
    val isCompleted: Boolean,
    val totalChapters: Int
) {
    val progressPercentage: Int
        get() = if (totalChapters > 0 && progress != null) {
            ((progress.chapterId.toFloat() / totalChapters) * 100).toInt()
        } else 0

    val lastReadChapterId: Int?
        get() = progress?.chapterId

    val lastReadChapterTitle: String?
        get() = progress?.chapterTitle

    val lastReadTimeAgo: String
        get() = progress?.lastReadAt?.let { lastReadAtStr ->
            try {
                "Recientemente"
            } catch (e: Exception) {
                "Sin progreso"
            }
        } ?: "Sin progreso"
}