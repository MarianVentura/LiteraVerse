package edu.ucne.literaverse.presentation.search


import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.AsyncImage
import edu.ucne.literaverse.domain.model.SortCriteria
import edu.ucne.literaverse.domain.model.Story
import edu.ucne.literaverse.presentation.components.UserMenuBottomBar
import edu.ucne.literaverse.presentation.components.BottomNavScreen


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchScreen(
    viewModel: SearchViewModel = hiltViewModel(),
    onStoryClick: (Int) -> Unit = {},
    onNavigateToHome: () -> Unit = {},
    onNavigateToLibrary: () -> Unit = {},
    onNavigateToWrite: () -> Unit = {},
    onNavigateToPerfil: () -> Unit = {},
    onLogout: () -> Unit = {}
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    val snackbarHostState = remember { SnackbarHostState() }


    LaunchedEffect(state.userMessage) {
        state.userMessage?.let { message ->
            snackbarHostState.showSnackbar(message)
            viewModel.onEvent(SearchEvent.UserMessageShown)
        }
    }


    Scaffold(
        bottomBar = {
            UserMenuBottomBar(
                currentScreen = BottomNavScreen.SEARCH,
                onNavigateToHome = onNavigateToHome,
                onNavigateToBuscar = {},
                onNavigateToLibrary = onNavigateToLibrary,
                onNavigateToWrite = onNavigateToWrite,
                onNavigateToPerfil = onNavigateToPerfil,
                onLogout = onLogout
            )
        },
        snackbarHost = { SnackbarHost(snackbarHostState) },
        containerColor = MaterialTheme.colorScheme.background
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            item {
                Text(
                    text = "Buscar",
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.Bold
                )
            }


            item {
                SearchBar(
                    query = state.searchQuery,
                    onQueryChange = { viewModel.onEvent(SearchEvent.OnQueryChange(it)) },
                    onSearch = { viewModel.onEvent(SearchEvent.OnSearch) },
                    onToggleFilters = { viewModel.onEvent(SearchEvent.ToggleFilters) },
                    showFilters = state.showFilters
                )
            }


            if (state.showFilters) {
                item {
                    FiltersSection(
                        selectedGenre = state.selectedGenre,
                        selectedStatus = state.selectedStatus,
                        selectedSort = state.selectedSort,
                        onGenreSelect = { viewModel.onEvent(SearchEvent.OnGenreSelect(it)) },
                        onStatusSelect = { viewModel.onEvent(SearchEvent.OnStatusSelect(it)) },
                        onSortChange = { viewModel.onEvent(SearchEvent.OnSortChange(it)) },
                        onClearFilters = { viewModel.onEvent(SearchEvent.ClearFilters) }
                    )
                }
            }


            if (state.isLoading) {
                item {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(200.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator()
                    }
                }
            } else if (state.stories.isEmpty() && state.hasSearched) {
                item {
                    EmptySearchResults()
                }
            } else {
                if (!state.hasSearched && state.stories.isNotEmpty()) {
                    item {
                        Text(
                            text = "Historias Populares",
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }


                items(state.stories) { story ->
                    StoryCard(
                        story = story,
                        onClick = { onStoryClick(story.storyId) }
                    )
                }
            }
        }
    }
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchBar(
    query: String,
    onQueryChange: (String) -> Unit,
    onSearch: () -> Unit,
    onToggleFilters: () -> Unit,
    showFilters: Boolean
) {
    val focusManager = LocalFocusManager.current


    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        OutlinedTextField(
            value = query,
            onValueChange = onQueryChange,
            modifier = Modifier.weight(1f),
            placeholder = {
                Text(
                    text = "Buscar historias, autores...",
                    style = MaterialTheme.typography.bodyMedium
                )
            },
            leadingIcon = {
                Icon(
                    imageVector = Icons.Default.Search,
                    contentDescription = null
                )
            },
            trailingIcon = {
                if (query.isNotBlank()) {
                    IconButton(onClick = { onQueryChange("") }) {
                        Icon(
                            imageVector = Icons.Default.Clear,
                            contentDescription = null
                        )
                    }
                }
            },
            singleLine = true,
            shape = RoundedCornerShape(12.dp),
            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
            keyboardActions = KeyboardActions(
                onSearch = {
                    onSearch()
                    focusManager.clearFocus()
                }
            ),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = MaterialTheme.colorScheme.primary,
                unfocusedBorderColor = MaterialTheme.colorScheme.outline
            )
        )


        IconButton(
            onClick = onToggleFilters,
            modifier = Modifier
                .size(56.dp)
                .background(
                    color = if (showFilters)
                        MaterialTheme.colorScheme.primary
                    else
                        MaterialTheme.colorScheme.primaryContainer,
                    shape = RoundedCornerShape(12.dp)
                )
        ) {
            Icon(
                imageVector = Icons.Default.FilterList,
                contentDescription = null,
                tint = if (showFilters)
                    MaterialTheme.colorScheme.onPrimary
                else
                    MaterialTheme.colorScheme.onPrimaryContainer
            )
        }
    }
}


