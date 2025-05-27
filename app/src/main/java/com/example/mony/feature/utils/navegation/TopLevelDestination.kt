package com.example.mony.feature.utils.navegation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.MoreHoriz
import androidx.compose.material.icons.filled.NoteAlt
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.MoreHoriz
import androidx.compose.material.icons.outlined.NoteAlt
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import com.example.mony.R

data class TopLevelDestination(
    val route: String,
    val selectedIcon: ImageVector,
    val unselectedIcon: ImageVector,
    val iconTextId: Int,
    val selectedIconColor: Color,
    val unselectedIconColor: Color,
    val textColor: Color,
    val backgroundColor: Color,
)

sealed class Destinations(val route: String) {
    object Home : Destinations("home")
    object Notes : Destinations("notes")
    object More : Destinations("mais")
}

@Composable
fun getTopLevelDestinations(): List<TopLevelDestination> {
    return listOf(
        TopLevelDestination(
            route = Destinations.Home.route,
            selectedIcon = Icons.Filled.Home,
            unselectedIcon = Icons.Outlined.Home,
            iconTextId = R.string.Home,
            selectedIconColor = MaterialTheme.colorScheme.primary,
            unselectedIconColor = Color(0xFF414141),
            textColor = Color.Black,
            backgroundColor = Color.White
        ),
        TopLevelDestination(
            route = Destinations.Notes.route,
            selectedIcon = Icons.Filled.NoteAlt,
            unselectedIcon = Icons.Outlined.NoteAlt,
            iconTextId = R.string.Notas,
            selectedIconColor = MaterialTheme.colorScheme.primary,
            unselectedIconColor = Color(0xFF414141),
            textColor = Color.Black,
            backgroundColor = Color.White
        ),
        TopLevelDestination(
            route = Destinations.More.route,
            selectedIcon = Icons.Filled.MoreHoriz,
            unselectedIcon = Icons.Outlined.MoreHoriz,
            iconTextId = R.string.More,
            selectedIconColor = MaterialTheme.colorScheme.primary,
            unselectedIconColor = Color(0xFF414141),
            textColor = Color.Black,
            backgroundColor = Color.White
        )
    )
}