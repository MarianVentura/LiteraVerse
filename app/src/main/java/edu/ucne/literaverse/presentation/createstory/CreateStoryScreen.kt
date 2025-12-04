package edu.ucne.literaverse.presentation.createstory

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle

@Composable
fun CreateStoryScreen(
    viewModel: CreateStoryViewModel = hiltViewModel(),
    onNavigateBack: () -> Unit = {},
    onStoryCreated: () -> Unit = {}
) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    LaunchedEffect(state.success) {
        if (state.success) {
            onStoryCreated()
        }
    }

    Scaffold(
        topBar = {
            CreateStoryTopBar(onNavigateBack = onNavigateBack)
        }
    ) { padding ->
        CreateStoryContent(
            state = state,
            onEvent = viewModel::onEvent,
            modifier = Modifier.padding(padding)
        )
    }
}

@Composable
private fun CreateStoryContent(
    state: CreateStoryUiState,
    onEvent: (CreateStoryEvent) -> Unit,
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
            CreateStoryHeader()

            Spacer(modifier = Modifier.height(8.dp))

            StoryTitleTextField(
                title = state.title,
                error = state.titleError,
                onTitleChanged = { onEvent(CreateStoryEvent.OnTitleChanged(it)) }
            )

            StorySynopsisTextField(
                synopsis = state.synopsis,
                error = state.synopsisError,
                onSynopsisChanged = { onEvent(CreateStoryEvent.OnSynopsisChanged(it)) }
            )

            GenreDropdownMenu(
                selectedGenre = state.genre,
                onGenreSelected = { onEvent(CreateStoryEvent.OnGenreChanged(it)) },
                error = state.genreError
            )

            TagsTextField(
                tags = state.tags,
                onTagsChanged = { onEvent(CreateStoryEvent.OnTagsChanged(it)) }
            )

            Spacer(modifier = Modifier.height(16.dp))

            StoryActionButtons(
                isLoading = state.isLoading,
                onSaveAsDraft = { onEvent(CreateStoryEvent.OnSaveAsDraft) },
                onPublish = { onEvent(CreateStoryEvent.OnPublish) }
            )

            Spacer(modifier = Modifier.height(32.dp))
        }

        ErrorSnackbar(error = state.error)
    }
}

@Composable
private fun CreateStoryHeader() {
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Text(
            text = "Crear Nueva Historia",
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold
        )

        Text(
            text = "Completa los detalles de tu historia",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
        )
    }
}

@Composable
private fun StoryTitleTextField(
    title: String,
    error: String?,
    onTitleChanged: (String) -> Unit
) {
    OutlinedTextField(
        value = title,
        onValueChange = onTitleChanged,
        label = { Text("Título") },
        placeholder = { Text("Ingresa el título de tu historia") },
        modifier = Modifier.fillMaxWidth(),
        isError = error != null,
        supportingText = {
            if (error != null) {
                Text(
                    text = error,
                    color = MaterialTheme.colorScheme.error
                )
            } else {
                Text("${title.length}/200 caracteres")
            }
        },
        singleLine = true
    )
}

@Composable
private fun StorySynopsisTextField(
    synopsis: String,
    error: String?,
    onSynopsisChanged: (String) -> Unit
) {
    OutlinedTextField(
        value = synopsis,
        onValueChange = onSynopsisChanged,
        label = { Text("Sinopsis") },
        placeholder = { Text("Describe tu historia") },
        modifier = Modifier
            .fillMaxWidth()
            .height(200.dp),
        isError = error != null,
        supportingText = {
            if (error != null) {
                Text(
                    text = error,
                    color = MaterialTheme.colorScheme.error
                )
            } else {
                Text("${synopsis.length}/1000 caracteres")
            }
        },
        maxLines = 8
    )
}

@Composable
private fun TagsTextField(
    tags: String,
    onTagsChanged: (String) -> Unit
) {
    OutlinedTextField(
        value = tags,
        onValueChange = onTagsChanged,
        label = { Text("Etiquetas (opcional)") },
        placeholder = { Text("romance, aventura, fantasía") },
        modifier = Modifier.fillMaxWidth(),
        supportingText = {
            Text("Separa las etiquetas con comas")
        },
        singleLine = true
    )
}

@Composable
private fun StoryActionButtons(
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
            Text("Guardar como Borrador")
        }

        PublishStoryButton(
            isLoading = isLoading,
            onClick = onPublish
        )
    }
}

@Composable
private fun RowScope.PublishStoryButton(
    isLoading: Boolean,
    onClick: () -> Unit
) {
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
            Text("Publicar")
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
fun CreateStoryTopBar(onNavigateBack: () -> Unit) {
    TopAppBar(
        title = {
            Text(
                text = "Nueva Historia",
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GenreDropdownMenu(
    selectedGenre: String,
    onGenreSelected: (String) -> Unit,
    error: String?
) {
    var expanded by remember { mutableStateOf(false) }

    val genres = listOf(
        "Romance",
        "Fantasía",
        "Ciencia Ficción",
        "Misterio",
        "Terror",
        "Aventura",
        "Drama",
        "Comedia",
        "Histórico",
        "Fanfic",
        "Poesía",
        "No Ficción",
        "Juvenil",
        "Paranormal",
        "Distopía"
    )

    ExposedDropdownMenuBox(
        expanded = expanded,
        onExpandedChange = { expanded = !expanded }
    ) {
        OutlinedTextField(
            value = selectedGenre,
            onValueChange = {},
            readOnly = true,
            label = { Text("Género") },
            placeholder = { Text("Selecciona un género") },
            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
            modifier = Modifier
                .fillMaxWidth()
                .menuAnchor(),
            isError = error != null,
            supportingText = {
                error?.let {
                    Text(
                        text = it,
                        color = MaterialTheme.colorScheme.error
                    )
                }
            },
            colors = ExposedDropdownMenuDefaults.outlinedTextFieldColors()
        )

        ExposedDropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {
            genres.forEach { genre ->
                DropdownMenuItem(
                    text = { Text(genre) },
                    onClick = {
                        onGenreSelected(genre)
                        expanded = false
                    }
                )
            }
        }
    }
}