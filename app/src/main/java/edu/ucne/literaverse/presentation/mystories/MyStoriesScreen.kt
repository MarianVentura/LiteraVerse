package edu.ucne.literaverse.presentation.mystories

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
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
import edu.ucne.literaverse.domain.model.StoryDetail

@Composable
fun MyStoriesScreen(
    viewModel: MyStoriesViewModel = hiltViewModel(),
    onNavigateBack: () -> Unit = {},
    onNavigateToStoryDetail: (Int) -> Unit = {}
) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    Scaffold(
        topBar = {
            MyStoriesTopBar(onNavigateBack = onNavigateBack)
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            MyStoriesFilterChips(
                selectedFilter = state.selectedFilter,
                onFilterChanged = { viewModel.onEvent(MyStoriesEvent.OnFilterChanged(it)) },
                allCount = state.stories.size,
                draftsCount = state.stories.count { it.isDraft && !it.isPublished },
                publishedCount = state.stories.count { it.isPublished }
            )

            if (state.isLoading) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            } else if (state.filteredStories.isEmpty()) {
                EmptyStoriesState(filter = state.selectedFilter)
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(state.filteredStories) { story ->
                        StoryListItem(
                            story = story,
                            onStoryClick = { onNavigateToStoryDetail(story.storyId) },
                            onDeleteClick = { viewModel.onEvent(MyStoriesEvent.OnDeleteStory(story.storyId)) }
                        )
                    }
                }
            }
        }

        state.error?.let { error ->
            Snackbar(
                modifier = Modifier.padding(16.dp)
            ) {
                Text(error)
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MyStoriesTopBar(onNavigateBack: () -> Unit) {
    TopAppBar(
        title = {
            Text(
                text = "Mis Historias",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold
            )
        },
        navigationIcon = {
            IconButton(onClick = onNavigateBack) {
                Icon(
                    imageVector = Icons.Default.ArrowBack,
                    contentDescription = "Volver"
                )
            }
        },
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = MaterialTheme.colorScheme.surface
        )
    )
}

@Composable
fun MyStoriesFilterChips(
    selectedFilter: StoryFilter,
    onFilterChanged: (StoryFilter) -> Unit,
    allCount: Int,
    draftsCount: Int,
    publishedCount: Int
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        FilterChip(
            selected = selectedFilter == StoryFilter.ALL,
            onClick = { onFilterChanged(StoryFilter.ALL) },
            label = {
                Text(
                    "Todas ($allCount)",
                    style = MaterialTheme.typography.labelMedium
                )
            }
        )

        FilterChip(
            selected = selectedFilter == StoryFilter.DRAFTS,
            onClick = { onFilterChanged(StoryFilter.DRAFTS) },
            label = {
                Text(
                    "Borradores ($draftsCount)",
                    style = MaterialTheme.typography.labelMedium
                )
            },
            leadingIcon = {
                Icon(
                    imageVector = Icons.Default.Edit,
                    contentDescription = null,
                    modifier = Modifier.size(16.dp)
                )
            }
        )

        FilterChip(
            selected = selectedFilter == StoryFilter.PUBLISHED,
            onClick = { onFilterChanged(StoryFilter.PUBLISHED) },
            label = {
                Text(
                    "Publicadas ($publishedCount)",
                    style = MaterialTheme.typography.labelMedium
                )
            },
            leadingIcon = {
                Icon(
                    imageVector = Icons.Default.Check,
                    contentDescription = null,
                    modifier = Modifier.size(16.dp)
                )
            }
        )
    }
}

