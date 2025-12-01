package edu.ucne.literaverse.presentation.search

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
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
import edu.ucne.literaverse.presentation.components.UserMenuBottomBar
import edu.ucne.literaverse.presentation.components.BottomNavScreen
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.AsyncImage
import edu.ucne.literaverse.domain.model.EstadoNovel
import edu.ucne.literaverse.domain.model.Novel
import edu.ucne.literaverse.domain.model.OrdenCriterio

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchScreen(
    viewModel: SearchViewModel = hiltViewModel(),
    onNovelClick: (Int) -> Unit = {},
    onNavigateToHome: () -> Unit = {},
    onNavigateToLibrary: () -> Unit = {},
    onNavigateToWrite: () -> Unit = {},
    onNavigateToPerfil: () -> Unit = {},
    onLogout: () -> Unit = {}
) {
    Scaffold(
        topBar = {
            SearchTopBar()
        },
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
    onNavigateToPerfil: () -> Unit = {}

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
        snackbarHost = { SnackbarHost(snackbarHostState) },
        containerColor = MaterialTheme.colorScheme.background
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            // Header con barra de búsqueda
            SearchHeader(
                query = state.searchQuery,
                onQueryChange = { viewModel.onEvent(SearchEvent.OnQueryChange(it)) },
                onSearch = { viewModel.onEvent(SearchEvent.OnSearch) },
                onToggleFilters = { viewModel.onEvent(SearchEvent.ToggleFilters) },
                showFilters = state.showFilters
            )

            // Panel de filtros expandible
            AnimatedVisibility(
                visible = state.showFilters,
                enter = expandVertically(),
                exit = shrinkVertically()
            ) {
                FiltersPanel(
                    selectedGenre = state.selectedGenre,
                    selectedCategory = state.selectedCategory,
                    selectedEstado = state.selectedEstado,
                    selectedOrden = state.selectedOrden,
                    onGenreSelect = { viewModel.onEvent(SearchEvent.OnGenreSelect(it)) },
                    onCategorySelect = { viewModel.onEvent(SearchEvent.OnCategorySelect(it)) },
                    onEstadoSelect = { viewModel.onEvent(SearchEvent.OnEstadoSelect(it)) },
                    onOrdenChange = { viewModel.onEvent(SearchEvent.OnOrdenChange(it)) },
                    onClearFilters = { viewModel.onEvent(SearchEvent.ClearFilters) }
                )
            }

            // Resultados
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .weight(1f)
            ) {
                when {
                    state.isLoading -> {
                        CircularProgressIndicator(
                            modifier = Modifier.align(Alignment.Center),
                            color = MaterialTheme.colorScheme.primary
                        )
                    }

                    state.novels.isEmpty() && state.hasSearched -> {
                        EmptySearchResults(
                            modifier = Modifier.align(Alignment.Center)
                        )
                    }

                    else -> {
                        NovelsList(
                            novels = state.novels,
                            onNovelClick = {
                                viewModel.onEvent(SearchEvent.OnNovelClick(it))
                                onNovelClick(it)
                            }
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun SearchHeader(
    query: String,
    onQueryChange: (String) -> Unit,
    onSearch: () -> Unit,
    onToggleFilters: () -> Unit,
    showFilters: Boolean
) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        color = MaterialTheme.colorScheme.surface,
        shadowElevation = 4.dp
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Text(
                text = "Buscar",
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface,
                modifier = Modifier.padding(bottom = 12.dp)
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Barra de búsqueda
                OutlinedTextField(
                    value = query,
                    onValueChange = onQueryChange,
                    modifier = Modifier.weight(1f),
                    placeholder = {
                        Text(
                            "Buscar historias, autores...",
                            style = MaterialTheme.typography.bodyMedium
                        )
                    },
                    leadingIcon = {
                        Icon(
                            imageVector = Icons.Default.Search,
                            contentDescription = "Buscar",
                            tint = MaterialTheme.colorScheme.primary
                        )
                    },
                    trailingIcon = {
                        if (query.isNotEmpty()) {
                            IconButton(onClick = { onQueryChange("") }) {
                                Icon(
                                    imageVector = Icons.Default.Close,
                                    contentDescription = "Limpiar"
                                )
                            }
                        }
                    },
                    shape = RoundedCornerShape(24.dp),
                    singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = MaterialTheme.colorScheme.primary,
                        unfocusedBorderColor = MaterialTheme.colorScheme.outline
                    )
                )

                // Botón de filtros
                FilledTonalIconButton(
                    onClick = onToggleFilters,
                    modifier = Modifier.size(56.dp),
                    colors = IconButtonDefaults.filledTonalIconButtonColors(
                        containerColor = if (showFilters)
                            MaterialTheme.colorScheme.primaryContainer
                        else
                            MaterialTheme.colorScheme.surfaceVariant
                    )
                ) {
                    Icon(
                        imageVector = Icons.Default.FilterList,
                        contentDescription = "Filtros",
                        tint = if (showFilters)
                            MaterialTheme.colorScheme.onPrimaryContainer
                        else
                            MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }
    }
}

@Composable
private fun FiltersPanel(
    selectedGenre: String?,
    selectedCategory: String?,
    selectedEstado: EstadoNovel?,
    selectedOrden: OrdenCriterio,
    onGenreSelect: (String?) -> Unit,
    onCategorySelect: (String?) -> Unit,
    onEstadoSelect: (EstadoNovel?) -> Unit,
    onOrdenChange: (OrdenCriterio) -> Unit,
    onClearFilters: () -> Unit
) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Header de filtros
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Filtros",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.onSurface
                )

                TextButton(onClick = onClearFilters) {
                    Text(
                        "Limpiar todo",
                        color = MaterialTheme.colorScheme.primary
                    )
                }
            }

            // Género
            FilterSection(
                title = "Género",
                items = listOf("Todos", "Fantasía", "Romance", "Ciencia Ficción", "Misterio", "Drama", "Thriller"),
                selectedItem = selectedGenre,
                onItemSelect = { onGenreSelect(if (it == "Todos") null else it) }
            )

            // Estado
            FilterSection(
                title = "Estado",
                items = listOf("Todos") + EstadoNovel.entries.map { it.displayName },
                selectedItem = selectedEstado?.displayName,
                onItemSelect = {
                    onEstadoSelect(
                        if (it == "Todos") null
                        else EstadoNovel.entries.find { estado -> estado.displayName == it }
                    )
                }
            )

            // Ordenar por
            FilterSection(
                title = "Ordenar por",
                items = OrdenCriterio.entries.map { it.displayName },
                selectedItem = selectedOrden.displayName,
                onItemSelect = { displayName ->
                    OrdenCriterio.entries.find { it.displayName == displayName }
                        ?.let { onOrdenChange(it) }
                }
            )
        }
    }
}

