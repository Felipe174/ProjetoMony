package com.example.mony.feature.utils.navegation

import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
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
    // Cria um NavController para gerenciar a navegação
    val navController = rememberNavController()
    val appState = remember { AppState(navController) }
    val notesViewModel: NotesViewModel = viewModel()

    // Configura o NavHost com o NavController e o destino inicial
    NavHost(navController = navController, startDestination = "home") {
        // Tela inicial (Home)
        composable("home") { HomeScreen(appState) }

        // Tela de notas
        composable("notes") {
            NotasScreen(navController, appState, notesViewModel)
        }

        // Tela de edição de nota
        composable("noteEditor") {
            NoteEditor(navController, appState, notesViewModel)
        }

        // Tela de detalhes da nota
        composable(
            route = "notaDetalhes/{noteId}",
            arguments = listOf(navArgument("noteId") { type = NavType.StringType })
        ) { backStackEntry ->
            val noteId = backStackEntry.arguments?.getString("noteId")
            val notesState = notesViewModel.notes.collectAsState().value
            val note = notesState.find { it.id == noteId }
            if (note != null) {
                NotaDetalhes(
                    title = note.title,
                    content = note.content,
                    navController = navController,
                    notesViewModel = notesViewModel,
                    noteId = note.id
                )
            }
        }

        // Outras telas
        composable("mais") { ContaScreen(appState, navController, onLogout = {}) }
        composable("info") { InfoScreen(navController) }
        composable("secure") { SecureScreen() }
        composable("help") { HelpScreen() }
        composable("about") { AboutScreen() }
    }
}