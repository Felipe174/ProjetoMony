package com.example.mony.feature.home

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.pullrefresh.rememberPullRefreshState
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.FloatingActionButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults.topAppBarColors
import androidx.compose.material3.adaptive.navigationsuite.NavigationSuiteScaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.example.mony.R
import com.example.mony.feature.conta.viewmodel.ContaViewModel
import com.example.mony.feature.home.classe.Expense
import com.example.mony.feature.home.classe.ExpenseItem
import com.example.mony.feature.home.dialog.AddDialog
import com.example.mony.feature.home.logicaGrafico.HomeWithGraph
import com.example.mony.feature.home.viewmodel.HomeViewModel
import com.example.mony.feature.home.viewmodel.fakeHomeViewModel
import com.example.mony.feature.notas.viewmodel.NotesViewModel
import com.example.mony.feature.utils.AppState
import com.example.mony.feature.utils.navegation.MyApp
import com.example.mony.feature.utils.navegation.getTopLevelDestinations
import com.example.mony.ui.theme.Black
import com.example.mony.ui.theme.GreenLight
import com.example.mony.ui.theme.MonyTheme
import com.example.mony.ui.theme.RedLight
import com.google.accompanist.pager.HorizontalPagerIndicator
import com.google.accompanist.swiperefresh.SwipeRefresh
import com.google.accompanist.swiperefresh.rememberSwipeRefreshState
import kotlinx.coroutines.delay
import java.text.NumberFormat
import java.util.Calendar
import java.util.Locale