@Composable
fun FiltersSection(
    selectedGenre: String?,
    selectedStatus: String?,
    selectedSort: SortCriteria,
    onGenreSelect: (String?) -> Unit,
    onStatusSelect: (String?) -> Unit,
    onSortChange: (SortCriteria) -> Unit,
    onClearFilters: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        )
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Filtros",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                TextButton(onClick = onClearFilters) {
                    Text(text = "Limpiar")
                }
            }


            Spacer(modifier = Modifier.height(12.dp))


            Text(
                text = "Género",
                style = MaterialTheme.typography.labelLarge,
                fontWeight = FontWeight.SemiBold
            )
            Spacer(modifier = Modifier.height(8.dp))
            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(genreOptions) { genre ->
                    FilterChip(
                        selected = selectedGenre == genre,
                        onClick = {
                            onGenreSelect(if (selectedGenre == genre) null else genre)
                        },
                        label = {
                            Text(
                                text = genre,
                                style = MaterialTheme.typography.bodyMedium
                            )
                        }
                    )
                }
            }


            Spacer(modifier = Modifier.height(16.dp))


            Text(
                text = "Estado",
                style = MaterialTheme.typography.labelLarge,
                fontWeight = FontWeight.SemiBold
            )
            Spacer(modifier = Modifier.height(8.dp))
            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(statusOptions) { status ->
                    FilterChip(
                        selected = selectedStatus == status.value,
                        onClick = {
                            onStatusSelect(if (selectedStatus == status.value) null else status.value)
                        },
                        label = {
                            Text(
                                text = status.label,
                                style = MaterialTheme.typography.bodyMedium
                            )
                        }
                    )
                }
            }


            Spacer(modifier = Modifier.height(16.dp))


            Text(
                text = "Ordenar por",
                style = MaterialTheme.typography.labelLarge,
                fontWeight = FontWeight.SemiBold
            )
            Spacer(modifier = Modifier.height(8.dp))
            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(sortOptions) { sort ->
                    FilterChip(
                        selected = selectedSort == sort.criteria,
                        onClick = { onSortChange(sort.criteria) },
                        label = {
                            Text(
                                text = sort.label,
                                style = MaterialTheme.typography.bodyMedium
                            )
                        }
                    )
                }
            }
        }
    }
}


