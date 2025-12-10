package edu.ucne.literaverse.presentation.perfil

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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Book
import androidx.compose.material.icons.filled.DeviceUnknown
import androidx.compose.material.icons.filled.ExitToApp
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import edu.ucne.literaverse.domain.model.Session
import edu.ucne.literaverse.presentation.components.BottomNavScreen
import edu.ucne.literaverse.presentation.components.UserMenuBottomBar
import java.text.DecimalFormat
import java.text.SimpleDateFormat
import java.util.Locale

@Composable
fun PerfilScreen(
    viewModel: ProfileViewModel = hiltViewModel(),
    onNavigateToHome: () -> Unit = {},
    onNavigateToBuscar: () -> Unit = {},
    onNavigateToLibrary: () -> Unit = {},
    onNavigateToWrite: () -> Unit = {},
    onLogout: () -> Unit = {}
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(state.userMessage) {
        state.userMessage?.let { message ->
            snackbarHostState.showSnackbar(message)
            viewModel.onEvent(ProfileEvent.UserMessageShown)
        }
    }

    LaunchedEffect(state.isLoggingOut) {
        if (state.isLoggingOut) {
            onLogout()
        }
    }

    if (state.showLogoutAllDialog) {
        LogoutAllDialog(
            onDismiss = { viewModel.dismissLogoutAllDialog() },
            onConfirm = { viewModel.confirmLogoutAll() }
        )
    }

    Scaffold(
        topBar = {
            PerfilTopBar()
        },
        bottomBar = {
            UserMenuBottomBar(
                currentScreen = BottomNavScreen.PERFIL,
                onNavigateToHome = onNavigateToHome,
                onNavigateToBuscar = onNavigateToBuscar,
                onNavigateToLibrary = onNavigateToLibrary,
                onNavigateToWrite = onNavigateToWrite,
                onNavigateToPerfil = {},
                onLogout = {
                    viewModel.onEvent(ProfileEvent.Logout)
                }
            )
        },
        snackbarHost = {
            SnackbarHost(hostState = snackbarHostState)
        }
    ) { padding ->
        PullToRefreshBox(
            isRefreshing = state.isLoading,
            onRefresh = {
                viewModel.onEvent(ProfileEvent.Refresh)
            },
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                item {
                    UserInfoCard(
                        userName = state.userName,
                        userId = state.userId,
                        loginDate = state.loginDate
                    )
                }

                item {
                    if (state.isLoading && state.userProfile == null) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(200.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            CircularProgressIndicator()
                        }
                    } else {
                        state.userProfile?.let { profile ->
                            StatisticsCard(
                                storiesCount = profile.storiesCount,
                                publishedCount = profile.publishedStoriesCount,
                                totalViews = profile.totalViews,
                                favoritesCount = profile.favoritesCount
                            )
                        }
                    }
                }

                item {
                    SessionsCard(
                        sessions = state.sessions,
                        isLoading = state.isLoadingSessions
                    )
                }

                item {
                    AccountActionsCard(
                        onLogoutAll = {
                            viewModel.onEvent(ProfileEvent.LogoutAllSessions)
                        },
                        onLogout = {
                            viewModel.onEvent(ProfileEvent.Logout)
                        },
                        sessionsCount = state.sessions.size
                    )
                }

                item {
                    Spacer(modifier = Modifier.height(16.dp))
                }
            }
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
fun UserInfoCard(
    userName: String,
    userId: Int,
    loginDate: String?
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier.padding(20.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(
                imageVector = Icons.Default.AccountCircle,
                contentDescription = null,
                modifier = Modifier.size(80.dp),
                tint = MaterialTheme.colorScheme.primary
            )

            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = userName,
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onPrimaryContainer
            )

            Text(
                text = "ID: $userId",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.7f)
            )

            loginDate?.let {
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "Última sesión: ${formatDate(it)}",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.6f)
                )
            }
        }
    }
}

@Composable
fun StatisticsCard(
    storiesCount: Int,
    publishedCount: Int,
    totalViews: Int,
    favoritesCount: Int
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.secondaryContainer
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier.padding(20.dp)
        ) {
            Text(
                text = "Estadísticas",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSecondaryContainer
            )

            Spacer(modifier = Modifier.height(16.dp))

            StatisticItem(
                icon = Icons.Default.Book,
                label = "Historias creadas",
                value = formatNumber(storiesCount)
            )

            Spacer(modifier = Modifier.height(12.dp))

            StatisticItem(
                icon = Icons.Default.Lock,
                label = "Historias publicadas",
                value = formatNumber(publishedCount)
            )

            Spacer(modifier = Modifier.height(12.dp))

            StatisticItem(
                icon = Icons.Default.Visibility,
                label = "Total de vistas",
                value = formatNumber(totalViews)
            )

            Spacer(modifier = Modifier.height(12.dp))

            StatisticItem(
                icon = Icons.Default.Favorite,
                label = "Favoritos guardados",
                value = formatNumber(favoritesCount)
            )
        }
    }
}

