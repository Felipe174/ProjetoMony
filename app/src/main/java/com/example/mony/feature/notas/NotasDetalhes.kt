package com.example.mony.feature.notas

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.example.mony.feature.notas.classe.NotaItem
import com.example.mony.feature.notas.viewmodel.NotesViewModel
import com.example.mony.ui.theme.MonyTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NotaDetalhes(
    title: String,
    content: String,
    navController: NavController,
    notesViewModel: NotesViewModel,
    noteId: String,
) {
    var editableTitle by remember { mutableStateOf(title) }
    var editableContent by remember { mutableStateOf(content) }
    var isModified by remember { mutableStateOf(false) }
    var lastModified by remember { mutableStateOf(getCurrentDateTime()) }

    // Atualiza a data de modificação a cada segundo enquanto estiver editando
    LaunchedEffect(isModified) {
        if (isModified) {
            lastModified = getCurrentDateTime()
        }
    }

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
        ) {
            TopAppBar(
                title = { Text("Detalhes da Nota", modifier = Modifier.padding(start = 12.dp)) },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Voltar")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(MaterialTheme.colorScheme.onPrimary)
            )

            Spacer(modifier = Modifier.height(10.dp))

            // Exibe a data de modificação
            Text(
                text = "Última alteração foi em $lastModified",
                color = Color.Gray,
                modifier = Modifier.padding(2.dp).align(Alignment.End)
            )

            Spacer(modifier = Modifier.height(2.dp))

            // Campo de título editável
            OutlinedTextField(
                value = editableTitle,
                onValueChange = {
                    editableTitle = it
                    isModified = it != title || editableContent != content
                },
                label = { Text("Título") },
                modifier = Modifier.fillMaxWidth(),
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Campo de conteúdo editável
            OutlinedTextField(
                value = editableContent,
                onValueChange = {
                    editableContent = it
                    isModified = it != content || editableTitle != title
                },
                label = { Text("Conteúdo") },
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                maxLines = Int.MAX_VALUE,
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Botão de salvar se houver alterações
            if (isModified) {
                Button(
                    onClick = {
                        // Salva a nota
                        notesViewModel.updateNote(NotaItem(id = noteId, title = editableTitle, content = editableContent))
                        lastModified = getCurrentDateTime() // Atualiza a data de modificação ao salvar
                        navController.popBackStack()
                    },
                    modifier = Modifier.align(Alignment.End)
                ) {
                    Text("Salvar")
                }
            }
        }
    }
}

fun getCurrentDateTime(): String {
    return try {
        val current = System.currentTimeMillis()
        val formatter = java.text.SimpleDateFormat("EEEE, dd MMM, HH:mm", java.util.Locale.getDefault())
        formatter.format(current)
    } catch (e: Exception) {
        "Erro ao obter data/hora"
    }
}

@Preview(showBackground = true)
@Composable
fun NotaDetalhesPreview() {
    val navController = rememberNavController()
    val notesViewModel = NotesViewModel()

    MonyTheme {
    NotaDetalhes(
        title = "Exemplo de Título",
        content = "Este é o conteúdo completo da nota.",
        navController = navController,
        notesViewModel = notesViewModel,
        noteId = "1",
    )
}}