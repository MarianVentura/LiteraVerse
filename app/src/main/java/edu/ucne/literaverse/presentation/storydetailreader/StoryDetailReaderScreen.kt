package edu.ucne.literaverse.presentation.storydetailreader

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.AsyncImage
import edu.ucne.literaverse.domain.model.Chapter
import edu.ucne.literaverse.domain.model.StoryReader
import edu.ucne.literaverse.presentation.components.AddToLibraryMenu
import edu.ucne.literaverse.presentation.components.LibraryStates

@Composable
fun StoryDetailReaderScreen(
    viewModel: StoryDetailReaderViewModel = hiltViewModel(),
    onNavigateBack: () -> Unit = {},
    onNavigateToChapter: (Int, Int) -> Unit = { _, _ -> }
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(state.userMessage) {
        state.userMessage?.let { message ->
            snackbarHostState.showSnackbar(message)
            viewModel.onEvent(StoryDetailReaderEvent.UserMessageShown)
        }
    }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { padding ->
        StoryDetailContent(
            state = state,
            padding = padding,
            onEvent = viewModel::onEvent,
            onNavigateBack = onNavigateBack,
            onNavigateToChapter = onNavigateToChapter
        )
    }

    if (state.showLibraryMenu) {
        LibraryMenuDialog(
            isFavorite = state.isFavorite,
            isReading = state.isReading,
            isCompleted = state.isCompleted,
            onDismiss = { viewModel.onEvent(StoryDetailReaderEvent.DismissLibraryMenu) },
            onSave = { states ->
                viewModel.onEvent(StoryDetailReaderEvent.UpdateLibraryStates(states))
            }
        )
    }
}

@Composable
private fun StoryDetailContent(
    state: StoryDetailReaderUiState,
    padding: PaddingValues,
    onEvent: (StoryDetailReaderEvent) -> Unit,
    onNavigateBack: () -> Unit,
    onNavigateToChapter: (Int, Int) -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(padding)
    ) {
        when {
            state.isLoading -> LoadingState()
            state.error != null -> ErrorState(error = state.error)
            state.story != null -> StoryDetailReaderContent(
                story = state.story,
                isFavorite = state.isFavorite,
                isInLibrary = state.isInLibrary,
                onEvent = onEvent,
                onNavigateBack = onNavigateBack,
                onNavigateToChapter = onNavigateToChapter
            )
        }
    }
}

@Composable
private fun LoadingState() {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        CircularProgressIndicator()
    }
}

@Composable
private fun ErrorState(error: String) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Icon(
            imageVector = Icons.Default.Error,
            contentDescription = null,
            modifier = Modifier.size(64.dp),
            tint = MaterialTheme.colorScheme.error
        )
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = error,
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.error
        )
    }
}

@Composable
private fun LibraryMenuDialog(
    isFavorite: Boolean,
    isReading: Boolean,
    isCompleted: Boolean,
    onDismiss: () -> Unit,
    onSave: (LibraryStates) -> Unit
) {
    AddToLibraryMenu(
        currentStates = LibraryStates(
            isFavorite = isFavorite,
            isReading = isReading,
            isCompleted = isCompleted
        ),
        onDismiss = onDismiss,
        onSave = onSave
    )
}

@Composable
fun StoryDetailReaderContent(
    story: StoryReader,
    isFavorite: Boolean,
    isInLibrary: Boolean,
    onEvent: (StoryDetailReaderEvent) -> Unit,
    onNavigateBack: () -> Unit,
    onNavigateToChapter: (Int, Int) -> Unit
) {
    LazyColumn(
        modifier = Modifier.fillMaxSize()
    ) {
        item {
            StoryHeroSection(
                story = story,
                onNavigateBack = onNavigateBack
            )
        }

        item {
            StoryInfoSection(
                story = story,
                isFavorite = isFavorite,
                isInLibrary = isInLibrary,
                onStartReading = {
                    onEvent(StoryDetailReaderEvent.OnStartReading(onNavigateToChapter))
                },
                onToggleFavorite = { onEvent(StoryDetailReaderEvent.OnToggleFavorite) },
                onAddToLibrary = { onEvent(StoryDetailReaderEvent.ShowLibraryMenu) }
            )
        }

        item {
            SynopsisSection(synopsis = story.synopsis)
        }

        item {
            StatsSection(
                viewCount = story.viewCount,
                chapterCount = story.publishedChapters.size
            )
        }

        item {
            Text(
                text = "Capítulos",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 16.dp)
            )
        }

        items(story.publishedChapters) { chapter ->
            ChapterItem(
                chapter = chapter,
                onClick = { onNavigateToChapter(story.storyId, chapter.chapterId) }
            )
        }

        item {
            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}

@Composable
fun StoryHeroSection(
    story: StoryReader,
    onNavigateBack: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(400.dp)
    ) {
        StoryCoverImage(coverUrl = story.coverImageUrl)

        StoryHeroGradientOverlay()

        StoryHeroBackButton(onNavigateBack = onNavigateBack)
    }
}

