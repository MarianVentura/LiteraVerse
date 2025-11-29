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
                    text = "Crear Nueva Historia",
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.Bold
                )

                Text(
                    text = "Completa los detalles de tu historia",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                )

                Spacer(modifier = Modifier.height(8.dp))

                val titleError = state.titleError
                OutlinedTextField(
                    value = state.title,
                    onValueChange = { viewModel.onEvent(CreateStoryEvent.OnTitleChanged(it)) },
                    label = { Text("Título") },
                    placeholder = { Text("Ingresa el título de tu historia") },
                    modifier = Modifier.fillMaxWidth(),
                    isError = titleError != null,
                    supportingText = {
                        if (titleError != null) {
                            Text(
                                text = titleError,
                                color = MaterialTheme.colorScheme.error
                            )
                        } else {
                            Text("${state.title.length}/200 caracteres")
                        }
                    },
                    singleLine = true
                )

                val synopsisError = state.synopsisError
                OutlinedTextField(
                    value = state.synopsis,
                    onValueChange = { viewModel.onEvent(CreateStoryEvent.OnSynopsisChanged(it)) },
                    label = { Text("Sinopsis") },
                    placeholder = { Text("Describe tu historia") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(200.dp),
                    isError = synopsisError != null,
                    supportingText = {
                        if (synopsisError != null) {
                            Text(
                                text = synopsisError,
                                color = MaterialTheme.colorScheme.error
                            )
                        } else {
                            Text("${state.synopsis.length}/1000 caracteres")
                        }
                    },
                    maxLines = 8
                )

                GenreDropdownMenu(
                    selectedGenre = state.genre,
                    onGenreSelected = { viewModel.onEvent(CreateStoryEvent.OnGenreChanged(it)) },
                    error = state.genreError
                )

                OutlinedTextField(
                    value = state.tags,
                    onValueChange = { viewModel.onEvent(CreateStoryEvent.OnTagsChanged(it)) },
                    label = { Text("Etiquetas (opcional)") },
                    placeholder = { Text("romance, aventura, fantasía") },
                    modifier = Modifier.fillMaxWidth(),
                    supportingText = {
                        Text("Separa las etiquetas con comas")
                    },
                    singleLine = true
                )

                Spacer(modifier = Modifier.height(16.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    OutlinedButton(
                        onClick = { viewModel.onEvent(CreateStoryEvent.OnSaveAsDraft) },
                        modifier = Modifier.weight(1f),
                        enabled = !state.isLoading
                    ) {
                        Text("Guardar como Borrador")
                    }

                    Button(
                        onClick = { viewModel.onEvent(CreateStoryEvent.OnPublish) },
                        modifier = Modifier.weight(1f),
                        enabled = !state.isLoading
                    ) {
                        if (state.isLoading) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(20.dp),
                                color = MaterialTheme.colorScheme.onPrimary
                            )
                        } else {
                            Text("Publicar")
                        }
                    }
                }

                Spacer(modifier = Modifier.height(32.dp))
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
                if (error != null) {
                    Text(
                        text = error,
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