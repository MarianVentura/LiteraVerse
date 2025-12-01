package edu.ucne.literaverse.presentation.components

import androidx.compose.foundation.layout.Box
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier

enum class BottomNavScreen {
    HOME,
    SEARCH,
    LIBRARY,
    WRITE,
    PERFIL
}

@Composable
fun UserMenuBottomBar(
    currentScreen: BottomNavScreen,
    onNavigateToHome: () -> Unit,
    onNavigateToBuscar: () -> Unit,
    onNavigateToLibrary: () -> Unit,
    onNavigateToWrite: () -> Unit,
    onNavigateToPerfil: () -> Unit,
    onLogout: () -> Unit
) {
    var showPerfilMenu by remember { mutableStateOf(false) }

    Box {
        NavigationBar(
            containerColor = MaterialTheme.colorScheme.surface
        ) {
            NavigationBarItem(
                icon = { Icon(Icons.Default.Home, contentDescription = null) },
                label = { Text("Inicio", style = MaterialTheme.typography.labelSmall) },
                selected = currentScreen == BottomNavScreen.HOME,
                onClick = onNavigateToHome
            )
            NavigationBarItem(
                icon = { Icon(Icons.Default.Search, contentDescription = null) },
                label = { Text("Buscar", style = MaterialTheme.typography.labelSmall) },
                selected = currentScreen == BottomNavScreen.SEARCH,
                onClick = onNavigateToBuscar
            )
            NavigationBarItem(
                icon = { Icon(Icons.Default.List, contentDescription = null) },
                label = { Text("Biblioteca", style = MaterialTheme.typography.labelSmall) },
                selected = currentScreen == BottomNavScreen.LIBRARY,
                onClick = onNavigateToLibrary
            )
            NavigationBarItem(
                icon = { Icon(Icons.Default.Edit, contentDescription = null) },
                label = { Text("Escribir", style = MaterialTheme.typography.labelSmall) },
                selected = currentScreen == BottomNavScreen.WRITE,
                onClick = onNavigateToWrite
            )
            NavigationBarItem(
                icon = { Icon(Icons.Default.Person, contentDescription = null) },
                label = { Text("Perfil", style = MaterialTheme.typography.labelSmall) },
                selected = currentScreen == BottomNavScreen.PERFIL,
                onClick = { showPerfilMenu = !showPerfilMenu }
            )
        }

        DropdownMenu(
            expanded = showPerfilMenu,
            onDismissRequest = { showPerfilMenu = false },
            modifier = Modifier.align(Alignment.BottomEnd)
        ) {
            DropdownMenuItem(
                text = { Text("Ver Perfil") },
                onClick = {
                    showPerfilMenu = false
                    onNavigateToPerfil()
                },
                leadingIcon = {
                    Icon(Icons.Default.Person, contentDescription = null)
                }
            )
            DropdownMenuItem(
                text = { Text("Cerrar Sesión") },
                onClick = {
                    showPerfilMenu = false
                    onLogout()
                },
                leadingIcon = {
                    Icon(Icons.Default.ExitToApp, contentDescription = null)
                }
            )
        }
    }
}