@Composable
private fun StoryCoverImage(coverUrl: String?) {
    if (coverUrl != null) {
        AsyncImage(
            model = coverUrl,
            contentDescription = null,
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop
        )
    } else {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.surfaceVariant),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Default.MenuBook,
                contentDescription = null,
                modifier = Modifier.size(120.dp),
                tint = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
private fun StoryHeroGradientOverlay() {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                brush = Brush.verticalGradient(
                    colors = listOf(
                        MaterialTheme.colorScheme.scrim.copy(alpha = 0.3f),
                        MaterialTheme.colorScheme.scrim.copy(alpha = 0.7f)
                    ),
                    startY = 0f,
                    endY = 1200f
                )
            )
    )
}

@Composable
private fun StoryHeroBackButton(onNavigateBack: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
            .statusBarsPadding(),
        horizontalArrangement = Arrangement.Start
    ) {
        IconButton(
            onClick = onNavigateBack,
            modifier = Modifier
                .clip(CircleShape)
                .background(MaterialTheme.colorScheme.surface.copy(alpha = 0.7f))
        ) {
            Icon(
                imageVector = Icons.Default.ArrowBack,
                contentDescription = "Volver",
                tint = MaterialTheme.colorScheme.onSurface
            )
        }
    }
}

@Composable
fun StoryInfoSection(
    story: StoryReader,
    isFavorite: Boolean,
    isInLibrary: Boolean,
    onStartReading: () -> Unit,
    onToggleFavorite: () -> Unit,
    onAddToLibrary: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        StoryGenreAndTagsSection(
            genre = story.genre,
            tags = story.tags
        )

        Text(
            text = story.title,
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold
        )

        Text(
            text = "por ${story.author}",
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
        )

        StoryMetadataRow(
            viewCount = story.viewCount,
            chapterCount = story.publishedChapters.size
        )

        StoryActionButtons(
            isFavorite = isFavorite,
            isInLibrary = isInLibrary,
            onStartReading = onStartReading,
            onToggleFavorite = onToggleFavorite,
            onAddToLibrary = onAddToLibrary
        )
    }
}

@Composable
private fun StoryGenreAndTagsSection(
    genre: String?,
    tags: String?
) {
    if (!genre.isNullOrBlank() || !tags.isNullOrBlank()) {
        LazyRow(
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            genre?.let {
                item {
                    GenreChip(genre = it)
                }
            }

            tags?.split(",")?.take(3)?.forEach { tag ->
                item {
                    TagChip(tag = tag.trim())
                }
            }
        }
    }
}

@Composable
private fun GenreChip(genre: String) {
    AssistChip(
        onClick = {},
        label = { Text(genre) },
        colors = AssistChipDefaults.assistChipColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer,
            labelColor = MaterialTheme.colorScheme.onPrimaryContainer
        )
    )
}

@Composable
private fun TagChip(tag: String) {
    AssistChip(
        onClick = {},
        label = { Text(tag) },
        colors = AssistChipDefaults.assistChipColors(
            containerColor = MaterialTheme.colorScheme.secondaryContainer,
            labelColor = MaterialTheme.colorScheme.onSecondaryContainer
        )
    )
}

@Composable
private fun StoryMetadataRow(
    viewCount: Int,
    chapterCount: Int
) {
    Row(
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = Icons.Default.RemoveRedEye,
            contentDescription = null,
            modifier = Modifier.size(20.dp),
            tint = MaterialTheme.colorScheme.primary
        )
        Text(
            text = "$viewCount",
            style = MaterialTheme.typography.bodyMedium
        )

        Spacer(modifier = Modifier.width(8.dp))

        Icon(
            imageVector = Icons.Default.MenuBook,
            contentDescription = null,
            modifier = Modifier.size(20.dp),
            tint = MaterialTheme.colorScheme.primary
        )
        Text(
            text = "$chapterCount capítulos",
            style = MaterialTheme.typography.bodyMedium
        )
    }
}

