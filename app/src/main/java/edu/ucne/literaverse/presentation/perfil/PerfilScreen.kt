package edu.ucne.literaverse.presentation.perfil

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight

@Composable
fun PerfilScreen(
    onNavigateToHome: () -> Unit = {},
    onNavigateToBuscar: () -> Unit = {},
    onNavigateToLibrary: () -> Unit = {},
    onNavigateToWrite: () -> Unit = {}
) {
    Scaffold(
        topBar = {
            PerfilTopBar()
        },
        bottomBar = {
            PerfilBottomBar(
                onNavigateToHome = onNavigateToHome,
                onNavigateToBuscar = onNavigateToBuscar,
                onNavigateToLibrary = onNavigateToLibrary,
                onNavigateToWrite = onNavigateToWrite
            )
        }
    ) { padding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "Pantalla de Perfil (Preview)",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PerfilTopBar() {
    TopAppBar(
        title = {
            Text(
                text = "Mi Perfil",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold
            )
        },
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = MaterialTheme.colorScheme.surface
        )
    )
}

@Composable
fun PerfilBottomBar(
    onNavigateToHome: () -> Unit,
    onNavigateToBuscar: () -> Unit,
    onNavigateToLibrary: () -> Unit,
    onNavigateToWrite: () -> Unit
) {
    NavigationBar(
        containerColor = MaterialTheme.colorScheme.surface
    ) {
        NavigationBarItem(
            icon = { Icon(Icons.Default.Home, contentDescription = null) },
            label = { Text("Inicio", style = MaterialTheme.typography.labelSmall) },
            selected = false,
            onClick = onNavigateToHome
        )
        NavigationBarItem(
            icon = { Icon(Icons.Default.Search, contentDescription = null) },
            label = { Text("Buscar", style = MaterialTheme.typography.labelSmall) },
            selected = false,
            onClick = onNavigateToBuscar
        )
        NavigationBarItem(
            icon = { Icon(Icons.Default.List, contentDescription = null) },
            label = { Text("Biblioteca", style = MaterialTheme.typography.labelSmall) },
            selected = false,
            onClick = onNavigateToLibrary
        )
        NavigationBarItem(
            icon = { Icon(Icons.Default.Edit, contentDescription = null) },
            label = { Text("Escribir", style = MaterialTheme.typography.labelSmall) },
            selected = false,
            onClick = onNavigateToWrite
        )
        NavigationBarItem(
            icon = { Icon(Icons.Default.Person, contentDescription = null) },
            label = { Text("Perfil", style = MaterialTheme.typography.labelSmall) },
            selected = true,
            onClick = {}
        )
    }
}