@Composable
fun StoryCard(
    story: Story,
    onClick: () -> Unit
) {
    ElevatedCard(
        onClick = onClick,
        modifier = Modifier
            .fillMaxWidth()
            .height(140.dp),
        shape = RoundedCornerShape(12.dp),
        elevation = CardDefaults.elevatedCardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxSize()
        ) {
            AsyncImage(
                model = story.coverImageUrl,
                contentDescription = null,
                modifier = Modifier
                    .width(100.dp)
                    .fillMaxHeight()
                    .clip(RoundedCornerShape(topStart = 12.dp, bottomStart = 12.dp)),
                contentScale = ContentScale.Crop
            )


            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(12.dp),
                verticalArrangement = Arrangement.SpaceBetween
            ) {
                Column {
                    Text(
                        text = story.title,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = story.author,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }


                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Visibility,
                                contentDescription = null,
                                modifier = Modifier.size(16.dp),
                                tint = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Text(
                                text = story.reads.toString(),
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Book,
                                contentDescription = null,
                                modifier = Modifier.size(16.dp),
                                tint = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Text(
                                text = "${story.chapters} caps",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }


                    AssistChip(
                        onClick = {},
                        label = {
                            Text(
                                text = story.status,
                                style = MaterialTheme.typography.labelSmall
                            )
                        },
                        colors = AssistChipDefaults.assistChipColors(
                            containerColor = if (story.status == "Publicado")
                                MaterialTheme.colorScheme.primaryContainer
                            else
                                MaterialTheme.colorScheme.secondaryContainer
                        )
                    )
                }
            }
        }
    }
}