@Composable
private fun StoryActionButtons(
    isFavorite: Boolean,
    isInLibrary: Boolean,
    onStartReading: () -> Unit,
    onToggleFavorite: () -> Unit,
    onAddToLibrary: () -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        StartReadingButton(
            onStartReading = onStartReading,
            modifier = Modifier.weight(1f)
        )

        FavoriteButton(
            isFavorite = isFavorite,
            onToggleFavorite = onToggleFavorite
        )

        LibraryButton(
            isInLibrary = isInLibrary,
            onAddToLibrary = onAddToLibrary
        )
    }
}

@Composable
private fun StartReadingButton(
    onStartReading: () -> Unit,
    modifier: Modifier = Modifier
) {
    Button(
        onClick = onStartReading,
        modifier = modifier,
        colors = ButtonDefaults.buttonColors(
            containerColor = MaterialTheme.colorScheme.primary
        ),
        shape = RoundedCornerShape(12.dp)
    ) {
        Text(
            text = "Comenzar a Leer",
            color = MaterialTheme.colorScheme.onPrimary,
            fontWeight = FontWeight.Bold
        )
    }
}

@Composable
private fun FavoriteButton(
    isFavorite: Boolean,
    onToggleFavorite: () -> Unit
) {
    IconButton(
        onClick = onToggleFavorite,
        modifier = Modifier
            .size(56.dp)
            .clip(RoundedCornerShape(12.dp))
            .background(
                if (isFavorite) MaterialTheme.colorScheme.primaryContainer
                else MaterialTheme.colorScheme.surfaceVariant
            )
    ) {
        Icon(
            imageVector = if (isFavorite) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
            contentDescription = "Favorito",
            tint = if (isFavorite) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

@Composable
private fun LibraryButton(
    isInLibrary: Boolean,
    onAddToLibrary: () -> Unit
) {
    IconButton(
        onClick = onAddToLibrary,
        modifier = Modifier
            .size(56.dp)
            .clip(RoundedCornerShape(12.dp))
            .background(
                if (isInLibrary) MaterialTheme.colorScheme.primaryContainer
                else MaterialTheme.colorScheme.surfaceVariant
            )
    ) {
        Icon(
            imageVector = if (isInLibrary) Icons.Default.LibraryBooks else Icons.Default.LibraryAdd,
            contentDescription = "Biblioteca",
            tint = if (isInLibrary) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

@Composable
fun SynopsisSection(synopsis: String) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Text(
            text = "Sinopsis",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold
        )

        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surfaceVariant
            ),
            shape = RoundedCornerShape(12.dp)
        ) {
            Text(
                text = synopsis,
                style = MaterialTheme.typography.bodyMedium,
                modifier = Modifier.padding(16.dp),
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
fun StatsSection(
    viewCount: Int,
    chapterCount: Int
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp),
        horizontalArrangement = Arrangement.SpaceEvenly
    ) {
        StatItem(
            icon = Icons.Default.RemoveRedEye,
            value = "$viewCount",
            label = "Vistas"
        )

        StatItem(
            icon = Icons.Default.MenuBook,
            value = "$chapterCount",
            label = "Capítulos"
        )
    }
}

@Composable
private fun StatItem(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    value: String,
    label: String
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.primary,
            modifier = Modifier.size(24.dp)
        )
        Text(
            text = value,
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold
        )
        Text(
            text = label,
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
        )
    }
}

@Composable
fun ChapterItem(
    chapter: Chapter,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 4.dp)
            .clickable { onClick() },
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Text(
                    text = chapter.title,
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )

                chapter.updatedAt?.let { updatedAt ->
                    Text(
                        text = updatedAt,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f)
                    )
                }
            }

            Icon(
                imageVector = Icons.Default.ChevronRight,
                contentDescription = "Leer capítulo",
                tint = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}


private fun formatNumber(number: Int): String {
    return when {
        number >= 1_000_000 -> "${number / 1_000_000}M"
        number >= 1_000 -> "${number / 1_000}k"
        else -> number.toString()
    }
}

@Preview(showBackground = true)
@Composable
private fun ChapterItemPreview() {
    MaterialTheme {
        ChapterItem(
            chapter = Chapter(
                chapterId = 1,
                storyId = 1,
                title = "El Comienzo",
                content = "Contenido...",
                chapterNumber = 1,
                isDraft = false,
                isPublished = true,
                createdAt = String(),
                updatedAt = String(),
                publishedAt = "2024-01-15T10:00:00"
            ),
            onClick = {}
        )
    }
}