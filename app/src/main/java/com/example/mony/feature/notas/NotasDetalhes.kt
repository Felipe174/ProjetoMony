package com.example.mony.feature.notas

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
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
import androidx.compose.runtime.collectAsState
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
import com.example.mony.feature.notas.viewmodel.FakeNotesViewModel
import com.example.mony.feature.notas.viewmodel.NotesViewModel
import com.example.mony.ui.theme.MonyTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NotaDetalhes(
    navController: NavController,
    notesViewModel: NotesViewModel,
    noteId: String,
) {
    val notes by notesViewModel.notes.collectAsState()
    val note = notes.find { it.id == noteId }

    var editableTitle by remember { mutableStateOf(note?.title ?: "") }
    var editableContent by remember { mutableStateOf(note?.content ?: "") }
    var isModified by remember { mutableStateOf(false) }
    var lastModified by remember { mutableStateOf(getCurrentDateTime()) }

    if (note == null) {
        Text("Nota não encontrada.", modifier = Modifier.padding(16.dp))
        return
    }

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
                .padding(start=16.dp, end=16.dp, bottom=16.dp)
        ) {
            TopAppBar(
                title = { Text("Detalhes da Nota", modifier = Modifier.padding(start = 12.dp)) },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Voltar")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(MaterialTheme.colorScheme.onPrimary)
            )

            Spacer(modifier = Modifier.height(5.dp))

            Text(
                text = "Última alteração foi em $lastModified",
                color = Color.Gray,
                modifier = Modifier.padding(2.dp).align(Alignment.End),
                fontSize = MaterialTheme.typography.bodySmall.fontSize
            )

            Spacer(modifier = Modifier.height(2.dp))

            OutlinedTextField(
                value = editableTitle,
                onValueChange = {
                    editableTitle = it
                    isModified = it != note.title || editableContent != note.content
                },
                label = { Text("Título") },
                modifier = Modifier.fillMaxWidth(),
            )

            Spacer(modifier = Modifier.height(16.dp))

            OutlinedTextField(
                value = editableContent,
                onValueChange = {
                    editableContent = it
                    isModified = it != note.content || editableTitle != note.title
                },
                label = { Text("Conteúdo") },
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                maxLines = Int.MAX_VALUE,
            )

            Spacer(modifier = Modifier.height(16.dp))

            if (isModified) {
                Button(
                    onClick = {
                        notesViewModel.updateNote(
                            NotaItem(id = note.id, title = editableTitle, content = editableContent)
                        )
                        lastModified = getCurrentDateTime()
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
    val fakeViewModel = remember { FakeNotesViewModel() }

    MonyTheme {
    NotaDetalhes(
        navController = navController,
        notesViewModel = fakeViewModel,
        noteId = "1",
    )
}}