@Composable
fun StoryListItem(
    story: StoryDetail,
    onStoryClick: () -> Unit,
    onDeleteClick: () -> Unit
) {
    var showDeleteDialog by remember { mutableStateOf(false) }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onStoryClick() },
        shape = RoundedCornerShape(12.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            if (story.coverImageUrl != null) {
                AsyncImage(
                    model = story.coverImageUrl,
                    contentDescription = story.title,
                    modifier = Modifier
                        .size(100.dp, 140.dp)
                        .clip(RoundedCornerShape(8.dp)),
                    contentScale = ContentScale.Crop
                )
            } else {
                Box(
                    modifier = Modifier
                        .size(100.dp, 140.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .background(MaterialTheme.colorScheme.surfaceVariant),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.MenuBook,
                        contentDescription = null,
                        modifier = Modifier.size(48.dp),
                        tint = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            Column(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxHeight(),
                verticalArrangement = Arrangement.SpaceBetween
            ) {
                Column(
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Text(
                        text = story.title,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis
                    )

                    Text(
                        text = story.synopsis,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis
                    )

                    if (story.genre != null) {
                        AssistChip(
                            onClick = {},
                            label = {
                                Text(
                                    story.genre,
                                    style = MaterialTheme.typography.labelSmall
                                )
                            }
                        )
                    }
                }

                Row(
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(4.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.RemoveRedEye,
                            contentDescription = null,
                            modifier = Modifier.size(16.dp),
                            tint = MaterialTheme.colorScheme.primary
                        )
                        Text(
                            text = "${story.viewCount}",
                            style = MaterialTheme.typography.labelSmall
                        )
                    }

                    Row(
                        horizontalArrangement = Arrangement.spacedBy(4.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.MenuBook,
                            contentDescription = null,
                            modifier = Modifier.size(16.dp),
                            tint = MaterialTheme.colorScheme.primary
                        )
                        Text(
                            text = "${story.chapters.size} caps",
                            style = MaterialTheme.typography.labelSmall
                        )
                    }

                    if (story.isDraft && !story.isPublished) {
                        Badge(
                            containerColor = MaterialTheme.colorScheme.tertiary
                        ) {
                            Text(
                                "Borrador",
                                style = MaterialTheme.typography.labelSmall
                            )
                        }
                    }

                    if (story.isPublished) {
                        Badge(
                            containerColor = MaterialTheme.colorScheme.primary
                        ) {
                            Text(
                                "Publicada",
                                style = MaterialTheme.typography.labelSmall
                            )
                        }
                    }
                }
            }

            IconButton(onClick = { showDeleteDialog = true }) {
                Icon(
                    imageVector = Icons.Default.Delete,
                    contentDescription = "Eliminar",
                    tint = MaterialTheme.colorScheme.error
                )
            }
        }
    }

    if (showDeleteDialog) {
        AlertDialog(
            onDismissRequest = { showDeleteDialog = false },
            title = {
                Text(
                    "Eliminar Historia",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold
                )
            },
            text = {
                Text(
                    "¿Estás seguro de que deseas eliminar \"${story.title}\"? Esta acción no se puede deshacer.",
                    style = MaterialTheme.typography.bodyMedium
                )
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        showDeleteDialog = false
                        onDeleteClick()
                    },
                    colors = ButtonDefaults.textButtonColors(
                        contentColor = MaterialTheme.colorScheme.error
                    )
                ) {
                    Text("Eliminar")
                }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteDialog = false }) {
                    Text("Cancelar")
                }
            }
        )
    }
}

@Composable
fun EmptyStoriesState(filter: StoryFilter) {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp),
            modifier = Modifier.padding(32.dp)
        ) {
            Icon(
                imageVector = when (filter) {
                    StoryFilter.ALL -> Icons.Default.MenuBook
                    StoryFilter.DRAFTS -> Icons.Default.Edit
                    StoryFilter.PUBLISHED -> Icons.Default.Check
                },
                contentDescription = null,
                modifier = Modifier.size(64.dp),
                tint = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.4f)
            )

            Text(
                text = when (filter) {
                    StoryFilter.ALL -> "No tienes historias"
                    StoryFilter.DRAFTS -> "No tienes borradores"
                    StoryFilter.PUBLISHED -> "No tienes historias publicadas"
                },
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
            )

            Text(
                text = when (filter) {
                    StoryFilter.ALL -> "Comienza a escribir tu primera historia"
                    StoryFilter.DRAFTS -> "Tus borradores aparecerán aquí"
                    StoryFilter.PUBLISHED -> "Publica una historia para verla aquí"
                },
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun MyStoriesTopBarPreview() {
    MaterialTheme {
        MyStoriesTopBar(onNavigateBack = {})
    }
}

@Preview(showBackground = true)
@Composable
private fun MyStoriesFilterChipsPreview() {
    MaterialTheme {
        MyStoriesFilterChips(
            selectedFilter = StoryFilter.ALL,
            onFilterChanged = {},
            allCount = 15,
            draftsCount = 5,
            publishedCount = 10
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun StoryListItemPreview() {
    MaterialTheme {
        StoryListItem(
            story = StoryDetail(
                storyId = 1,
                userId = 1,
                userName = "Usuario",
                title = "Mi Primera Historia",
                synopsis = "Esta es una historia increíble sobre...",
                coverImageUrl = null,
                genre = "Fantasía",
                tags = "aventura, magia",
                isDraft = false,
                isPublished = true,
                createdAt = "2024-01-01",
                publishedAt = "2024-01-15",
                updatedAt = "2024-01-20",
                viewCount = 250,
                chapters = emptyList()
            ),
            onStoryClick = {},
            onDeleteClick = {}
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun EmptyStoriesStatePreview() {
    MaterialTheme {
        EmptyStoriesState(filter = StoryFilter.DRAFTS)
    }
}