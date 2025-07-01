@file:Suppress("DEPRECATION")

package com.example.mony.feature.utils.navegation

import android.annotation.SuppressLint
import android.util.Log
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
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
import com.example.mony.feature.home.ExpenseDetailScreen
import com.example.mony.feature.home.HomeScreen
import com.example.mony.feature.home.viewmodel.HomeViewModel
import com.example.mony.feature.login.LoginScreen
import com.example.mony.feature.notas.NotaDetalhes
import com.example.mony.feature.notas.NotasScreen
import com.example.mony.feature.notas.NoteEditor
import com.example.mony.feature.notas.viewmodel.NotesViewModel
import com.example.mony.feature.utils.AppState
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions

@SuppressLint("StateFlowValueCalledInComposition")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MyApp(
    notesViewModel: NotesViewModel,
    contaViewModel: ContaViewModel,
    homeViewModel: HomeViewModel
) {
    val navController = rememberNavController()
    val appState = remember { AppState(navController) }

    val context = LocalContext.current
    val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
        .requestEmail()
        .build()
    val googleSignInClient = GoogleSignIn.getClient(context, gso)

    // Configura o NavHost com o NavController e o destino inicial
    NavHost(navController = navController, startDestination = "home") {

        // Tela inicial (Home)
        composable("home") {
            HomeScreen(
                appState,
                homeViewModel= homeViewModel,
                onExpenseClick = { expenseId ->
                    navController.navigate("expenseDetail/${it.id}")
                },
                navController
            )
        }

        composable(
            route = "expenseDetail/{expenseId}",
            arguments = listOf(navArgument("expenseId") { type = NavType.StringType })
        ) { backStackEntry ->
            val expenseId = backStackEntry.arguments?.getString("expenseId") ?: ""

            LaunchedEffect(expenseId) {
                homeViewModel.loadExpenses()
            }

            ExpenseDetailScreen(
                expenseId = expenseId,
                onBack = { navController.popBackStack() },
                homeViewModel = homeViewModel
            )
        }
        // Tela de login
        composable("login") {
            LoginScreen(onGoogleSignInClick = {
                navController.navigate("home") {
                    popUpTo("login") { inclusive = true }
                }
            },
                isLoading = false
            )
        }


        // Tela de notas
        composable("notes") {
            NotasScreen(navController, appState, notesViewModel)
        }

        // Tela de edição de nota
        composable("noteEditor") {
            NoteEditor(navController, notesViewModel)
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
                LoadingScreen()
            } else {
                val note = notesState.find { it.id == noteId }
                Log.d("NotaDetalhes", "Nota encontrada: ${note?.title ?: "NÃO ENCONTRADA"}")

                // Se a nota for encontrada, mostra os detalhes
                if (note != null) {
                    NotaDetalhes(
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
        composable("mais") { ContaScreen(appState, navController, contaViewModel) }
        composable("info") { InfoScreen(navController, contaViewModel, googleSignInClient ) }
        composable("secure") { SecureScreen() }
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