@Composable
fun EmptySearchResults() {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 48.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Icon(
            imageVector = Icons.Default.SearchOff,
            contentDescription = null,
            modifier = Modifier.size(64.dp),
            tint = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = "No se encontraron resultados",
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = "Intenta con otros filtros o términos de búsqueda",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}


private val genreOptions = listOf(
    "Fantasía",
    "Romance",
    "Ciencia Ficción",
    "Misterio",
    "Aventura",
    "Drama",
    "Horror",
    "Comedia"
)


private data class StatusOption(val label: String, val value: String)
private val statusOptions = listOf(
    StatusOption("Publicado", "published"),
    StatusOption("Borrador", "draft")
)


private data class SortOption(val label: String, val criteria: SortCriteria)
private val sortOptions = listOf(
    SortOption("Relevancia", SortCriteria.RELEVANCE),
    SortOption("Popularidad", SortCriteria.POPULARITY),
    SortOption("Recientes", SortCriteria.RECENT),
    SortOption("Más leídos", SortCriteria.MOST_READ)
)

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun SearchScreenPreview() {
    MaterialTheme {
        SearchScreenContent(
            state = SearchUiState(
                searchQuery = "",
                stories = listOf(
                    Story(
                        storyId = 1,
                        title = "El Reino de las Sombras",
                        author = "María García",
                        description = "Una épica historia de fantasía",
                        coverImageUrl = null,
                        genres = listOf("Fantasía", "Aventura"),
                        reads = 25000,
                        chapters = 35,
                        status = "Publicado",
                        synopsis = "Un joven descubre su destino en un mundo mágico."
                    ),
                    Story(
                        storyId = 2,
                        title = "Amor en Tiempos Modernos",
                        author = "Carlos Ruiz",
                        description = "Romance contemporáneo",
                        coverImageUrl = null,
                        genres = listOf("Romance"),
                        reads = 18000,
                        chapters = 22,
                        status = "Publicado",
                        synopsis = "Dos almas se encuentran en la ciudad."
                    )
                ),
                selectedGenre = null,
                selectedStatus = null,
                selectedSort = SortCriteria.RELEVANCE,
                showFilters = false,
                isLoading = false,
                hasSearched = false,
                userMessage = null
            ),
            onEvent = {},
            onStoryClick = {}
        )
    }
}

@Preview(showBackground = true)
@Composable
fun SearchScreenWithFiltersPreview() {
    MaterialTheme {
        SearchScreenContent(
            state = SearchUiState(
                searchQuery = "fantasía",
                stories = emptyList(),
                selectedGenre = "Fantasía",
                selectedStatus = null,
                selectedSort = SortCriteria.POPULARITY,
                showFilters = true,
                isLoading = false,
                hasSearched = true,
                userMessage = null
            ),
            onEvent = {},
            onStoryClick = {}
        )
    }
}

@Preview(showBackground = true)
@Composable
fun SearchBarPreview() {
    MaterialTheme {
        SearchBar(
            query = "búsqueda de prueba",
            onQueryChange = {},
            onSearch = {},
            onToggleFilters = {},
            showFilters = false
        )
    }
}

@Preview(showBackground = true)
@Composable
fun FiltersSectionPreview() {
    MaterialTheme {
        FiltersSection(
            selectedGenre = "Fantasía",
            selectedStatus = "published",
            selectedSort = SortCriteria.POPULARITY,
            onGenreSelect = {},
            onStatusSelect = {},
            onSortChange = {},
            onClearFilters = {}
        )
    }
}

@Preview(showBackground = true)
@Composable
fun StoryCardPreview() {
    MaterialTheme {
        StoryCard(
            story = Story(
                storyId = 1,
                title = "El Reino de las Sombras Eternas",
                author = "María García López",
                description = "Una épica historia de fantasía",
                coverImageUrl = null,
                genres = listOf("Fantasía", "Aventura"),
                reads = 25000,
                chapters = 35,
                status = "Publicado",
                synopsis = "Un joven descubre su destino."
            ),
            onClick = {}
        )
    }
}

@Preview(showBackground = true)
@Composable
fun EmptySearchResultsPreview() {
    MaterialTheme {
        EmptySearchResults()
    }
}

@Composable
private fun SearchScreenContent(
    state: SearchUiState,
    onEvent: (SearchEvent) -> Unit,
    onStoryClick: (Int) -> Unit
) {
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(state.userMessage) {
        state.userMessage?.let { message ->
            snackbarHostState.showSnackbar(message)
            onEvent(SearchEvent.UserMessageShown)
        }
    }

    Scaffold(
        bottomBar = {
            UserMenuBottomBar(
                currentScreen = BottomNavScreen.SEARCH,
                onNavigateToHome = {},
                onNavigateToBuscar = {},
                onNavigateToLibrary = {},
                onNavigateToWrite = {},
                onNavigateToPerfil = {},
                onLogout = {}
            )
        },
        snackbarHost = { SnackbarHost(snackbarHostState) },
        containerColor = MaterialTheme.colorScheme.background
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            item {
                Text(
                    text = "Buscar",
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.Bold
                )
            }

            item {
                SearchBar(
                    query = state.searchQuery,
                    onQueryChange = { onEvent(SearchEvent.OnQueryChange(it)) },
                    onSearch = { onEvent(SearchEvent.OnSearch) },
                    onToggleFilters = { onEvent(SearchEvent.ToggleFilters) },
                    showFilters = state.showFilters
                )
            }

            if (state.showFilters) {
                item {
                    FiltersSection(
                        selectedGenre = state.selectedGenre,
                        selectedStatus = state.selectedStatus,
                        selectedSort = state.selectedSort,
                        onGenreSelect = { onEvent(SearchEvent.OnGenreSelect(it)) },
                        onStatusSelect = { onEvent(SearchEvent.OnStatusSelect(it)) },
                        onSortChange = { onEvent(SearchEvent.OnSortChange(it)) },
                        onClearFilters = { onEvent(SearchEvent.ClearFilters) }
                    )
                }
            }

            if (state.isLoading) {
                item {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(200.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator()
                    }
                }
            } else if (state.stories.isEmpty() && state.hasSearched) {
                item {
                    EmptySearchResults()
                }
            } else {
                if (!state.hasSearched && state.stories.isNotEmpty()) {
                    item {
                        Text(
                            text = "Historias Populares",
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }

                items(state.stories) { story ->
                    StoryCard(
                        story = story,
                        onClick = { onStoryClick(story.storyId) }
                    )
                }
            }
        }
    }
}
