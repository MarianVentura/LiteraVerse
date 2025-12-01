package edu.ucne.literaverse.presentation.library

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import edu.ucne.literaverse.presentation.components.UserMenuBottomBar
import edu.ucne.literaverse.presentation.components.BottomNavScreen

@Composable
fun LibraryScreen(
    onNavigateToHome: () -> Unit = {},
    onNavigateToBuscar: () -> Unit = {},
    onNavigateToWrite: () -> Unit = {},
    onNavigateToPerfil: () -> Unit = {},
    onLogout: () -> Unit = {}
) {
    Scaffold(
        topBar = {
            LibraryTopBar()
        },
        bottomBar = {
            UserMenuBottomBar(
                currentScreen = BottomNavScreen.LIBRARY,
                onNavigateToHome = onNavigateToHome,
                onNavigateToBuscar = onNavigateToBuscar,
                onNavigateToLibrary = {},
                onNavigateToWrite = onNavigateToWrite,
                onNavigateToPerfil = onNavigateToPerfil,
                onLogout = onLogout
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

