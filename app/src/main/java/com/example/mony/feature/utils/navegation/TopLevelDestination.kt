package com.example.mony.feature.utils.navegation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.MoreHoriz
import androidx.compose.material.icons.filled.NoteAlt
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.MoreHoriz
import androidx.compose.material.icons.outlined.NoteAlt
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import com.example.mony.R


data class TopLevelDestination(
    val route: String,
    val selectedIcon: ImageVector,
    val unselectedIcon: ImageVector,
    val iconTextId: Int,
    val selectedIconColor: Color, // Cor do ícone selecionado
    val unselectedIconColor: Color, // Cor do ícone não selecionado
    val textColor: Color // Cor do texto

)

// Definindo constantes para as rotas
object Destinations {
    const val HOME = "home"
    const val NOTES = "notes"
    const val MORE = "mais"
}

// Exemplo de como você pode definir seus destinos
val topLevelDestinations = listOf(
    TopLevelDestination(
        route = Destinations.HOME,
        selectedIcon = Icons.Filled.Home,
        unselectedIcon = Icons.Outlined.Home,
        iconTextId = R.string.Home, // ID do recurso de string
        selectedIconColor = Color(0xFF8550E0), // Cor do ícone selecionado
        unselectedIconColor = Color(0xFF414141), // Cor do ícone não selecionado
        textColor = Color.Black, // Cor do texto

    ),
    TopLevelDestination(
        route = Destinations.NOTES,
        selectedIcon = Icons.Filled.NoteAlt,
        unselectedIcon = Icons.Outlined.NoteAlt,
        iconTextId = R.string.Notas, // ID do recurso de string
        selectedIconColor = Color(0xFF8550E0), // Cor do ícone selecionado
        unselectedIconColor = Color(0xFF414141), // Cor do ícone não selecionado
        textColor = Color.Black // Cor do texto
    ),
    TopLevelDestination(
        route = Destinations.MORE,
        selectedIcon = Icons.Filled.MoreHoriz,
        unselectedIcon = Icons.Outlined.MoreHoriz,
        iconTextId = R.string.More, // Certifique-se de que este ID de string existe
        selectedIconColor = Color(0xFF8550E0), // Cor do ícone selecionado
        unselectedIconColor = Color(0xFF414141), // Cor do ícone não selecionado
        textColor = Color.Black // Cor do texto
    ),
)