class HomeActivity : ComponentActivity() {
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

@OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class,
    ExperimentalMaterialApi::class
)
@Composable
fun HomeScreen(
    appState: AppState,
    homeViewModel: HomeViewModel = viewModel(),
    onExpenseClick: (String) -> Unit,
    navController: NavController
) {
    var showAddExpenseDialog by remember { mutableStateOf(false) }
    var selectedFilter by remember { mutableStateOf("Semana") }
    var currentDate by remember { mutableLongStateOf(System.currentTimeMillis()) }
    val selectedItems = remember { mutableStateListOf<Expense>() }
    var showDeleteDialog by remember { mutableStateOf(false) }
    var showCheckboxes by remember { mutableStateOf(false) }
    val expenses by homeViewModel.expenses.collectAsState()
    var selectedExpense by remember { mutableStateOf<Expense?>(null) }

    val filteredExpenses by remember(currentDate, selectedFilter, expenses) {
        derivedStateOf {
            filterExpensesByPeriod(expenses, currentDate, selectedFilter)
        }
    }

    val totalIncome by remember(filteredExpenses) {
        derivedStateOf { filteredExpenses.filter { it.type.isIncome }.sumOf { it.amount } }
    }

    val totalExpense by remember(filteredExpenses) {
        derivedStateOf { filteredExpenses.filter { !it.type.isIncome }.sumOf { it.amount } }
    }

    val topLevelDestinations = getTopLevelDestinations()

    val imagens = listOf(
        R.drawable.carrosel1,
        R.drawable.carrosel2,
    )

    LaunchedEffect(Unit) {
        delay(300)
        if (expenses.isEmpty()) {
            homeViewModel.checkUserAndLoadExpenses()
        }
    }

    NavigationSuiteScaffold(
        containerColor = Color.White,
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
                    onClick = { appState.navigateToTopLevelDestination(destination.route) }
                )
            }
        }
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background),
            verticalArrangement = Arrangement.Top,
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            TopAppBar(
                title = {},
                navigationIcon = {
                    IconButton(onClick = { /* TODO: Abrir menu ou pesquisa */ }) {
                        Icon(
                            imageVector = Icons.Filled.Menu,
                            contentDescription = "Pesquisar",
                            tint = MaterialTheme.colorScheme.onSecondary
                        )
                    }
                },
                actions = {
                    if (selectedItems.isNotEmpty()) {
                        IconButton(onClick = {
                            Log.d("HomeScreen", "SelectedItems size = ${selectedItems.size}")
                            selectedItems.forEach { Log.d("HomeScreen", "Item selecionado: ${it.id}") }
                            showDeleteDialog = true
                        }) {
                            Icon(imageVector = Icons.Filled.Delete, contentDescription = "Deletar")
                        }
                    }

                },
                colors = topAppBarColors(containerColor = MaterialTheme.colorScheme.onPrimary)
            )

            val isRefreshing by remember { derivedStateOf { expenses.isEmpty() } }
            val swipeRefreshState = rememberSwipeRefreshState(isRefreshing)

            SwipeRefresh(
                state = swipeRefreshState,
                onRefresh = { homeViewModel.loadExpenses() }
            ) {

                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(MaterialTheme.colorScheme.background),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    item {
                        // Card Carrossel
                        Card(
                            colors = CardDefaults.cardColors(MaterialTheme.colorScheme.background),
                            modifier = Modifier
                                .padding(bottom = 20.dp, top = 10.dp)
                                .width(330.dp)
                                .height(200.dp)
                                .border(
                                    width = 2.dp,
                                    color = MaterialTheme.colorScheme.secondary,
                                    shape = RoundedCornerShape(12.dp)
                                ),
                            elevation = CardDefaults.cardElevation(2.dp),
                        ) {
                            val pagerState = rememberPagerState(pageCount = { imagens.size })

                            Box(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .clip(RoundedCornerShape(12.dp))
                            ) {
                                HorizontalPager(
                                    state = pagerState,
                                    modifier = Modifier.fillMaxSize()
                                ) { page ->
                                    Image(
                                        painter = painterResource(id = imagens[page]),
                                        contentDescription = null,
                                        modifier = Modifier
                                            .fillMaxSize()
                                            .clickable {
                                                when (page) {
                                                    0 -> navController.navigate("updates_page")
                                                    1 -> navController.navigate("comparison_page")
                                                    2 -> navController.navigate("tip_page")
                                                }
                                            }
                                    )
                                }

                                HorizontalPagerIndicator(
                                    pagerState = pagerState,
                                    pageCount = imagens.size,
                                    activeColor = Color.Black,
                                    inactiveColor = Color.LightGray,
                                    modifier = Modifier
                                        .align(Alignment.BottomCenter)
                                        .padding(8.dp)
                                )
                            }
                        }

                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(90.dp)
                                .padding(start = 30.dp, end = 15.dp),
                        ) {
                            // Card Renda
                            Card(
                                colors = CardDefaults.cardColors(MaterialTheme.colorScheme.background),
                                modifier = Modifier
                                    .width(150.dp)
                                    .fillMaxHeight()
                                    .align(Alignment.CenterVertically)
                                    .border(
                                        width = 2.dp,
                                        color = MaterialTheme.colorScheme.secondary,
                                        shape = RoundedCornerShape(12.dp)
                                    ),
                                elevation = CardDefaults.cardElevation(2.dp)
                            ) {
                                Column(
                                    Modifier
                                        .fillMaxSize()
                                        .padding(3.dp),
                                    horizontalAlignment = Alignment.CenterHorizontally
                                ) {
                                    Row(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .height(30.dp)
                                    ) {
                                        Spacer(modifier = Modifier.width(30.dp))
                                        Image(
                                            painter = painterResource(id = R.drawable.expense),
                                            contentDescription = "Expense",
                                            modifier = Modifier
                                                .size(15.dp)
                                                .align(Alignment.Bottom)
                                        )
                                        Spacer(modifier = Modifier.width(5.dp))
                                        Text(
                                            "Renda",
                                            fontSize = 15.sp,
                                            color = MaterialTheme.colorScheme.secondary,
                                            fontWeight = FontWeight.Bold,
                                            modifier = Modifier.padding(top = 10.dp)
                                        )
                                    }
                                    Spacer(modifier = Modifier.height(8.dp))
                                    Text(
                                        formatCurrency(totalIncome),
                                        fontSize = 24.sp,
                                        fontWeight = FontWeight.Medium,
                                        color = GreenLight,
                                        textAlign = TextAlign.Center,
                                        modifier = Modifier.padding(5.dp),
                                        maxLines = 1
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.width(35.dp))

                            // Card Gasto
                            Card(
                                colors = CardDefaults.cardColors(MaterialTheme.colorScheme.background),
                                modifier = Modifier
                                    .width(150.dp)
                                    .fillMaxHeight()
                                    .padding(end = 5.dp)
                                    .align(Alignment.CenterVertically)
                                    .border(
                                        width = 2.dp,
                                        color = MaterialTheme.colorScheme.secondary,
                                        shape = RoundedCornerShape(12.dp)
                                    ),
                                elevation = CardDefaults.cardElevation(2.dp)
                            ) {
                                Column(
                                    Modifier
                                        .fillMaxSize()
                                        .padding(3.dp),
                                    horizontalAlignment = Alignment.CenterHorizontally,
                                ) {
                                    Row(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .height(30.dp)
                                    ) {
                                        Spacer(modifier = Modifier.width(30.dp))
                                        Image(
                                            painter = painterResource(id = R.drawable.income),
                                            contentDescription = "Expense",
                                            modifier = Modifier
                                                .size(15.dp)
                                                .align(Alignment.Bottom)
                                        )
                                        Spacer(modifier = Modifier.width(5.dp))
                                        Text(
                                            "Gasto",
                                            fontSize = 15.sp,
                                            color = MaterialTheme.colorScheme.secondary,
                                            fontWeight = FontWeight.Bold,
                                            modifier = Modifier.padding(top = 10.dp)
                                        )
                                    }
                                    Spacer(modifier = Modifier.height(8.dp))
                                    Text(
                                        formatCurrency(totalExpense),
                                        fontSize = 24.sp,
                                        fontWeight = FontWeight.Medium,
                                        color = RedLight,
                                        modifier = Modifier.padding(5.dp),
                                        maxLines = 1
                                    )
                                }
                            }
                            Spacer(modifier = Modifier.weight(1f))
                        }
                    }

                    item {
                        Spacer(modifier = Modifier.height(7.dp))
                        HomeWithGraph(
                            selectedFilter = selectedFilter,
                            currentDate = currentDate,
                            expenses = expenses,
                            onDateChange = { newDate -> currentDate = newDate },
                            onFilterChange = { newFilter -> selectedFilter = newFilter }
                        )
                    }

                    items(filteredExpenses, key = { it.id }) { expense ->
                        val isSelected = selectedItems.any { it.id == expense.id }
                        val alpha by animateFloatAsState(if (isSelected) 0.5f else 1f)

                        Box(
                            modifier = Modifier
                                .graphicsLayer(alpha = alpha)
                                .combinedClickable(
                                    onClick = {
                                        Log.d("CLICK", "Item clicado: ${expense.id}")
                                        if (showCheckboxes) {
                                            if (isSelected) {
                                                selectedItems.removeAll { it.id == expense.id }
                                            } else {
                                                selectedItems.any { it.id == expense.id }

                                            }

                                        } else {
                                            onExpenseClick(expense.id)
                                        }
                                    },
                                    onLongClick = {
                                        if (!showCheckboxes) {
                                            showCheckboxes = true
                                        }
                                        if (!selectedItems.any { it.id == expense.id }) {
                                            selectedItems.add(expense)
                                        }
                                        Log.d("HomeScreen", "Clique longo em: ${expense.id}")
                                    }
                                )

                        ) {
                            ExpenseItem(
                                expense = expense,
                                isSelected = isSelected,
                            )
                        }
                    }
                }
            }

            AnimatedVisibility(
                visible = true,
                enter = fadeIn() + slideInVertically(),
                exit = fadeOut() + slideOutVertically()
            ) {
                // Seu conteúdo animado (se houver)
            }

            selectedExpense?.let { expense ->
                ExpenseDetailScreen(
                    expenseId = expense.id,
                    onBack = { selectedExpense = null },
                )
            }

            AddDialog(
                showDialog = showAddExpenseDialog,
                onDismiss = { showAddExpenseDialog = false },
                onAdd = { amount, type, date ->
                    val newExpense = Expense(
                        id = "",
                        amount = amount,
                        date = date,
                        type = type,
                        description = ""
                    )
                    homeViewModel.addExpense(newExpense)
                    showAddExpenseDialog = false
                }
            )
        }

        if (showCheckboxes) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .pointerInput(Unit) {
                        detectTapGestures(
                            onTap = {
                                showCheckboxes = false
                                selectedItems.clear()
                            }
                        )
                    }
            )
        }

        Column(
            Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.Bottom,
            horizontalAlignment = Alignment.End
        ) {
            val infiniteTransition = rememberInfiniteTransition()
            val floatAnimation by infiniteTransition.animateFloat(
                initialValue = 0f,
                targetValue = 10f,
                animationSpec = infiniteRepeatable(
                    animation = tween(1000, easing = FastOutSlowInEasing),
                    repeatMode = RepeatMode.Reverse
                ),
                label = "floatAnimation"
            )

            BtnAdicionar(
                onClick = { showAddExpenseDialog = true },
            )
        }
    }

    if (showDeleteDialog) {
        AlertDialog(
            onDismissRequest = { showDeleteDialog = false },
            title = { Text("Confirmar Exclusão") },
            text = {
                Text("Deseja realmente excluir ${selectedItems.size} item(ns) selecionado(s)?")
            },
            confirmButton = {
                TextButton(onClick = {
                    Log.d("HomeScreen", "Confirmou exclusão de ${selectedItems.size} itens")
                    if (selectedItems.isNotEmpty()) {
                        selectedItems.forEach {
                            Log.d("HomeScreen", "Deletando id: ${it.id}")
                            homeViewModel.deleteExpense(it.id)
                        }
                        selectedItems.clear()
                    }
                    showDeleteDialog = false
                }) {
                    Text("Sim", color = MaterialTheme.colorScheme.error)
                }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteDialog = false }) {
                    Text("Não")
                }
            }
        )
    }
}




