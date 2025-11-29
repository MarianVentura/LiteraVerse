package edu.ucne.literaverse.presentation.storychapters

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.AsyncImage
import edu.ucne.literaverse.domain.model.Chapter
import edu.ucne.literaverse.domain.model.StoryDetail
import java.time.format.DateTimeFormatter

@Composable
fun StoryChaptersScreen(
    viewModel: StoryChaptersViewModel = hiltViewModel(),
    onNavigateBack: () -> Unit = {},
    onNavigateToChapterEditor: (Int, Int) -> Unit = { _, _ -> },
    onNavigateToCreateChapter: (Int) -> Unit = {}
) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    Scaffold(
        topBar = {
            StoryChaptersTopBar(onNavigateBack = onNavigateBack)
        },
        floatingActionButton = {
            if (state.selectedTab == 1 && state.story != null) {
                FloatingActionButton(
                    onClick = { onNavigateToCreateChapter(state.story!!.storyId) },
                    containerColor = MaterialTheme.colorScheme.primary
                ) {
                    Icon(
                        imageVector = Icons.Default.Add,
                        contentDescription = "Agregar capítulo"
                    )
                }
            }
        }
    ) { padding ->
        if (state.isLoading) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }
        } else if (state.story != null) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
            ) {
                TabRow(
                    selectedTabIndex = state.selectedTab,
                    containerColor = MaterialTheme.colorScheme.surface
                ) {
                    Tab(
                        selected = state.selectedTab == 0,
                        onClick = { viewModel.onEvent(StoryChaptersEvent.OnTabSelected(0)) },
                        text = {
                            Text(
                                "Detalles",
                                style = MaterialTheme.typography.titleSmall
                            )
                        }
                    )
                    Tab(
                        selected = state.selectedTab == 1,
                        onClick = { viewModel.onEvent(StoryChaptersEvent.OnTabSelected(1)) },
                        text = {
                            Text(
                                "Capítulos",
                                style = MaterialTheme.typography.titleSmall
                            )
                        }
                    )
                    Tab(
                        selected = state.selectedTab == 2,
                        onClick = { viewModel.onEvent(StoryChaptersEvent.OnTabSelected(2)) },
                        text = {
                            Text(
                                "Notas",
                                style = MaterialTheme.typography.titleSmall
                            )
                        }
                    )
                }

                when (state.selectedTab) {
                    0 -> StoryDetailsTab(
                        story = state.story!!,
                        chapters = state.chapters,
                        onPublishStory = { viewModel.onEvent(StoryChaptersEvent.OnPublishStory) }
                    )
                    1 -> ChaptersTab(
                        chapters = state.chapters,
                        onChapterClick = { chapter ->
                            onNavigateToChapterEditor(state.story!!.storyId, chapter.chapterId)
                        },
                        onDeleteChapter = { viewModel.onEvent(StoryChaptersEvent.OnDeleteChapter(it)) },
                        onPublishChapter = { viewModel.onEvent(StoryChaptersEvent.OnPublishChapter(it)) }
                    )
                    2 -> NotesTab()
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
fun StoryChaptersTopBar(onNavigateBack: () -> Unit) {
    TopAppBar(
        title = {
            Text(
                text = "Historia",
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
fun StoryDetailsTab(
    story: StoryDetail,
    chapters: List<Chapter>,
    onPublishStory: () -> Unit
) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            if (story.coverImageUrl != null) {
                AsyncImage(
                    model = story.coverImageUrl,
                    contentDescription = story.title,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(300.dp)
                        .clip(RoundedCornerShape(12.dp)),
                    contentScale = ContentScale.Crop
                )
            } else {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(300.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(MaterialTheme.colorScheme.surfaceVariant),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.MenuBook,
                        contentDescription = null,
                        modifier = Modifier.size(80.dp),
                        tint = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }

        item {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text(
                    text = story.title,
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.Bold
                )

                Row(
                    horizontalArrangement = Arrangement.spacedBy(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    if (story.isDraft && !story.isPublished) {
                        Badge(containerColor = MaterialTheme.colorScheme.tertiary) {
                            Text(
                                "Borrador",
                                style = MaterialTheme.typography.labelSmall
                            )
                        }
                    }
                    if (story.isPublished) {
                        Badge(containerColor = MaterialTheme.colorScheme.primary) {
                            Text(
                                "Publicada",
                                style = MaterialTheme.typography.labelSmall
                            )
                        }
                    }
                }
            }
        }

        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surfaceVariant
                )
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalArrangement = Arrangement.SpaceAround
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(
                            imageVector = Icons.Default.RemoveRedEye,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.primary
                        )
                        Text(
                            text = "${story.viewCount}",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "Vistas",
                            style = MaterialTheme.typography.bodySmall
                        )
                    }

                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(
                            imageVector = Icons.Default.Book,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.primary
                        )
                        Text(
                            text = "${chapters.size}",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "Capítulos",
                            style = MaterialTheme.typography.bodySmall
                        )
                    }
                }
            }
        }

        item {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text(
                    text = "Sinopsis",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = story.synopsis,
                    style = MaterialTheme.typography.bodyMedium
                )
            }
        }

        if (story.genre != null) {
            item {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text(
                        text = "Género",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                    AssistChip(
                        onClick = {},
                        label = {
                            Text(
                                story.genre,
                                style = MaterialTheme.typography.bodyMedium
                            )
                        }
                    )
                }
            }
        }

        if (story.tags != null) {
            item {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text(
                        text = "Etiquetas",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        story.tags.split(",").take(3).forEach { tag ->
                            AssistChip(
                                onClick = {},
                                label = {
                                    Text(
                                        tag.trim(),
                                        style = MaterialTheme.typography.bodySmall
                                    )
                                }
                            )
                        }
                    }
                }
            }
        }

        if (!story.isPublished) {
            item {
                Button(
                    onClick = onPublishStory,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Icon(
                        imageVector = Icons.Default.Publish,
                        contentDescription = null,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Publicar Historia")
                }
            }
        }
    }
}

