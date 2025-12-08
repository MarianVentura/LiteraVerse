package edu.ucne.literaverse.presentation.library

import androidx.compose.foundation.background
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.MenuBook
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.AsyncImage
import edu.ucne.literaverse.domain.model.ReadingProgress
import edu.ucne.literaverse.domain.model.StoryDetail
import edu.ucne.literaverse.domain.model.StoryWithProgress
import edu.ucne.literaverse.presentation.components.AddToLibraryMenu
import edu.ucne.literaverse.presentation.components.BottomNavScreen
import edu.ucne.literaverse.presentation.components.LibraryStates
import edu.ucne.literaverse.presentation.components.UserMenuBottomBar
import edu.ucne.literaverse.ui.theme.LiteraVerseTheme

@Composable
fun LibraryScreen(
    viewModel: LibraryViewModel = hiltViewModel(),
    onStoryClick: (Int, Int?) -> Unit,
    onNavigateToHome: () -> Unit,
    onNavigateToBuscar: () -> Unit,
    onNavigateToWrite: () -> Unit,
    onNavigateToPerfil: () -> Unit,
    onLogout: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val snackbarHostState = remember { SnackbarHostState() }

    HandleSnackbarMessages(
        errorMessage = uiState.errorMessage,
        successMessage = uiState.successMessage,
        snackbarHostState = snackbarHostState,
        onMessageShown = { viewModel.onEvent(LibraryEvent.UserMessageShown) }
    )

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        bottomBar = {
            UserMenuBottomBar(
                currentScreen = BottomNavScreen.LIBRARY,
                onNavigateToHome = onNavigateToHome,
                onNavigateToBuscar = onNavigateToBuscar,
                onNavigateToLibrary = {},
                onNavigateToWrite = onNavigateToWrite,
                onNavigateToPerfil = onNavigateToPerfil,
                onLogout = onLogout
            )
        }
    ) { paddingValues ->
        LibraryContent(
            uiState = uiState,
            onEvent = viewModel::onEvent,
            onStoryClick = onStoryClick,
            onNavigateToHome = onNavigateToHome,
            modifier = Modifier.padding(paddingValues)
        )
    }

    LibraryDialogs(
        uiState = uiState,
        onEvent = viewModel::onEvent
    )
}

@Composable
private fun HandleSnackbarMessages(
    errorMessage: String?,
    successMessage: String?,
    snackbarHostState: SnackbarHostState,
    onMessageShown: () -> Unit
) {
    LaunchedEffect(errorMessage) {
        errorMessage?.let {
            snackbarHostState.showSnackbar(it)
            onMessageShown()
        }
    }

    LaunchedEffect(successMessage) {
        successMessage?.let {
            snackbarHostState.showSnackbar(it)
            onMessageShown()
        }
    }
}

@Composable
private fun LibraryContent(
    uiState: LibraryUiState,
    onEvent: (LibraryEvent) -> Unit,
    onStoryClick: (Int, Int?) -> Unit,
    onNavigateToHome: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.fillMaxSize()
    ) {
        Text(
            text = "Mi Biblioteca",
            style = MaterialTheme.typography.headlineMedium,
            color = MaterialTheme.colorScheme.onBackground,
            modifier = Modifier.padding(16.dp)
        )

        LibraryTabs(
            selectedTab = uiState.selectedTab,
            onTabSelected = { onEvent(LibraryEvent.SelectTab(it)) }
        )

        LibraryTabContent(
            uiState = uiState,
            onEvent = onEvent,
            onStoryClick = onStoryClick,
            onNavigateToHome = onNavigateToHome
        )
    }
}

@Composable
private fun LibraryTabs(
    selectedTab: LibraryTab,
    onTabSelected: (LibraryTab) -> Unit
) {
    TabRow(
        selectedTabIndex = selectedTab.ordinal,
        containerColor = MaterialTheme.colorScheme.surface
    ) {
        Tab(
            selected = selectedTab == LibraryTab.FAVORITES,
            onClick = { onTabSelected(LibraryTab.FAVORITES) },
            text = {
                Text(
                    text = "Favoritos",
                    style = MaterialTheme.typography.labelLarge
                )
            }
        )
        Tab(
            selected = selectedTab == LibraryTab.READING,
            onClick = { onTabSelected(LibraryTab.READING) },
            text = {
                Text(
                    text = "Leyendo",
                    style = MaterialTheme.typography.labelLarge
                )
            }
        )
        Tab(
            selected = selectedTab == LibraryTab.COMPLETED,
            onClick = { onTabSelected(LibraryTab.COMPLETED) },
            text = {
                Text(
                    text = "Finalizadas",
                    style = MaterialTheme.typography.labelLarge
                )
            }
        )
    }
}

