package com.example.mony.feature.utils.navegation

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
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
import com.example.mony.feature.conta.viewmodel.ContaViewModel
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
    val contaViewModel: ContaViewModel = viewModel()

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
            val noteId = backStackEntry.arguments?.getString("noteId") ?: return@composable
            val notesState by notesViewModel.notes.collectAsState()

            // Exibe um indicador de carregamento até que o estado das notas seja carregado
            if (notesState.isEmpty()) {
                // Exibe um indicador de carregamento
                LoadingScreen()
            } else {
                val note = notesState.find { it.id == noteId }

                // Se a nota for encontrada, mostra os detalhes
                if (note != null) {
                    NotaDetalhes(
                        title = note.title,
                        content = note.content,
                        navController = navController,
                        notesViewModel = notesViewModel,
                        noteId = note.id
                    )
                } else {
                    // Se a nota não for encontrada, mostra uma mensagem de erro
                    NotFoundScreen(navController)
                }
            }
        }

        // Outras telas
        composable("mais") { ContaScreen(appState, navController, contaViewModel, onLogout = {}) }
        composable("info") { InfoScreen(navController) }
        composable("secure") { SecureScreen(navController) }
        composable("help") { HelpScreen(navController) }
        composable("about") { AboutScreen(navController) }
    }
}

@Composable
fun LoadingScreen() {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        CircularProgressIndicator()
    }
}

@Composable
fun NotFoundScreen(navController: NavController) {
    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(text = "Nota não encontrada")
        Spacer(modifier = Modifier.height(16.dp))
        Button(onClick = { navController.popBackStack() }) {
            Text("Voltar")
        }
    }
}