@Composable
fun ChaptersTab(
    chapters: List<Chapter>,
    onChapterClick: (Chapter) -> Unit,
    onDeleteChapter: (Int) -> Unit,
    onPublishChapter: (Int) -> Unit
) {
    if (chapters.isEmpty()) {
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
                    imageVector = Icons.Default.MenuBook,
                    contentDescription = null,
                    modifier = Modifier.size(64.dp),
                    tint = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.4f)
                )
                Text(
                    text = "No hay capítulos",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                )
                Text(
                    text = "Agrega tu primer capítulo",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)
                )
            }
        }
    } else {
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            itemsIndexed(chapters) { index, chapter ->
                ChapterListItem(
                    chapter = chapter,
                    chapterNumber = index + 1,
                    onChapterClick = { onChapterClick(chapter) },
                    onDeleteClick = { onDeleteChapter(chapter.chapterId) },
                    onPublishClick = { onPublishChapter(chapter.chapterId) }
                )
            }
        }
    }
}

@Composable
fun ChapterListItem(
    chapter: Chapter,
    chapterNumber: Int,
    onChapterClick: () -> Unit,
    onDeleteClick: () -> Unit,
    onPublishClick: () -> Unit
) {
    var showMenu by remember { mutableStateOf(false) }
    var showDeleteDialog by remember { mutableStateOf(false) }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onChapterClick() },
        shape = RoundedCornerShape(12.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .background(MaterialTheme.colorScheme.primaryContainer),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "$chapterNumber",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onPrimaryContainer
                )
            }

            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Text(
                    text = chapter.title,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )

                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    if (chapter.isDraft && !chapter.isPublished) {
                        Badge(containerColor = MaterialTheme.colorScheme.tertiary) {
                            Text(
                                "Borrador",
                                style = MaterialTheme.typography.labelSmall
                            )
                        }
                    }
                    if (chapter.isPublished) {
                        Badge(containerColor = MaterialTheme.colorScheme.primary) {
                            Text(
                                "Publicado",
                                style = MaterialTheme.typography.labelSmall
                            )
                        }
                    }

                    chapter.updatedAt?.let { date ->
                        Text(
                            text = date.format(DateTimeFormatter.ofPattern("dd/MM/yyyy")),
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                        )
                    }
                }
            }

            Box {
                IconButton(onClick = { showMenu = true }) {
                    Icon(
                        imageVector = Icons.Default.MoreVert,
                        contentDescription = "Opciones"
                    )
                }

                DropdownMenu(
                    expanded = showMenu,
                    onDismissRequest = { showMenu = false }
                ) {
                    if (!chapter.isPublished) {
                        DropdownMenuItem(
                            text = { Text("Publicar") },
                            onClick = {
                                showMenu = false
                                onPublishClick()
                            },
                            leadingIcon = {
                                Icon(
                                    imageVector = Icons.Default.Publish,
                                    contentDescription = null
                                )
                            }
                        )
                    }

                    DropdownMenuItem(
                        text = { Text("Eliminar") },
                        onClick = {
                            showMenu = false
                            showDeleteDialog = true
                        },
                        leadingIcon = {
                            Icon(
                                imageVector = Icons.Default.Delete,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.error
                            )
                        }
                    )
                }
            }
        }
    }

    if (showDeleteDialog) {
        AlertDialog(
            onDismissRequest = { showDeleteDialog = false },
            title = {
                Text(
                    "Eliminar Capítulo",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold
                )
            },
            text = {
                Text(
                    "¿Estás seguro de que deseas eliminar este capítulo? Esta acción no se puede deshacer.",
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
fun NotesTab() {
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
                imageVector = Icons.Default.Notes,
                contentDescription = null,
                modifier = Modifier.size(64.dp),
                tint = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.4f)
            )
            Text(
                text = "Notas",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
            )
            Text(
                text = "Funcionalidad próximamente",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)
            )
        }
    }
}