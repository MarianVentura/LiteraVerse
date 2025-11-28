package edu.ucne.literaverse.presentation.library

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight

@Composable
fun LibraryScreen(
    onNavigateToHome: () -> Unit = {},
    onNavigateToBuscar: () -> Unit = {},
    onNavigateToWrite: () -> Unit = {},
    onNavigateToPerfil: () -> Unit = {}
) {
    Scaffold(
        topBar = {
            LibraryTopBar()
        },
        bottomBar = {
            LibraryBottomBar(
                onNavigateToHome = onNavigateToHome,
                onNavigateToBuscar = onNavigateToBuscar,
                onNavigateToWrite = onNavigateToWrite,
                onNavigateToPerfil = onNavigateToPerfil
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
                text = "Pantalla de Biblioteca (Preview)",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LibraryTopBar() {
    TopAppBar(
        title = {
            Text(
                text = "Mi Biblioteca",
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
fun LibraryBottomBar(
    onNavigateToHome: () -> Unit,
    onNavigateToBuscar: () -> Unit,
    onNavigateToWrite: () -> Unit,
    onNavigateToPerfil: () -> Unit
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
            selected = true,
            onClick = {}
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
            selected = false,
            onClick = onNavigateToPerfil
        )
    }
}