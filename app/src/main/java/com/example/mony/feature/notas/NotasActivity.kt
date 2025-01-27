package com.example.mony.feature.notas

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.Button
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.adaptive.navigationsuite.NavigationSuiteScaffold
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.example.mony.R
import com.example.mony.feature.notas.classe.NotaItem
import com.example.mony.feature.notas.viewmodel.NotesViewModel
import com.example.mony.feature.utils.AppState
import com.example.mony.feature.utils.navegation.MyApp
import com.example.mony.feature.utils.navegation.topLevelDestinations
import kotlinx.coroutines.launch

class NotasActivity : ComponentActivity() {
    private val notesViewModel: NotesViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MyApp()
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NotasScreen(
    navController: NavController,
    appState: AppState,
    notesViewModel: NotesViewModel
) {
    // Estado para controlar a visibilidade do Drawer
    val drawerState = rememberDrawerState(DrawerValue.Closed)
    var isDeleteMode by remember { mutableStateOf(false) }
    var selectedNotes by remember { mutableStateOf(mutableSetOf<NotaItem>()) }
    val scope = rememberCoroutineScope()
    val notesState = notesViewModel.notes.collectAsState().value


    // Scaffold com Drawer
    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            DrawerMenu(onMenuItemClick = { selectedItem ->
                when (selectedItem) {
                    "Notas" -> appState.navigateToTopLevelDestination("notes")
                    "Arquivos" -> appState.navigateToTopLevelDestination("arquivosScreen")
                    "Configurações" -> appState.navigateToTopLevelDestination("configScreen")
                }
                scope.launch { drawerState.close() }
            })
        }
    ) {
        NavigationSuiteScaffold(
            navigationSuiteItems = {
                topLevelDestinations.forEach { destination ->
                    val selected = appState.isRouteInHierarchy(
                        destination.route
                    )
                    item(
                        selected = selected,
                        icon = {
                            Icon(
                                imageVector = if (selected) {
                                    destination.selectedIcon
                                } else {
                                    destination.unselectedIcon
                                },
                                contentDescription = stringResource(destination.iconTextId),
                                tint = if (selected) {
                                    destination.selectedIconColor
                                } else {
                                    destination.unselectedIconColor
                                }
                            )
                        },
                        label = {
                            Text(
                                text = stringResource(destination.iconTextId),
                                maxLines = 1
                            )
                        },
                        onClick = { appState.navigateToTopLevelDestination(destination.route) }
                    )
                }
            }
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(top = 65.dp, end = 5.dp, start = 5.dp, bottom = 5.dp),

                ) {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                ) {
                    if (notesState.isEmpty()) {
                        item {
                            Text(
                                text = "Nenhuma nota encontrada",
                                color = Color.Gray,
                                modifier = Modifier.fillMaxWidth(),
                                textAlign = TextAlign.Center
                            )
                        }
                    } else {

                        items(notesState) { note ->
                            NotasItem(
                                note = note,
                                onClick = {
                                    Log.d("NotasItem", "Item clicado: ${note.id}")
                                    if (isDeleteMode) {
                                        // Se no modo de exclusão, alterna a seleção
                                        if (selectedNotes.contains(note)) {
                                            selectedNotes.remove(note)
                                        } else {
                                            selectedNotes.add(note)
                                        }
                                    } else {
                                        navController.navigate("notaDetalhes/{$note.id}")
                                    }
                                },
                                onLongClick = {
                                    isDeleteMode = true
                                    selectedNotes.add(note) // Adiciona a nota à seleção
                                },
                                isSelected = selectedNotes.contains(note)
                            )
                        }
                    }
                }
            }
            }
            Column(
                Modifier.fillMaxSize().padding(bottom = 85.dp),
                verticalArrangement = Arrangement.Bottom,
                horizontalAlignment = Alignment.End

            ) {
                FloatingActionButton(
                    onClick = { navController.navigate("noteEditor") },
                    modifier = Modifier.padding(16.dp)
                ) {
                    Icon(Icons.Default.Add, contentDescription = "Criar Nota")
                }
            }
        }

    TopAppBar(
        title = { Text("Notas") },
        actions = {
            if (isDeleteMode) {
                IconButton(onClick = {
                    selectedNotes.forEach { notesViewModel.deleteNote(it) }
                    selectedNotes.clear() // Limpa a seleção
                    isDeleteMode = false // Desativa o modo de exclusão
                }) {
                    Icon(Icons.Default.Delete, contentDescription = "Excluir Notas")
                }
            }
        },
        navigationIcon = {
            IconButton(onClick = { scope.launch { drawerState.open() } }) {
                Icon(Icons.Default.Menu, contentDescription = "Menu")
            }
        }
    )
}


