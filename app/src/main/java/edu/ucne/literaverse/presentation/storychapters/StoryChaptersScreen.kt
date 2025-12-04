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
            StoryChaptersFab(
                selectedTab = state.selectedTab,
                story = state.story,
                onCreateChapter = onNavigateToCreateChapter
            )
        }
    ) { padding ->
        StoryChaptersContent(
            state = state,
            padding = padding,
            onEvent = viewModel::onEvent,
            onNavigateToChapterEditor = onNavigateToChapterEditor
        )
    }
}

@Composable
private fun StoryChaptersFab(
    selectedTab: Int,
    story: StoryDetail?,
    onCreateChapter: (Int) -> Unit
) {
    if (selectedTab == 1 && story != null) {
        FloatingActionButton(
            onClick = { onCreateChapter(story.storyId) },
            containerColor = MaterialTheme.colorScheme.primary
        ) {
            Icon(
                imageVector = Icons.Default.Add,
                contentDescription = "Agregar capítulo"
            )
        }
    }
}

@Composable
private fun StoryChaptersContent(
    state: StoryChaptersUiState,
    padding: PaddingValues,
    onEvent: (StoryChaptersEvent) -> Unit,
    onNavigateToChapterEditor: (Int, Int) -> Unit
) {
    when {
        state.isLoading -> LoadingState(padding)
        state.story != null -> StoryTabsContainer(
            state = state,
            padding = padding,
            onEvent = onEvent,
            onNavigateToChapterEditor = onNavigateToChapterEditor
        )
    }

    state.error?.let { error ->
        ErrorSnackbar(error = error)
    }
}

@Composable
private fun LoadingState(padding: PaddingValues) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(padding),
        contentAlignment = Alignment.Center
    ) {
        CircularProgressIndicator()
    }
}

@Composable
private fun StoryTabsContainer(
    state: StoryChaptersUiState,
    padding: PaddingValues,
    onEvent: (StoryChaptersEvent) -> Unit,
    onNavigateToChapterEditor: (Int, Int) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(padding)
    ) {
        StoryChaptersTabs(
            selectedTab = state.selectedTab,
            onTabSelected = { onEvent(StoryChaptersEvent.OnTabSelected(it)) }
        )

        StoryTabContent(
            selectedTab = state.selectedTab,
            story = state.story!!,
            chapters = state.chapters,
            onPublishStory = { onEvent(StoryChaptersEvent.OnPublishStory) },
            onChapterClick = { chapter ->
                onNavigateToChapterEditor(state.story.storyId, chapter.chapterId)
            },
            onDeleteChapter = { onEvent(StoryChaptersEvent.OnDeleteChapter(it)) },
            onPublishChapter = { onEvent(StoryChaptersEvent.OnPublishChapter(it)) }
        )
    }
}

@Composable
private fun StoryChaptersTabs(
    selectedTab: Int,
    onTabSelected: (Int) -> Unit
) {
    TabRow(
        selectedTabIndex = selectedTab,
        containerColor = MaterialTheme.colorScheme.surface
    ) {
        Tab(
            selected = selectedTab == 0,
            onClick = { onTabSelected(0) },
            text = {
                Text(
                    "Detalles",
                    style = MaterialTheme.typography.titleSmall
                )
            }
        )
        Tab(
            selected = selectedTab == 1,
            onClick = { onTabSelected(1) },
            text = {
                Text(
                    "Capítulos",
                    style = MaterialTheme.typography.titleSmall
                )
            }
        )
        Tab(
            selected = selectedTab == 2,
            onClick = { onTabSelected(2) },
            text = {
                Text(
                    "Notas",
                    style = MaterialTheme.typography.titleSmall
                )
            }
        )
    }
}

@Composable
private fun StoryTabContent(
    selectedTab: Int,
    story: StoryDetail,
    chapters: List<Chapter>,
    onPublishStory: () -> Unit,
    onChapterClick: (Chapter) -> Unit,
    onDeleteChapter: (Int) -> Unit,
    onPublishChapter: (Int) -> Unit
) {
    when (selectedTab) {
        0 -> StoryDetailsTab(
            story = story,
            chapters = chapters,
            onPublishStory = onPublishStory
        )
        1 -> ChaptersTab(
            chapters = chapters,
            onChapterClick = onChapterClick,
            onDeleteChapter = onDeleteChapter,
            onPublishChapter = onPublishChapter
        )
        2 -> NotesTab()
    }
}

@Composable
private fun ErrorSnackbar(error: String) {
    Snackbar(
        modifier = Modifier.padding(16.dp)
    ) {
        Text(error)
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
            StoryCoverSection(coverUrl = story.coverImageUrl)
        }

        item {
            Text(
                text = story.title,
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold
            )
        }

        item {
            StoryStatsSection(
                viewCount = story.viewCount,
                chapterCount = chapters.size
            )
        }

        item {
            StorySynopsisSection(synopsis = story.synopsis)
        }

        if (story.genre != null) {
            item {
                StoryGenreSection(genre = story.genre)
            }
        }

        if (story.tags != null) {
            item {
                StoryTagsSection(tags = story.tags)
            }
        }

        if (!story.isPublished) {
            item {
                PublishStoryButton(onPublishStory = onPublishStory)
            }
        }
    }
}

