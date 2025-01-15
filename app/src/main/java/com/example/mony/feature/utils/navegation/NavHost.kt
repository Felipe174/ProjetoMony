package com.example.mony.feature.utils.navegation

import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.mony.feature.conta.ContaScreen
import com.example.mony.feature.conta.menu.AboutScreen
import com.example.mony.feature.conta.menu.HelpScreen
import com.example.mony.feature.conta.menu.InfoScreen
import com.example.mony.feature.conta.menu.SecureScreen
import com.example.mony.feature.home.HomeScreen
import com.example.mony.feature.notas.NotaDetalhes
import com.example.mony.feature.notas.NotasScreen
import com.example.mony.feature.notas.NoteEditor
import com.example.mony.feature.notas.viewmodel.NotesViewModel
import com.example.mony.feature.utils.AppState

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MyApp() {
    val navController = rememberNavController()
    val appState = remember { AppState(navController) }
    val notesViewModel: NotesViewModel = viewModel()

    NavHost(navController = navController, startDestination = "home") {
        //home
        composable("home") { HomeScreen(appState) }

        //notes
        composable(Destinations.NOTES) {
            NotasScreen(navController, appState, notesViewModel)
        }
        composable("noteEditor") {
            NoteEditor(navController, appState, notesViewModel)
        }
        composable(
            route = "notaDetalhes/{noteIndex}",
            arguments = listOf(navArgument("noteIndex") { type = NavType.IntType })
        ) { backStackEntry ->
            val noteIndex = backStackEntry.arguments?.getInt("noteIndex") ?: 0
            val note = if (noteIndex in notesViewModel.notes.indices) {
                notesViewModel.notes[noteIndex]
            } else {
                return@composable
            }
            NotaDetalhes(
                title = note.title,
                content = note.content,
                navController = navController,
                notesViewModel = notesViewModel,
                noteIndex = noteIndex
            )
        }


        //Mais
        composable("mais") { ContaScreen(appState, navController, onLogout = {})  }
        composable("info") { InfoScreen(navController) }
        composable("secure") { SecureScreen() }
        composable("help") { HelpScreen() }
        composable("about") { AboutScreen() }
    }
}
