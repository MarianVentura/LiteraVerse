package edu.ucne.literaverse.presentation.chapterreader

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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.ui.tooling.preview.Preview

@Composable
fun ChapterReaderScreen(
    viewModel: ChapterReaderViewModel = hiltViewModel(),
    onNavigateBack: () -> Unit
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(state.userMessage) {
        state.userMessage?.let { message ->
            snackbarHostState.showSnackbar(message)
            viewModel.onEvent(ChapterReaderEvent.UserMessageShown)
        }
    }

    Scaffold(
        topBar = {
            if (state.showMenu) {
                ChapterReaderTopBar(
                    title = state.chapter?.title ?: "Cargando...",
                    currentChapter = state.currentChapterNumber,
                    totalChapters = state.totalChapters,
                    onNavigateBack = onNavigateBack
                )
            }
        },
        bottomBar = {
            if (state.showMenu && !state.isLoading) {
                ChapterReaderBottomBar(
                    hasPrevious = state.hasPreviousChapter,
                    hasNext = state.hasNextChapter,
                    onPreviousClick = { viewModel.onEvent(ChapterReaderEvent.OnPreviousChapter) },
                    onNextClick = { viewModel.onEvent(ChapterReaderEvent.OnNextChapter) }
                )
            }
        },
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { padding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            if (state.isLoading) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            } else if (state.error != null) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = state.error ?: "Error desconocido",
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.error
                    )
                }
            } else if (state.chapter != null) {
                ChapterReaderContent(
                    chapter = state.chapter!!,
                    onToggleMenu = { viewModel.onEvent(ChapterReaderEvent.OnToggleMenu) },
                    onScrollPositionChanged = { position ->
                        viewModel.onEvent(ChapterReaderEvent.OnScrollPositionChanged(position))
                    }
                )
            }

            if (state.totalChapters > 0 && !state.isLoading) {
                LinearProgressIndicator(
                    progress = { state.currentChapterNumber.toFloat() / state.totalChapters.toFloat() },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(3.dp)
                        .align(Alignment.TopCenter),
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChapterReaderTopBar(
    title: String,
    currentChapter: Int,
    totalChapters: Int,
    onNavigateBack: () -> Unit
) {
    TopAppBar(
        title = {
            Column {
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    maxLines = 1
                )
                if (totalChapters > 0) {
                    Text(
                        text = "Capítulo $currentChapter de $totalChapters",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                    )
                }
            }
        },
        navigationIcon = {
            IconButton(onClick = onNavigateBack) {
                Icon(
                    imageVector = Icons.Default.Close,
                    contentDescription = "Cerrar"
                )
            }
        },
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = MaterialTheme.colorScheme.surface.copy(alpha = 0.95f)
        )
    )
}

@Composable
fun ChapterReaderBottomBar(
    hasPrevious: Boolean,
    hasNext: Boolean,
    onPreviousClick: () -> Unit,
    onNextClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.surface.copy(alpha = 0.95f))
            .padding(16.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        IconButton(
            onClick = onPreviousClick,
            enabled = hasPrevious
        ) {
            Icon(
                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                contentDescription = "Capítulo anterior",
                tint = if (hasPrevious) {
                    MaterialTheme.colorScheme.primary
                } else {
                    MaterialTheme.colorScheme.onSurface.copy(alpha = 0.3f)
                }
            )
        }

        Text(
            text = "Toca para mostrar/ocultar menú",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
        )

        IconButton(
            onClick = onNextClick,
            enabled = hasNext
        ) {
            Icon(
                imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                contentDescription = "Siguiente capítulo",
                tint = if (hasNext) {
                    MaterialTheme.colorScheme.primary
                } else {
                    MaterialTheme.colorScheme.onSurface.copy(alpha = 0.3f)
                }
            )
        }
    }
}

@Composable
fun ChapterReaderContent(
    chapter: edu.ucne.literaverse.domain.model.Chapter,
    onToggleMenu: () -> Unit,
    onScrollPositionChanged: (Double) -> Unit
) {
    val scrollState = rememberScrollState()

    LaunchedEffect(scrollState.value) {
        val maxScroll = scrollState.maxValue
        val currentScroll = scrollState.value
        val scrollPosition = if (maxScroll > 0) {
            currentScroll.toDouble() / maxScroll.toDouble()
        } else {
            0.0
        }
        onScrollPositionChanged(scrollPosition)
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .clickable { onToggleMenu() }
            .verticalScroll(scrollState)
            .padding(24.dp)
    ) {
        Text(
            text = chapter.title,
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onSurface
        )

        Spacer(modifier = Modifier.height(24.dp))

        Text(
            text = chapter.content,
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurface,
            lineHeight = MaterialTheme.typography.bodyLarge.lineHeight.times(1.5f)
        )

        Spacer(modifier = Modifier.height(48.dp))
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
private fun ChapterReaderScreenPreview() {
    MaterialTheme {
        Scaffold(
            topBar = {
                ChapterReaderTopBar(
                    title = "Capítulo 1: El Comienzo",
                    currentChapter = 1,
                    totalChapters = 12,
                    onNavigateBack = {}
                )
            },
            bottomBar = {
                ChapterReaderBottomBar(
                    hasPrevious = true,
                    hasNext = true,
                    onPreviousClick = {},
                    onNextClick = {}
                )
            }
        ) { padding ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .verticalScroll(rememberScrollState())
                    .padding(24.dp)
            ) {
                Text(
                    text = "El Comienzo",
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.Bold
                )

                Spacer(modifier = Modifier.height(24.dp))

                Text(
                    text = "En un reino lejano, donde las montañas tocaban el cielo y los ríos cantaban melodías ancestrales, vivía una joven llamada Aria...",
                    style = MaterialTheme.typography.bodyLarge,
                    lineHeight = MaterialTheme.typography.bodyLarge.lineHeight.times(1.5f)
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun ChapterReaderTopBarPreview() {
    MaterialTheme {
        ChapterReaderTopBar(
            title = "Capítulo 1: El Comienzo",
            currentChapter = 1,
            totalChapters = 12,
            onNavigateBack = {}
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun ChapterReaderBottomBarPreview() {
    MaterialTheme {
        ChapterReaderBottomBar(
            hasPrevious = true,
            hasNext = true,
            onPreviousClick = {},
            onNextClick = {}
        )
    }
}