@Composable
private fun StoryCoverSection(coverUrl: String?) {
    if (coverUrl != null) {
        AsyncImage(
            model = coverUrl,
            contentDescription = null,
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
                modifier = Modifier.size(64.dp),
                tint = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
private fun StoryStatsSection(
    viewCount: Int,
    chapterCount: Int
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceEvenly
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Icon(
                imageVector = Icons.Default.RemoveRedEye,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary
            )
            Text(
                text = "$viewCount",
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
                text = "$chapterCount",
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

@Composable
private fun StorySynopsisSection(synopsis: String) {
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Text(
            text = "Sinopsis",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold
        )
        Text(
            text = synopsis,
            style = MaterialTheme.typography.bodyMedium
        )
    }
}

@Composable
private fun StoryGenreSection(genre: String) {
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
                    genre,
                    style = MaterialTheme.typography.bodyMedium
                )
            }
        )
    }
}

@Composable
private fun StoryTagsSection(tags: String) {
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
            tags.split(",").take(3).forEach { tag ->
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

@Composable
private fun PublishStoryButton(onPublishStory: () -> Unit) {
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

@Composable
fun ChaptersTab(
    chapters: List<Chapter>,
    onChapterClick: (Chapter) -> Unit,
    onDeleteChapter: (Int) -> Unit,
    onPublishChapter: (Int) -> Unit
) {
    if (chapters.isEmpty()) {
        EmptyChaptersState()
    } else {
        ChaptersList(
            chapters = chapters,
            onChapterClick = onChapterClick,
            onDeleteChapter = onDeleteChapter,
            onPublishChapter = onPublishChapter
        )
    }
}

@Composable
private fun EmptyChaptersState() {
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
}

@Composable
private fun ChaptersList(
    chapters: List<Chapter>,
    onChapterClick: (Chapter) -> Unit,
    onDeleteChapter: (Int) -> Unit,
    onPublishChapter: (Int) -> Unit
) {
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
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            ChapterInfo(
                chapter = chapter,
                chapterNumber = chapterNumber,
                modifier = Modifier.weight(1f)
            )

            ChapterOptionsMenu(
                isPublished = chapter.isPublished,
                showMenu = showMenu,
                onMenuToggle = { showMenu = it },
                onPublishClick = {
                    showMenu = false
                    onPublishClick()
                },
                onDeleteClick = {
                    showMenu = false
                    showDeleteDialog = true
                }
            )
        }
    }

    if (showDeleteDialog) {
        DeleteChapterDialog(
            onDismiss = { showDeleteDialog = false },
            onConfirm = {
                showDeleteDialog = false
                onDeleteClick()
            }
        )
    }
}

@Composable
private fun ChapterInfo(
    chapter: Chapter,
    chapterNumber: Int,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        ChapterHeader(
            chapterNumber = chapterNumber,
            isPublished = chapter.isPublished
        )

        Text(
            text = chapter.title,
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            maxLines = 2,
            overflow = TextOverflow.Ellipsis
        )

        chapter.updatedAt?.let { updatedAt ->
            Text(
                text = "Actualizado: $updatedAt",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
            )
        }
    }
}

@Composable
private fun ChapterHeader(
    chapterNumber: Int,
    isPublished: Boolean
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Text(
            text = "Capítulo $chapterNumber",
            style = MaterialTheme.typography.labelMedium,
            color = MaterialTheme.colorScheme.primary
        )

        if (isPublished) {
            Badge(
                containerColor = MaterialTheme.colorScheme.primary
            ) {
                Text(
                    "Publicado",
                    style = MaterialTheme.typography.labelSmall
                )
            }
        } else {
            Badge(
                containerColor = MaterialTheme.colorScheme.tertiary
            ) {
                Text(
                    "Borrador",
                    style = MaterialTheme.typography.labelSmall
                )
            }
        }
    }
}

@Composable
private fun ChapterOptionsMenu(
    isPublished: Boolean,
    showMenu: Boolean,
    onMenuToggle: (Boolean) -> Unit,
    onPublishClick: () -> Unit,
    onDeleteClick: () -> Unit
) {
    Box {
        IconButton(onClick = { onMenuToggle(true) }) {
            Icon(
                imageVector = Icons.Default.MoreVert,
                contentDescription = "Opciones"
            )
        }

        DropdownMenu(
            expanded = showMenu,
            onDismissRequest = { onMenuToggle(false) }
        ) {
            if (!isPublished) {
                DropdownMenuItem(
                    text = { Text("Publicar") },
                    onClick = onPublishClick,
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
                onClick = onDeleteClick,
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

@Composable
private fun DeleteChapterDialog(
    onDismiss: () -> Unit,
    onConfirm: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
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
                onClick = onConfirm,
                colors = ButtonDefaults.textButtonColors(
                    contentColor = MaterialTheme.colorScheme.error
                )
            ) {
                Text("Eliminar")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancelar")
            }
        }
    )
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