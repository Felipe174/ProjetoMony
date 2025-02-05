package com.example.mony.feature.utils

import android.util.Log
import androidx.navigation.NavController

class AppState(private val navController: NavController) {

    companion object {
        // Definindo constantes para as rotas
        //notes
        const val ROUTE_NOTES = "notes"
        const val ROUTE_ARQUIVOS_SCREEN = "arquivosScreen"
        const val ROUTE_NOTA_DETALHES = "notaDetalhes/{noteId}"

        //Mais
        const val ROUTE_MAIS = "mais"
        const val ROUTE_INFO = "info"
        const val ROUTE_SECURE = "secure"
        const val ROUTE_HELP = "help"
        const val ROUTE_ABOUT = "about"

        //Home
        const val ROUTE_HOME = "home"


        // Conjunto de rotas válidas
        private val validRoutes = setOf(
            ROUTE_NOTES,
            ROUTE_ARQUIVOS_SCREEN,
            ROUTE_NOTA_DETALHES,
            ROUTE_MAIS,
            ROUTE_HOME,
            ROUTE_INFO,
            ROUTE_SECURE,
            ROUTE_HELP,
            ROUTE_ABOUT

        )
    }

    // Método para navegação para a tela de destino
    fun navigateToTopLevelDestination(route: String) {
        if (isValidRoute(route)) { // Verifique se a rota é válida
            navController.navigate(route) {
                launchSingleTop = true // Evita duplicação de telas
                restoreState = true    // Restaura estado anterior
                popUpTo(ROUTE_HOME) { inclusive = true } // Use a constante para a rota home
            }
        } else {
            // Lidar com rota inválida, se necessário
            Log.e("AppState", "Rota inválida: $route")
        }
    }

    // Verifica se a rota está presente na hierarquia
    fun isRouteInHierarchy(route: String): Boolean {
        return navController.currentBackStackEntry?.destination?.route == route
    }

    // Verifica se a rota é válida
    private fun isValidRoute(route: String): Boolean {
        return route in validRoutes
    }
}