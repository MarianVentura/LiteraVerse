package edu.ucne.literaverse.presentation.chaptereditor

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle

@Composable
fun ChapterEditorScreen(
    viewModel: ChapterEditorViewModel = hiltViewModel(),
    onNavigateBack: () -> Unit = {},
    onChapterSaved: () -> Unit = {}
) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    LaunchedEffect(state.success) {
        if (state.success) {
            onChapterSaved()
        }
    }

    Scaffold(
        topBar = {
            ChapterEditorTopBar(
                isEditMode = state.isEditMode,
                onNavigateBack = onNavigateBack
            )
        }
    ) { padding ->
        ChapterEditorContent(
            state = state,
            onEvent = viewModel::onEvent,
            modifier = Modifier.padding(padding)
        )
    }
}

@Composable
private fun ChapterEditorContent(
    state: ChapterEditorUiState,
    onEvent: (ChapterEditorEvent) -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier.fillMaxSize()
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            ChapterEditorTitle(isEditMode = state.isEditMode)

            ChapterNumberCard(
                isEditMode = state.isEditMode,
                chapterNumber = state.chapterNumber
            )

            Spacer(modifier = Modifier.height(8.dp))

            TitleTextField(
                title = state.title,
                error = state.titleError,
                onTitleChanged = { onEvent(ChapterEditorEvent.OnTitleChanged(it)) }
            )

            ContentTextField(
                content = state.content,
                error = state.contentError,
                onContentChanged = { onEvent(ChapterEditorEvent.OnContentChanged(it)) }
            )

            Spacer(modifier = Modifier.height(16.dp))

            ActionButtons(
                isEditMode = state.isEditMode,
                isLoading = state.isLoading,
                onSaveAsDraft = { onEvent(ChapterEditorEvent.OnSaveAsDraft) },
                onPublish = { onEvent(ChapterEditorEvent.OnPublish) }
            )

            Spacer(modifier = Modifier.height(32.dp))
        }

        ErrorSnackbar(error = state.error)
    }
}

@Composable
private fun ChapterEditorTitle(isEditMode: Boolean) {
    val titleText = if (isEditMode) "Editar Capítulo" else "Nuevo Capítulo"
    Text(
        text = titleText,
        style = MaterialTheme.typography.headlineMedium,
        fontWeight = FontWeight.Bold
    )
}

@Composable
private fun ChapterNumberCard(
    isEditMode: Boolean,
    chapterNumber: Int
) {
    if (!isEditMode) {
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.primaryContainer
            )
        ) {
            Row(
                modifier = Modifier.padding(16.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text(
                    text = "Capítulo",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onPrimaryContainer
                )
                Text(
                    text = "$chapterNumber",
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onPrimaryContainer
                )
            }
        }
    }
}

@Composable
private fun TitleTextField(
    title: String,
    error: String?,
    onTitleChanged: (String) -> Unit
) {
    OutlinedTextField(
        value = title,
        onValueChange = onTitleChanged,
        label = { Text("Título del Capítulo") },
        placeholder = { Text("Ingresa el título") },
        modifier = Modifier.fillMaxWidth(),
        isError = error != null,
        supportingText = {
            error?.let {
                Text(
                    text = it,
                    color = MaterialTheme.colorScheme.error
                )
            }
        },
        singleLine = true
    )
}

@Composable
private fun ContentTextField(
    content: String,
    error: String?,
    onContentChanged: (String) -> Unit
) {
    OutlinedTextField(
        value = content,
        onValueChange = onContentChanged,
        label = { Text("Contenido") },
        placeholder = { Text("Escribe tu capítulo aquí...") },
        modifier = Modifier
            .fillMaxWidth()
            .heightIn(min = 400.dp),
        isError = error != null,
        supportingText = {
            if (error != null) {
                Text(
                    text = error,
                    color = MaterialTheme.colorScheme.error
                )
            } else {
                Text("${content.length} caracteres")
            }
        },
        maxLines = Int.MAX_VALUE
    )
}

@Composable
private fun ActionButtons(
    isEditMode: Boolean,
    isLoading: Boolean,
    onSaveAsDraft: () -> Unit,
    onPublish: () -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        OutlinedButton(
            onClick = onSaveAsDraft,
            modifier = Modifier.weight(1f),
            enabled = !isLoading
        ) {
            Text("Guardar Borrador")
        }

        PublishButton(
            isEditMode = isEditMode,
            isLoading = isLoading,
            onClick = onPublish
        )
    }
}

@Composable
private fun RowScope.PublishButton(
    isEditMode: Boolean,
    isLoading: Boolean,
    onClick: () -> Unit
) {
    val buttonText = if (isEditMode) "Actualizar" else "Publicar"

    Button(
        onClick = onClick,
        modifier = Modifier.weight(1f),
        enabled = !isLoading
    ) {
        if (isLoading) {
            CircularProgressIndicator(
                modifier = Modifier.size(20.dp),
                color = MaterialTheme.colorScheme.onPrimary
            )
        } else {
            Text(buttonText)
        }
    }
}

@Composable
private fun ErrorSnackbar(error: String?) {
    error?.let {
        Snackbar(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(it)
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChapterEditorTopBar(
    isEditMode: Boolean,
    onNavigateBack: () -> Unit
) {
    val titleText = if (isEditMode) "Editar Capítulo" else "Nuevo Capítulo"

    TopAppBar(
        title = {
            Text(
                text = titleText,
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

@Preview(showBackground = true, showSystemUi = true)
@Composable
private fun ChapterEditorScreenPreview() {
    MaterialTheme {

        Scaffold(
            topBar = {
                ChapterEditorTopBar(
                    isEditMode = false,
                    onNavigateBack = {}
                )
            }
        ) { padding ->
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .verticalScroll(rememberScrollState())
                        .padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    Text(
                        text = "Nuevo Capítulo",
                        style = MaterialTheme.typography.headlineMedium,
                        fontWeight = FontWeight.Bold
                    )

                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.primaryContainer
                        )
                    ) {
                        Row(
                            modifier = Modifier.padding(16.dp),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Text(
                                text = "Capítulo",
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onPrimaryContainer
                            )
                            Text(
                                text = "1",
                                style = MaterialTheme.typography.bodyMedium,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onPrimaryContainer
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    OutlinedTextField(
                        value = "El Comienzo de una Aventura",
                        onValueChange = {},
                        label = { Text("Título del Capítulo") },
                        placeholder = { Text("Ingresa el título") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true
                    )

                    OutlinedTextField(
                        value = "Había una vez en un reino lejano...",
                        onValueChange = {},
                        label = { Text("Contenido") },
                        placeholder = { Text("Escribe tu capítulo aquí...") },
                        modifier = Modifier
                            .fillMaxWidth()
                            .heightIn(min = 400.dp),
                        supportingText = {
                            Text("38 caracteres")
                        },
                        maxLines = Int.MAX_VALUE
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        OutlinedButton(
                            onClick = {},
                            modifier = Modifier.weight(1f)
                        ) {
                            Text("Guardar Borrador")
                        }

                        Button(
                            onClick = {},
                            modifier = Modifier.weight(1f)
                        ) {
                            Text("Publicar")
                        }
                    }

                    Spacer(modifier = Modifier.height(32.dp))
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun ChapterEditorTopBarPreview() {
    MaterialTheme {
        ChapterEditorTopBar(
            isEditMode = false,
            onNavigateBack = {}
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun ChapterEditorTopBarEditModePreview() {
    MaterialTheme {
        ChapterEditorTopBar(
            isEditMode = true,
            onNavigateBack = {}
        )
    }
}