package edu.ucne.literaverse.presentation.library

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.material.icons.filled.MenuBook
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
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
import edu.ucne.literaverse.presentation.components.BottomNavScreen
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

    LaunchedEffect(uiState.errorMessage) {
        uiState.errorMessage?.let { message ->
            snackbarHostState.showSnackbar(message)
            viewModel.onEvent(LibraryEvent.UserMessageShown)
        }
    }

    LaunchedEffect(uiState.successMessage) {
        uiState.successMessage?.let { message ->
            snackbarHostState.showSnackbar(message)
            viewModel.onEvent(LibraryEvent.UserMessageShown)
        }
    }

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
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            Text(
                text = "Mi Biblioteca",
                style = MaterialTheme.typography.headlineMedium,
                color = MaterialTheme.colorScheme.onBackground,
                modifier = Modifier.padding(16.dp)
            )

            TabRow(
                selectedTabIndex = uiState.selectedTab.ordinal,
                containerColor = MaterialTheme.colorScheme.surface
            ) {
                Tab(
                    selected = uiState.selectedTab == LibraryTab.FAVORITES,
                    onClick = { viewModel.onEvent(LibraryEvent.SelectTab(LibraryTab.FAVORITES)) },
                    text = {
                        Text(
                            text = "Favoritos",
                            style = MaterialTheme.typography.labelLarge
                        )
                    }
                )
                Tab(
                    selected = uiState.selectedTab == LibraryTab.READING,
                    onClick = { viewModel.onEvent(LibraryEvent.SelectTab(LibraryTab.READING)) },
                    text = {
                        Text(
                            text = "Leyendo",
                            style = MaterialTheme.typography.labelLarge
                        )
                    }
                )
                Tab(
                    selected = uiState.selectedTab == LibraryTab.COMPLETED,
                    onClick = { viewModel.onEvent(LibraryEvent.SelectTab(LibraryTab.COMPLETED)) },
                    text = {
                        Text(
                            text = "Finalizadas",
                            style = MaterialTheme.typography.labelLarge
                        )
                    }
                )
            }

            if (uiState.isLoading) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            } else {
                val currentList = when (uiState.selectedTab) {
                    LibraryTab.FAVORITES -> uiState.favorites
                    LibraryTab.READING -> uiState.reading
                    LibraryTab.COMPLETED -> uiState.completed
                }

                if (currentList.isEmpty()) {
                    EmptyLibraryState(
                        tab = uiState.selectedTab,
                        onExploreClick = onNavigateToHome
                    )
                } else {
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        verticalArrangement = Arrangement.spacedBy(12.dp),
                        contentPadding = androidx.compose.foundation.layout.PaddingValues(16.dp)
                    ) {
                        items(currentList) { storyWithProgress ->
                            StoryLibraryCard(
                                storyWithProgress = storyWithProgress,
                                onClick = {
                                    onStoryClick(
                                        storyWithProgress.story.storyId,
                                        storyWithProgress.lastReadChapterId
                                    )
                                }
                            )
                        }
                    }
                }
            }
        }
    }
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
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
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
                Text(
                    text = storyWithProgress.story.title,
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onSurface,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = "por ${storyWithProgress.story.userName ?: "Usuario ${storyWithProgress.story.userId}"}",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                if (storyWithProgress.isReading && storyWithProgress.progress != null) {
                    Spacer(modifier = Modifier.height(12.dp))

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
                            text = "Cap ${storyWithProgress.progress.chapterId} • ${storyWithProgress.progressPercentage}%",
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
            onClick = {}
        )
    }
}