@Composable
fun DrawerMenu(onMenuItemClick: (String) -> Unit) {
    var selectedItemIndex by remember { mutableStateOf(0) }

    Surface {
        Column(
            modifier = Modifier
                .width(200.dp)
                .fillMaxHeight()
        ) {
            Text(
                "Configuração da Conta",
                fontSize = 12.sp,
                fontWeight = FontWeight.Medium,
                color = Color(android.graphics.Color.parseColor("#808080")),
                modifier = Modifier
                    .align(Alignment.Start)
                    .padding(start = 10.dp, top = 20.dp, bottom = 10.dp)
            )

            DrawerItem(
                icon = R.drawable.escrita,
                title = "Notas",
                onClick = {
                    selectedItemIndex = 0
                    onMenuItemClick("notes")
                },
                isSelected = selectedItemIndex == 0
            )

            DrawerItem(
                icon = R.drawable.pasta,
                title = "Arquivos",
                onClick = {
                    selectedItemIndex = 1
                    onMenuItemClick("arquivoScreen")
                },
                isSelected = selectedItemIndex == 1
            )

            Spacer(modifier = Modifier.weight(1f))

            Box(
                modifier = Modifier
                    .height(1.dp)
                    .fillMaxWidth()
                    .padding(horizontal = 5.dp)
                    .background(Color.LightGray)
                    .alpha(0.2f)
            )

            Text(
                "Configurações",
                fontSize = 12.sp,
                fontWeight = FontWeight.Medium,
                color = Color(android.graphics.Color.parseColor("#808080")),
                modifier = Modifier
                    .padding(start = 10.dp, top = 10.dp)
            )

            DrawerItem(
                icon = R.drawable.configuracao,
                title = "Configuração",
                onClick = {
                    selectedItemIndex = 2
                    onMenuItemClick("mais")
                },
                isSelected = selectedItemIndex == 2
            )
            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}

@Composable
fun DrawerItem(icon: Int, title: String, onClick: () -> Unit, isSelected: Boolean) {
    val selectedBackgroundColor = Color(android.graphics.Color.parseColor("#fff0f5"))
    val selectedTextColor = Color(android.graphics.Color.parseColor("#b34db2"))
    val defaultBackgroundColor = Color.Transparent
    val defaultTextColor = Color.Gray

    val backgroundColor = if (isSelected) selectedBackgroundColor else defaultBackgroundColor
    val textColor = if (isSelected) selectedTextColor else defaultTextColor
    val iconColor = if (isSelected) selectedTextColor else defaultTextColor

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(backgroundColor)
            .clickable(onClick = onClick)
            .padding(10.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Image(
            painter = painterResource(id = icon),
            contentDescription = null,
            colorFilter = ColorFilter.tint(iconColor),
            modifier = Modifier
                .size(20.dp)
                .align(Alignment.CenterVertically)
        )
        Spacer(modifier = Modifier.width(16.dp))
        Text(
            text = title,
            fontSize = 15.sp,
            fontWeight = FontWeight.Medium,
            color = textColor
        )
    }
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NoteEditor(
    navController: NavController,
    appState: AppState,
    notesViewModel: NotesViewModel
) {
    var noteTitle by remember { mutableStateOf("") }
    var noteContent by remember { mutableStateOf("") }

    NavigationSuiteScaffold(
        navigationSuiteItems = {},
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(5.dp)
                .padding(16.dp)
        ) {
            // Botão para voltar (topo)
            TopAppBar(
                title = { Text("Nova Nota") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Voltar")
                    }
                }
            )
            Spacer(modifier = Modifier.height(16.dp))

            // Campo de Título
            OutlinedTextField(
                value = noteTitle,
                onValueChange = { noteTitle = it },
                label = { Text("Título") },
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(16.dp))

            // Campo de Conteúdo
            OutlinedTextField(
                value = noteContent,
                onValueChange = { noteContent = it },
                label = { Text("Conteúdo") },
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                maxLines = Int.MAX_VALUE
            )
            Spacer(modifier = Modifier.height(16.dp))

            // Botão de salvar
            Button(
                onClick = {
                    if (noteTitle.isNotEmpty() && noteContent.isNotEmpty()) {
                        notesViewModel.addNote(NotaItem(title = noteTitle, content = noteContent))
                        navController.popBackStack()  // Volta para a tela anterior após salvar
                    }
                },
                modifier = Modifier.align(Alignment.End)
            ) {
                Text("Salvar")
            }


        }
    }
}
@Preview(showBackground = true)
@Composable
fun NotasScreenPreview() {
    // Usando um NavController simples
    val navController = rememberNavController()

    // Criando uma instância do AppState
    val appState = AppState(navController)

    // Chamando a tela de Notas diretamente
    NotasScreen(
        navController = navController,
        appState = appState,
        notesViewModel = NotesViewModel()
    )
}

@Preview(showBackground = true)
@Composable
fun NoteEditorPreview() {
    // Usando um NavController simples
    val navController = rememberNavController()

    // Criando uma instância do AppState
    val appState = AppState(navController)

    // Chamando a tela de Editor de Notas diretamente
    NoteEditor(
        navController = navController,
        appState = appState,
        notesViewModel = NotesViewModel()
    )
}

@Preview(showBackground = true)
@Composable
fun DrawerMenuPreview() {
    DrawerMenu { selectedItem ->
        println("Menu item clicked: $selectedItem")
    }
}