@Composable
private fun LibraryTabContent(
    uiState: LibraryUiState,
    onEvent: (LibraryEvent) -> Unit,
    onStoryClick: (Int, Int?) -> Unit,
    onNavigateToHome: () -> Unit
) {
    when {
        uiState.isLoading -> LoadingState()
        else -> {
            val currentList = getCurrentList(uiState)

            if (currentList.isEmpty()) {
                EmptyLibraryState(
                    tab = uiState.selectedTab,
                    onExploreClick = onNavigateToHome
                )
            } else {
                StoriesListContent(
                    stories = currentList,
                    onStoryClick = onStoryClick,
                    onEvent = onEvent
                )
            }
        }
    }
}

private fun getCurrentList(uiState: LibraryUiState): List<StoryWithProgress> {
    return when (uiState.selectedTab) {
        LibraryTab.FAVORITES -> uiState.favorites
        LibraryTab.READING -> uiState.reading
        LibraryTab.COMPLETED -> uiState.completed
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
private fun StoriesListContent(
    stories: List<StoryWithProgress>,
    onStoryClick: (Int, Int?) -> Unit,
    onEvent: (LibraryEvent) -> Unit
) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(12.dp),
        contentPadding = androidx.compose.foundation.layout.PaddingValues(16.dp)
    ) {
        items(stories) { storyWithProgress ->
            StoryLibraryCard(
                storyWithProgress = storyWithProgress,
                onClick = {
                    onStoryClick(
                        storyWithProgress.story.storyId,
                        storyWithProgress.lastReadChapterId
                    )
                },
                onLongClick = {
                    onEvent(
                        LibraryEvent.ShowContextMenu(
                            storyId = storyWithProgress.story.storyId,
                            currentStates = LibraryStates(
                                isFavorite = storyWithProgress.isFavorite,
                                isReading = storyWithProgress.isReading,
                                isCompleted = storyWithProgress.isCompleted
                            )
                        )
                    )
                },
                onMenuClick = {
                    onEvent(
                        LibraryEvent.ShowLibraryMenu(
                            storyId = storyWithProgress.story.storyId,
                            currentStates = LibraryStates(
                                isFavorite = storyWithProgress.isFavorite,
                                isReading = storyWithProgress.isReading,
                                isCompleted = storyWithProgress.isCompleted
                            )
                        )
                    )
                }
            )
        }
    }
}

@Composable
private fun LibraryDialogs(
    uiState: LibraryUiState,
    onEvent: (LibraryEvent) -> Unit
) {
    uiState.selectedStoryStates?.let { selectedStates ->
        if (uiState.showLibraryMenu) {
            AddToLibraryMenu(
                currentStates = selectedStates,
                onDismiss = { onEvent(LibraryEvent.DismissLibraryMenu) },
                onSave = { states ->
                    uiState.selectedStoryId?.let { storyId ->
                        onEvent(LibraryEvent.UpdateLibraryStates(storyId, states))
                    }
                }
            )
        }

        if (uiState.showContextMenu) {
            ContextMenuDialog(
                selectedStates = selectedStates,
                onDismiss = { onEvent(LibraryEvent.DismissContextMenu) },
                onEditStates = {
                    uiState.selectedStoryId?.let { storyId ->
                        onEvent(
                            LibraryEvent.ShowLibraryMenu(
                                storyId = storyId,
                                currentStates = selectedStates
                            )
                        )
                    }
                    onEvent(LibraryEvent.DismissContextMenu)
                },
                onRemove = {
                    uiState.selectedStoryId?.let { storyId ->
                        onEvent(LibraryEvent.RemoveFromLibrary(storyId))
                    }
                    onEvent(LibraryEvent.DismissContextMenu)
                }
            )
        }
    }
}

@Composable
private fun ContextMenuDialog(
    selectedStates: LibraryStates,
    onDismiss: () -> Unit,
    onEditStates: () -> Unit,
    onRemove: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = "Opciones",
                style = MaterialTheme.typography.titleLarge
            )
        },
        text = {
            Column {
                TextButton(
                    onClick = onEditStates,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.Start,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.Edit,
                            contentDescription = null
                        )
                        Spacer(modifier = Modifier.width(16.dp))
                        Text("Editar estados")
                    }
                }

                TextButton(
                    onClick = onRemove,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.Start,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.Delete,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.error
                        )
                        Spacer(modifier = Modifier.width(16.dp))
                        Text(
                            "Eliminar de biblioteca",
                            color = MaterialTheme.colorScheme.error
                        )
                    }
                }
            }
        },
        confirmButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancelar")
            }
        }
    )
}

