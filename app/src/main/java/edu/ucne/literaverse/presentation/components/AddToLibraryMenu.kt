package edu.ucne.literaverse.presentation.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.MenuBook
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Checkbox
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

data class LibraryStates(
    val isFavorite: Boolean = false,
    val isReading: Boolean = false,
    val isCompleted: Boolean = false
)

@Composable
fun AddToLibraryMenu(
    currentStates: LibraryStates,
    onDismiss: () -> Unit,
    onSave: (LibraryStates) -> Unit
) {
    var isFavorite by remember { mutableStateOf(currentStates.isFavorite) }
    var isReading by remember { mutableStateOf(currentStates.isReading) }
    var isCompleted by remember { mutableStateOf(currentStates.isCompleted) }

    AlertDialog(
        onDismissRequest = onDismiss,
        icon = {
            Icon(
                imageVector = Icons.Default.MenuBook,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary
            )
        },
        title = {
            Text(
                text = "Agregar a Biblioteca",
                style = MaterialTheme.typography.titleLarge
            )
        },
        text = {
            Column {
                LibraryOption(
                    icon = Icons.Default.Favorite,
                    label = "Favoritos",
                    checked = isFavorite,
                    onCheckedChange = { isFavorite = it }
                )

                LibraryOption(
                    icon = Icons.Default.MenuBook,
                    label = "Leyendo",
                    checked = isReading,
                    onCheckedChange = { isReading = it }
                )

                LibraryOption(
                    icon = Icons.Default.CheckCircle,
                    label = "Finalizadas",
                    checked = isCompleted,
                    onCheckedChange = { isCompleted = it }
                )
            }
        },
        confirmButton = {
            TextButton(
                onClick = {
                    onSave(LibraryStates(isFavorite, isReading, isCompleted))
                    onDismiss()
                }
            ) {
                Text("Guardar")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancelar")
            }
        }
    )
}

@Composable
private fun LibraryOption(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    label: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = if (checked) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant
        )

        Spacer(modifier = Modifier.width(16.dp))

        Text(
            text = label,
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurface,
            modifier = Modifier.weight(1f)
        )

        Checkbox(
            checked = checked,
            onCheckedChange = onCheckedChange
        )
    }
}