@Composable
private fun FilterSection(
    title: String,
    items: List<String>,
    selectedItem: String?,
    onItemSelect: (String) -> Unit
) {
    Column(
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Text(
            text = title,
            style = MaterialTheme.typography.labelLarge,
            fontWeight = FontWeight.Medium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        LazyRow(
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(items) { item ->
                FilterChip(
                    selected = item == selectedItem || (selectedItem == null && item == "Todos"),
                    onClick = { onItemSelect(item) },
                    label = { Text(item) },
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = MaterialTheme.colorScheme.primaryContainer,
                        selectedLabelColor = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                )
            }
        }
    }
}

@Composable
private fun NovelsList(
    novels: List<Novel>,
    onNovelClick: (Int) -> Unit
) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        item {
            Text(
                text = "${novels.size} resultado${if (novels.size != 1) "s" else ""}",
                style = MaterialTheme.typography.titleSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(bottom = 8.dp)
            )
        }

        items(
            items = novels,
            key = { it.id }
        ) { novel ->
            NovelCard(
                novel = novel,
                onClick = { onNovelClick(novel.id) }
            )
        }
    }
}

@Composable
private fun NovelCard(
    novel: Novel,
    onClick: () -> Unit
) {
    ElevatedCard(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(12.dp),
        elevation = CardDefaults.elevatedCardElevation(defaultElevation = 2.dp),
        colors = CardDefaults.elevatedCardColors(
            containerColor = MaterialTheme.colorScheme.surface
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Portada
            AsyncImage(
                model = novel.portada,
                contentDescription = novel.titulo,
                modifier = Modifier
                    .width(80.dp)
                    .height(120.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .background(MaterialTheme.colorScheme.surfaceVariant),
                contentScale = ContentScale.Crop
            )

            // Información
            Column(
                modifier = Modifier
                    .weight(1f)
                    .height(120.dp),
                verticalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                Text(
                    text = novel.titulo,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis,
                    color = MaterialTheme.colorScheme.onSurface
                )

                Text(
                    text = "por ${novel.autor}",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                // Género tag
                novel.genero?.let {
                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = MaterialTheme.colorScheme.secondaryContainer
                    ) {
                        Text(
                            text = it,
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSecondaryContainer,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.weight(1f))

                // Stats
                Row(
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    StatItem(
                        icon = Icons.Default.Visibility,
                        value = formatNumber(novel.vistas)
                    )
                    StatItem(
                        icon = Icons.Default.Favorite,
                        value = formatNumber(novel.likes)
                    )
                    StatItem(
                        icon = Icons.Default.Book,
                        value = "${novel.capitulos}"
                    )
                }

                // Estado
                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = when (novel.estado) {
                        "Completa" -> MaterialTheme.colorScheme.tertiaryContainer
                        "En progreso" -> MaterialTheme.colorScheme.primaryContainer
                        else -> MaterialTheme.colorScheme.surfaceVariant
                    }
                ) {
                    Text(
                        text = novel.estado,
                        style = MaterialTheme.typography.labelSmall,
                        color = when (novel.estado) {
                            "Completa" -> MaterialTheme.colorScheme.onTertiaryContainer
                            "En progreso" -> MaterialTheme.colorScheme.onPrimaryContainer
                            else -> MaterialTheme.colorScheme.onSurfaceVariant
                        },
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp)
                    )
                }
            }
        }
    }
}

@Composable
private fun StatItem(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    value: String
) {
    Row(
        horizontalArrangement = Arrangement.spacedBy(4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            modifier = Modifier.size(14.dp),
            tint = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Text(
            text = value,
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

@Composable
private fun EmptySearchResults(
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Icon(
            imageVector = Icons.Default.SearchOff,
            contentDescription = null,
            modifier = Modifier.size(64.dp),
            tint = MaterialTheme.colorScheme.onSurfaceVariant
        )

        Text(
            text = "No se encontraron resultados",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Medium,
            color = MaterialTheme.colorScheme.onSurface
        )

        Text(
            text = "Intenta con otros términos de búsqueda o filtros",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

private fun formatNumber(number: Int): String {
    return when {
        number >= 1_000_000 -> "${number / 1_000_000}M"
        number >= 1_000 -> "${number / 1_000}K"
        else -> number.toString()
    }
}
