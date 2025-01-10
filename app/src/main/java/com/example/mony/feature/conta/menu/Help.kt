package com.example.mony.feature.conta.menu

import androidx.compose.foundation.layout.*
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.compose.rememberNavController

@Composable
fun HelpScreen() {
    Surface {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(text = "Tela de Ajuda", fontSize = 24.sp)
            Text(text = "Aqui você encontrará informações sobre ajuda.", fontSize = 16.sp)
        }
    }
}

@Preview
@Composable
fun HelpScreenPreview() {
    val navController = rememberNavController()
    HelpScreen()
}