@Composable
fun EmptyLibraryState(
    tab: LibraryTab,
    onExploreClick: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Icon(
            imageVector = Icons.Default.MenuBook,
            contentDescription = null,
            modifier = Modifier.size(64.dp),
            tint = MaterialTheme.colorScheme.onSurfaceVariant
        )

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = when (tab) {
                LibraryTab.FAVORITES -> "No hay favoritos"
                LibraryTab.READING -> "No hay historias en lectura"
                LibraryTab.COMPLETED -> "No hay historias finalizadas"
            },
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = "Empieza a explorar y descubre nuevas historias",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        Spacer(modifier = Modifier.height(24.dp))

        Button(onClick = onExploreClick) {
            Text(text = "Explorar Historias")
        }
    }
}

@Composable
fun StoryLibraryCard(
    storyWithProgress: StoryWithProgress,
    onClick: () -> Unit,
    onLongClick: () -> Unit,
    onMenuClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .combinedClickable(
                onClick = onClick,
                onLongClick = onLongClick
            ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp)
        ) {
            AsyncImage(
                model = storyWithProgress.story.coverImageUrl,
                contentDescription = storyWithProgress.story.title,
                modifier = Modifier
                    .width(80.dp)
                    .height(120.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .background(MaterialTheme.colorScheme.surfaceVariant),
                contentScale = ContentScale.Crop
            )

            Spacer(modifier = Modifier.width(12.dp))

            Column(
                modifier = Modifier.weight(1f)
            ) {
                StoryCardHeader(
                    title = storyWithProgress.story.title,
                    onMenuClick = onMenuClick
                )

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = "por ${storyWithProgress.story.userName ?: "Usuario ${storyWithProgress.story.userId}"}",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                if (storyWithProgress.isReading && storyWithProgress.progress != null) {
                    Spacer(modifier = Modifier.height(12.dp))
                    ReadingProgressSection(storyWithProgress = storyWithProgress)
                }

                if (storyWithProgress.isCompleted) {
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "✓ Completada",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.primary
                    )
                }
            }
        }
    }
}

@Composable
private fun StoryCardHeader(
    title: String,
    onMenuClick: () -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.Top
    ) {
        Text(
            text = title,
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.onSurface,
            maxLines = 2,
            overflow = TextOverflow.Ellipsis,
            modifier = Modifier.weight(1f)
        )

        IconButton(
            onClick = onMenuClick,
            modifier = Modifier.size(24.dp)
        ) {
            Icon(
                imageVector = Icons.Default.MoreVert,
                contentDescription = "Opciones",
                tint = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
private fun ReadingProgressSection(storyWithProgress: StoryWithProgress) {
    LinearProgressIndicator(
        progress = { storyWithProgress.progressPercentage / 100f },
        modifier = Modifier
            .fillMaxWidth()
            .height(6.dp)
            .clip(RoundedCornerShape(3.dp)),
        color = MaterialTheme.colorScheme.primary,
        trackColor = MaterialTheme.colorScheme.surfaceVariant
    )

    Spacer(modifier = Modifier.height(4.dp))

    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = "Cap ${storyWithProgress.progress?.chapterId} • ${storyWithProgress.progressPercentage}%",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        Text(
            text = storyWithProgress.lastReadTimeAgo,
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

@Preview(showBackground = true)
@Composable
fun LibraryScreenPreview() {
    LiteraVerseTheme {
        Box(modifier = Modifier.fillMaxSize()) {
            Text("Preview de LibraryScreen")
        }
    }
}

@Preview(showBackground = true)
@Composable
fun EmptyLibraryStatePreview() {
    LiteraVerseTheme {
        EmptyLibraryState(
            tab = LibraryTab.FAVORITES,
            onExploreClick = {}
        )
    }
}

@Preview(showBackground = true)
@Composable
fun StoryLibraryCardPreview() {
    LiteraVerseTheme {
        StoryLibraryCard(
            storyWithProgress = StoryWithProgress(
                story = StoryDetail(
                    storyId = 1,
                    userId = 1,
                    userName = "Ember Smith",
                    title = "El Último Ember",
                    synopsis = "Una historia épica",
                    coverImageUrl = null,
                    genre = "Fantasía",
                    tags = null,
                    isDraft = false,
                    isPublished = true,
                    createdAt = "",
                    publishedAt = null,
                    updatedAt = "",
                    viewCount = 0,
                    chapters = emptyList()
                ),
                progress = ReadingProgress(
                    progressId = 1,
                    userId = 1,
                    storyId = 1,
                    chapterId = 5,
                    scrollPosition = 0.5,
                    lastReadAt = "2024-01-01",
                    storyTitle = "El Último Ember",
                    chapterTitle = "Capítulo 5"
                ),
                isFavorite = true,
                isReading = true,
                isCompleted = false,
                totalChapters = 12
            ),
            onClick = {},
            onLongClick = {},
            onMenuClick = {}
        )
    }
}