@Composable
fun StatisticItem(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    label: String,
    value: String
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.weight(1f)
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                modifier = Modifier.size(24.dp),
                tint = MaterialTheme.colorScheme.primary
            )

            Spacer(modifier = Modifier.size(12.dp))

            Text(
                text = label,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSecondaryContainer
            )
        }

        Text(
            text = value,
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.primary
        )
    }
}

@Composable
fun SessionsCard(
    sessions: List<Session>,
    isLoading: Boolean
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.tertiaryContainer
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier.padding(20.dp)
        ) {
            Text(
                text = "Sesiones Activas",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onTertiaryContainer
            )

            Spacer(modifier = Modifier.height(16.dp))

            if (isLoading) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(100.dp),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            } else {
                if (sessions.isEmpty()) {
                    Text(
                        text = "No hay sesiones activas",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onTertiaryContainer.copy(alpha = 0.6f)
                    )
                } else {
                    sessions.forEach { session ->
                        SessionItem(session = session)
                        if (session != sessions.last()) {
                            Spacer(modifier = Modifier.height(12.dp))
                            Divider()
                            Spacer(modifier = Modifier.height(12.dp))
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun SessionItem(session: Session) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = Icons.Default.DeviceUnknown,
            contentDescription = null,
            modifier = Modifier.size(28.dp),
            tint = MaterialTheme.colorScheme.tertiary
        )

        Spacer(modifier = Modifier.size(12.dp))

        Column(
            modifier = Modifier.weight(1f)
        ) {
            Text(
                text = session.deviceInfo ?: "Dispositivo desconocido",
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.Medium,
                color = MaterialTheme.colorScheme.onTertiaryContainer
            )

            Text(
                text = "Creada: ${formatDate(session.createdAt)}",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onTertiaryContainer.copy(alpha = 0.6f)
            )

            session.lastActivity?.let {
                Text(
                    text = "Última actividad: ${formatDate(it)}",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onTertiaryContainer.copy(alpha = 0.6f)
                )
            }
        }
    }
}

@Composable
fun AccountActionsCard(
    onLogoutAll: () -> Unit,
    onLogout: () -> Unit,
    sessionsCount: Int
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.errorContainer
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier.padding(20.dp)
        ) {
            Text(
                text = "Opciones de Cuenta",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onErrorContainer
            )

            Spacer(modifier = Modifier.height(16.dp))

            OutlinedButton(
                onClick = onLogout,
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.ExitToApp,
                    contentDescription = null,
                    modifier = Modifier.size(20.dp)
                )
                Spacer(modifier = Modifier.size(8.dp))
                Text("Cerrar Sesión Actual")
            }

            if (sessionsCount > 1) {
                Spacer(modifier = Modifier.height(12.dp))

                OutlinedButton(
                    onClick = onLogoutAll,
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.ExitToApp,
                        contentDescription = null,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.size(8.dp))
                    Text("Cerrar Todas las Sesiones ($sessionsCount)")
                }
            }
        }
    }
}

@Composable
fun LogoutAllDialog(
    onDismiss: () -> Unit,
    onConfirm: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        icon = {
            Icon(
                imageVector = Icons.Default.ExitToApp,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.error
            )
        },
        title = {
            Text(
                text = "Cerrar todas las sesiones",
                style = MaterialTheme.typography.titleLarge
            )
        },
        text = {
            Text(
                text = "¿Estás seguro de que deseas cerrar todas las sesiones activas? Esto incluye la sesión actual y serás redirigido al inicio de sesión.",
                style = MaterialTheme.typography.bodyMedium
            )
        },
        confirmButton = {
            TextButton(onClick = onConfirm) {
                Text("Cerrar todas")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancelar")
            }
        }
    )
}

private fun formatNumber(number: Int): String {
    val formatter = DecimalFormat("#,###")
    return formatter.format(number)
}

private fun formatDate(dateString: String): String {
    return try {
        val inputFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.getDefault())
        val outputFormat = SimpleDateFormat("dd MMM yyyy, HH:mm", Locale("es", "ES"))
        val date = inputFormat.parse(dateString)
        date?.let { outputFormat.format(it) } ?: dateString
    } catch (e: Exception) {
        dateString
    }
}

@Preview(showBackground = true)
@Composable
private fun PerfilTopBarPreview() {
    MaterialTheme {
        PerfilTopBar()
    }
}

@Preview(showBackground = true)
@Composable
private fun UserInfoCardPreview() {
    MaterialTheme {
        UserInfoCard(
            userName = "usuario_demo",
            userId = 123,
            loginDate = "2024-12-01T10:30:00"
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun StatisticsCardPreview() {
    MaterialTheme {
        StatisticsCard(
            storiesCount = 15,
            publishedCount = 10,
            totalViews = 5000,
            favoritesCount = 25
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun SessionsCardPreview() {
    MaterialTheme {
        SessionsCard(
            sessions = listOf(
                Session(
                    sessionId = 1,
                    userId = 1,
                    token = "token",
                    deviceInfo = "Chrome en Windows",
                    createdAt = "2024-12-01T10:00:00",
                    lastActivity = "2024-12-03T15:30:00",
                    isActive = true
                )
            ),
            isLoading = false
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun LogoutAllDialogPreview() {
    MaterialTheme {
        LogoutAllDialog(
            onDismiss = {},
            onConfirm = {}
        )
    }
}