@Composable
fun BtnAdicionar(onClick: () -> Unit, modifier: Modifier = Modifier) {

    FloatingActionButton(
        onClick = onClick,
        containerColor = MaterialTheme.colorScheme.primary,
        contentColor = Color.Black,
        elevation = FloatingActionButtonDefaults.elevation(8.dp),
        modifier = modifier
            .padding(16.dp)
    ) {
        Icon(
            imageVector = Icons.Filled.Add,
            contentDescription = "Adicionar",
            tint = Black
        )
    }
}

//Cria um tooltip
@Composable
fun EnhancedTooltip (value: Double, position: Offset) {
    Box(
        modifier = Modifier
            .offset { IntOffset(position.x.toInt(), position.y.toInt()) }
            .background(Color(0xFF6200EE), shape = RoundedCornerShape(8.dp))
            .shadow(4.dp, shape = RoundedCornerShape(8.dp))
            .padding(12.dp)
            .animateContentSize() // Animação de tamanho
    ) {
        Text(
            text = value.toString(),
            color = Color.White,
            style = TextStyle(fontWeight = FontWeight.Bold, fontSize = 16.sp)
        )
    }
}

//Filtra as despesas com base na data e no filtro selecionado.
fun filterExpensesByPeriod(
    expenses: List<Expense>,
    currentDate: Long,
    selectedFilter: String
): List<Expense> {
    val calendar = Calendar.getInstance().apply {
        timeInMillis = currentDate
    }

    return when (selectedFilter) {
        "Semana" -> {
            val startOfWeek = (calendar.clone() as Calendar).apply {
                firstDayOfWeek = Calendar.MONDAY
                set(Calendar.DAY_OF_WEEK, Calendar.MONDAY)
                set(Calendar.HOUR_OF_DAY, 0)
                set(Calendar.MINUTE, 0)
                set(Calendar.SECOND, 0)
                set(Calendar.MILLISECOND, 0)
            }
            val endOfWeek = (calendar.clone() as Calendar).apply {
                firstDayOfWeek = Calendar.MONDAY
                set(Calendar.DAY_OF_WEEK, Calendar.SUNDAY)
                set(Calendar.HOUR_OF_DAY, 23)
                set(Calendar.MINUTE, 59)
                set(Calendar.SECOND, 59)
                set(Calendar.MILLISECOND, 999)
            }

            expenses.filter { expense ->
                expense.date in startOfWeek.timeInMillis..endOfWeek.timeInMillis
            }
        }

        "Mês" -> {
            val firstDayOfMonth = (calendar.clone() as Calendar).apply {
                set(Calendar.DAY_OF_MONTH, 1)
            }
            val lastDayOfMonth = (calendar.clone() as Calendar).apply {
                set(Calendar.DAY_OF_MONTH, getActualMaximum(Calendar.DAY_OF_MONTH))
            }

            expenses.filter { expense ->
                expense.date in firstDayOfMonth.timeInMillis..lastDayOfMonth.timeInMillis
            }
        }

        "Ano" -> {
            val firstDayOfYear = (calendar.clone() as Calendar).apply {
                set(Calendar.DAY_OF_YEAR, 1)
            }
            val lastDayOfYear = (calendar.clone() as Calendar).apply {
                set(Calendar.DAY_OF_YEAR, getActualMaximum(Calendar.DAY_OF_YEAR))
            }

            expenses.filter { expense ->
                expense.date in firstDayOfYear.timeInMillis..lastDayOfYear.timeInMillis
            }
        }

        else -> emptyList()
    }
}

fun formatCurrency(amount: Double): String {
    val formatter = NumberFormat.getCurrencyInstance(Locale.getDefault())
    return formatter.format(amount)
}


@Preview(showBackground = true)
@Composable
fun HomeScreenPreview() {
    // Usando um NavController simples
    val navController = rememberNavController()
    // Criando uma instância do HomeViewModel
    val fakeViewModel = remember { fakeHomeViewModel() }
    // Criando uma instância do AppState
    AppState(navController)

    MonyTheme(darkTheme = false) {
    HomeScreen(appState = AppState(navController = rememberNavController())
            ,homeViewModel = fakeViewModel
            ,onExpenseClick={},
            navController = navController)
}
    }