package com.example.mony.feature.notas

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
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
import androidx.compose.material3.ListItemDefaults.containerColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults.topAppBarColors
import androidx.compose.material3.adaptive.navigationsuite.NavigationSuiteScaffold
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
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
import androidx.compose.ui.graphics.graphicsLayer
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
import com.example.mony.feature.conta.viewmodel.ContaViewModel
import com.example.mony.feature.home.viewmodel.HomeViewModel
import com.example.mony.feature.notas.classe.NotaItem
import com.example.mony.feature.notas.viewmodel.NotesViewModel
import com.example.mony.feature.utils.AppState
import com.example.mony.feature.utils.navegation.MyApp
import com.example.mony.feature.utils.navegation.getTopLevelDestinations
import com.example.mony.ui.theme.Amarelo
import com.example.mony.ui.theme.MonyTheme
import kotlinx.coroutines.launch


class NotasActivity : ComponentActivity() {
    private val notesViewModel: NotesViewModel by viewModels()
    private val contaViewModel: ContaViewModel by viewModels()
    private val homeViewModel: HomeViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MonyTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background,
                ) {
                    MyApp(
                        notesViewModel = notesViewModel,
                        contaViewModel = contaViewModel,
                        homeViewModel = homeViewModel
                    )
                }
            }
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
    val drawerState = rememberDrawerState(DrawerValue.Closed)
    var isDeleteMode by remember { mutableStateOf(false) }
    val selectedNotes by remember { mutableStateOf(mutableSetOf<NotaItem>()) }
    val scope = rememberCoroutineScope()
    val notesState by notesViewModel.notes.collectAsState(initial = emptyList())
    val topLevelDestinations = getTopLevelDestinations()


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
                    val selected = appState.isRouteInHierarchy(destination.route)
                    item(
                        selected = selected,
                        icon = {
                            Icon(
                                imageVector = if (selected) destination.selectedIcon else destination.unselectedIcon,
                                contentDescription = stringResource(destination.iconTextId),
                                tint = if (selected) destination.selectedIconColor else destination.unselectedIconColor
                            )
                        },
                        label = {
                            Text(text = stringResource(destination.iconTextId), maxLines = 1)
                        },
                        onClick = { appState.navigateToTopLevelDestination(destination.route) },
                    )
                }
            }
        ) {

            Box(modifier = Modifier.fillMaxSize()) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(MaterialTheme.colorScheme.background)
                ) {
                    TopAppBar(
                        title = { Text("Notas") },
                        actions = {
                            if (isDeleteMode) {
                                IconButton(onClick = {
                                    selectedNotes.forEach { notesViewModel.deleteNote(it) }
                                    selectedNotes.clear()
                                    isDeleteMode = false
                                }) {
                                    Icon(Icons.Default.Delete, contentDescription = "Excluir Notas")
                                }
                            }
                        },
                        navigationIcon = {
                            IconButton(onClick = { scope.launch { drawerState.open() } }) {
                                Icon(Icons.Default.Menu, contentDescription = "Menu")
                            }
                        },
                        colors = topAppBarColors(containerColor = MaterialTheme.colorScheme.onPrimary)
                    )
                    LazyColumn(
                        modifier = Modifier.fillMaxSize().background(MaterialTheme.colorScheme.background)
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
                                val alpha by animateFloatAsState(
                                    targetValue = if (selectedNotes.contains(note)) 0.5f else 1f,
                                    animationSpec = tween(durationMillis = 300)
                                )
                                NotasItem(
                                    note = note,
                                    onClick = {
                                        if (isDeleteMode) {
                                            if (selectedNotes.contains(note)) {
                                                selectedNotes.remove(note)
                                            } else {
                                                selectedNotes.add(note)
                                            }
                                        }
                                        if (note.id.isNotEmpty()) {
                                            navController.navigate("notaDetalhes/${note.id}")
                                        }
                                    },
                                    onLongClick = {
                                        isDeleteMode = true
                                        selectedNotes.add(note)
                                    },
                                    isSelected = selectedNotes.contains(note)
                                )
                            }
                        }
                    }
                }

                Column(
                    Modifier
                        .fillMaxSize()
                        .padding(16.dp),
                    verticalArrangement = Arrangement.Bottom,
                    horizontalAlignment = Alignment.End
                ) {
                    FloatingActionButton(
                        containerColor = MaterialTheme.colorScheme.primary,
                        onClick = { navController.navigate("noteEditor") }
                    ) {
                        Icon(Icons.Default.Add, contentDescription = "Criar Nota")
                    }
                }
            }
        }
    }
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
                color = MaterialTheme.colorScheme.onSecondary,
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
                    onMenuItemClick("arquivosScreen")
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
                color = MaterialTheme.colorScheme.onSecondary,
                modifier = Modifier
                    .padding(start = 10.dp, top = 10.dp)
            )

            DrawerItem(
                icon = R.drawable.configuracao,
                title = "Configuração",
                onClick = {
                    selectedItemIndex = 2
                    onMenuItemClick("configScreen")
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
            .background(MaterialTheme.colorScheme.background)
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
            color = MaterialTheme.colorScheme.onSecondary
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


    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        TopAppBar(
            title = { Text("Nova Nota", color = MaterialTheme.colorScheme.onSecondary) },
            navigationIcon = {
                IconButton(onClick = { navController.popBackStack() }) {
                    Icon(Icons.Default.ArrowBack, contentDescription = "Voltar")
                }
            },
            colors = topAppBarColors(containerColor = MaterialTheme.colorScheme.onPrimary)
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(bottom = 20.dp)
                .padding(16.dp)
                .background(MaterialTheme.colorScheme.background),
        ) {
            Spacer(modifier = Modifier.height(16.dp))

            OutlinedTextField(
                value = noteTitle,
                onValueChange = { noteTitle = it },
                label = { Text("Título", color = MaterialTheme.colorScheme.secondary) },
                modifier = Modifier.fillMaxWidth(),
            )
            Spacer(modifier = Modifier.height(16.dp))

            OutlinedTextField(
                value = noteContent,
                onValueChange = { noteContent = it },
                label = { Text("Conteúdo", color = MaterialTheme.colorScheme.secondary) },
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                maxLines = Int.MAX_VALUE
            )
            Spacer(modifier = Modifier.height(16.dp))

            Button(
                onClick = {
                    if (noteTitle.isNotEmpty() && noteContent.isNotEmpty()) {
                        notesViewModel.addNote(NotaItem(title = noteTitle, content = noteContent))
                        navController.popBackStack()
                    }
                },
                modifier = Modifier.align(Alignment.End),
                enabled = noteTitle.isNotEmpty() && noteContent.isNotEmpty()
            ) {
                Text("Salvar", color = MaterialTheme.colorScheme.onSecondary)
            }
        }
    }
}


@Preview(showBackground = true)
@Composable
fun NotasScreenPreview() {
    val navController = rememberNavController()
    val appState = AppState(navController)

    MonyTheme {
    NotasScreen(
        navController = navController,
        appState = appState,
        notesViewModel = NotesViewModel()
    )
}
}
@Preview(showBackground = true)
@Composable
fun NoteEditorPreview() {
    val navController = rememberNavController()
    val appState = AppState(navController)
    MonyTheme {
    NoteEditor(
        navController = navController,
        appState = appState,
        notesViewModel = NotesViewModel()
    )
}}

@Preview(showBackground = true)
@Composable
fun DrawerMenuPreview() {
    MonyTheme {
        DrawerMenu { selectedItem ->
            println("Menu item clicked: $selectedItem")
        }
    }
}