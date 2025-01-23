package com.example.mony.feature.home

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBackIosNew
import androidx.compose.material.icons.filled.ArrowForwardIos
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Done
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.FloatingActionButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.adaptive.navigationsuite.NavigationSuiteScaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.rememberNavController
import com.example.mony.feature.home.classe.Expense
import com.example.mony.feature.home.classe.ExpenseItem
import com.example.mony.feature.home.dialog.AddDialog
import com.example.mony.feature.home.viewmodel.HomeViewModel
import com.example.mony.feature.notas.NotasScreen
import com.example.mony.feature.utils.AppState
import com.example.mony.feature.utils.navegation.MyApp
import com.example.mony.feature.utils.navegation.topLevelDestinations
import com.google.firebase.FirebaseApp
import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class HomeActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            Surface {
                // Passa o ViewModel para a HomeScreen
                val homeViewModel: HomeViewModel = viewModel()
                HomeScreen(
                    appState = AppState(rememberNavController()),
                    homeViewModel = homeViewModel
                )
                // Inicializa o Firebase (apenas se não tiver sido feito em algum lugar central)
                if (FirebaseApp.getApps(this).isEmpty()) {
                    FirebaseApp.initializeApp(this)
                }

            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(appState: AppState, homeViewModel: HomeViewModel = viewModel()) {
    var showAddExpenseDialog by remember { mutableStateOf(false) }
    var selectedFilter by remember { mutableStateOf("Semana") }
    var currentDate by remember { mutableStateOf(System.currentTimeMillis()) }
    var selectedItems = remember { mutableStateListOf<Expense>() } // Lista de itens selecionados
    var showDeleteDialog by remember { mutableStateOf(false) } // Controle do diálogo de exclusão
    var showCheckboxes by remember { mutableStateOf(false) } // Controle da visibilidade do checkbox

    // Carrega as despesas do Firestore ao iniciar a tela
    LaunchedEffect(Unit) {
        homeViewModel.loadExpenses()
    }

    // Observe as despesas do ViewModel
    val expenses by homeViewModel.expenses.collectAsState()

    // Filtra despesas com base no período selecionado
    val filteredExpenses = filterExpensesByPeriod(expenses, currentDate, selectedFilter)

    NavigationSuiteScaffold(
        navigationSuiteItems = {
            topLevelDestinations.forEach { destination ->
                val selected = appState.isRouteInHierarchy(destination.route)
                item(
                    selected = selected,
                    icon = {
                        // Aplica animação de suavização no ícone
                        val animatedIconSize by animateFloatAsState(
                            targetValue = if (selected) 32f else 24f
                        )
                        Icon(
                            imageVector = if (selected) destination.selectedIcon else destination.unselectedIcon,
                            contentDescription = stringResource(destination.iconTextId),
                            tint = if (selected) destination.selectedIconColor else destination.unselectedIconColor,
                            modifier = Modifier.size(animatedIconSize.dp)
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
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.Top,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Barra superior da tela
            TopAppBar(
                title = {
                    Text("Home", fontWeight = FontWeight.Bold, fontSize = 20.sp)
                },
                actions = {
                    if (selectedItems.isNotEmpty()) {
                        IconButton(onClick = { showDeleteDialog = true }) {
                            Icon(imageVector = Icons.Filled.Delete, contentDescription = "Deletar")
                        }
                    }
                    IconButton(onClick = { showAddExpenseDialog = true }) {
                        Icon(imageVector = Icons.Filled.Add, contentDescription = "Adicionar")
                    }
                }
            )

            Scaffold {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(it)
                ) {
                    item {
                        // Exibição de saldo com animação de soma
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(48.dp)
                                .padding(start = 5.dp, end = 5.dp)
                        ) {
                            Column(Modifier.padding(start = 5.dp)) {
                                Text("Dinheiro", fontSize = 22.sp, fontWeight = FontWeight.Bold)
                                // Animação de transição suave do valor do saldo
                                val animatedBalance by animateFloatAsState(
                                    targetValue = expenses.sumOf { it.amount }.toFloat()
                                )
                                Text(
                                    "${formatCurrency(animatedBalance.toDouble())}",
                                    fontSize = 15.sp,
                                    fontWeight = FontWeight.Light
                                )
                            }
                            Spacer(modifier = Modifier.weight(1f))
                            Icon(
                                imageVector = Icons.Filled.Edit,
                                contentDescription = "Editar",
                                modifier = Modifier.size(30.dp)
                            )
                        }
                    }

                    item {
                        Spacer(modifier = Modifier.height(7.dp))
                        HomeWithGraph(
                            selectedFilter = selectedFilter,
                            currentDate = currentDate,
                            expensesData = expenses,
                            onDateChange = { newDate -> currentDate = newDate },
                            onFilterChange = { newFilter -> selectedFilter = newFilter }
                        )
                    }

                    items(filteredExpenses) { expense ->
                        ExpenseItem(
                            expense = expense,
                            isSelected = selectedItems.contains(expense),
                            onSelect = { isSelected ->
                                if (isSelected) {
                                    selectedItems.add(expense)
                                } else {
                                    selectedItems.remove(expense)
                                }
                            },
                            onLongPress = {
                                // Adiciona o item à lista de selecionados ao pressionar e segurar
                                if (!selectedItems.contains(expense)) {
                                    selectedItems.add(expense)
                                }
                                showCheckboxes = true
                            }
                        )
                    }
                }

                // Botão flutuante para adicionar nova despesa com animação
                Column(
                    Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.Bottom,
                    horizontalAlignment = Alignment.End
                ) {
                    BtnAdicionar(onClick = { showAddExpenseDialog = true })
                }

                // Diálogo para adicionar gasto ou ganho
                AddDialog(
                    showDialog = showAddExpenseDialog,
                    onDismiss = { showAddExpenseDialog = false },
                    homeViewModel = homeViewModel,
                    onAdd = { amount, type, transactionType, date ->
                        val newExpense = Expense(
                            id = "",
                            date = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).parse(date)?.time
                                ?: System.currentTimeMillis(),
                            amount = amount,
                            type = type
                        )
                        homeViewModel.addExpense(newExpense)
                        showAddExpenseDialog = false
                    }
                )
            }
        }

        if (showCheckboxes) {
            Modifier.pointerInput(Unit) {
                detectTapGestures(
                    onTap = {
                        showCheckboxes = false
                        selectedItems.clear()
                    }
                )
            }
        }
    }

    // Diálogo de confirmação para exclusão
    if (showDeleteDialog) {
        AlertDialog(
            onDismissRequest = { showDeleteDialog = false },
            title = { Text("Confirmar Exclusão") },
            text = { Text("Você tem certeza que deseja excluir os itens selecionados?") },
            confirmButton = {
                TextButton(onClick = {
                    selectedItems.forEach { expense ->
                        homeViewModel.deleteExpense(expense)
                    }
                    selectedItems.clear()
                    showDeleteDialog = false
                }) {
                    Text("Sim")
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
fun BtnAdicionar(onClick: () -> Unit) {
    ExtendedFloatingActionButton(
        onClick = { onClick() },
        icon = { Icon(Icons.Filled.Add, "Adicionar") },
        text = { Text(text = "Adicionar") },
        containerColor = Color(0xFFBD95FF),
        contentColor = Color.Black,
        elevation = FloatingActionButtonDefaults.elevation(8.dp),
        modifier = Modifier
            .padding(16.dp) // Adiciona um espaçamento ao redor do botão
    )
}

// Função principal com gráfico
@Composable
fun HomeWithGraph(
    selectedFilter: String,
    currentDate: Long,
    expensesData: List<Expense>,
    onDateChange: (Long) -> Unit,
    onFilterChange: (String) -> Unit
) {
    // Se nenhum filtro estiver selecionado, defina "Semana" como padrão
    val effectiveFilter = if (selectedFilter.isEmpty()) "Semana" else selectedFilter
    val filteredExpenses = filterExpensesByPeriod(expensesData, currentDate, effectiveFilter)

    Column(
        modifier = Modifier.fillMaxSize().padding(start = 5.dp, end = 5.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        FilterChip(
            selectedFilter = effectiveFilter,
            onFilterChange = onFilterChange
        )

        DateSelectors(
            currentDate = currentDate,
            selectedFilter = effectiveFilter,
            onDateChange = onDateChange,
        )

        Spacer(modifier = Modifier.height(10.dp))

        ElevatedCard(
            elevation = CardDefaults.cardElevation(
                defaultElevation = 3.dp
            ),
            modifier = Modifier
                .size(width = 500.dp, height = 310.dp)
                .padding(5.dp)
        ) {



        Column(modifier = Modifier.fillMaxWidth().padding(start = 15.dp,top=15.dp,end=15.dp), horizontalAlignment = Alignment.Start) {
            Text(
                text = "${filteredExpenses.sumOf { it.amount }}€",
                fontSize = 29.sp,
                fontWeight = FontWeight.Bold
            )
            Text(
                modifier = Modifier.padding(start = 7.dp, bottom = 5.dp),
                text = "Renda",
                fontSize = 14.sp,
                fontWeight = FontWeight.Medium
            )
        }

        Box(
            modifier = Modifier.fillMaxWidth().padding(start = 10.dp, end = 10.dp).height(200.dp),
            contentAlignment = Alignment.Center
        ) {
            BarChart(
                data = filteredExpenses.map { it.amount },
                selectedFilter = effectiveFilter,
                modifier = Modifier.fillMaxSize()
            )
        }
}
        Spacer(modifier = Modifier.height(15.dp))
    }
}
@Composable
fun BarChart(
    data: List<Double>, // Agora recebe uma lista de valores
    selectedFilter: String,
    modifier: Modifier = Modifier
) {
    val labels = when (selectedFilter) {
        "Semana" -> listOf("Seg", "Ter", "Qua", "Qui", "Sex", "Sáb", "Dom")
        "Mês" -> (1..4).map { "Semana $it" }
        "Ano" -> listOf(
            "Jan",
            "Fev",
            "Mar",
            "Abr",
            "Mai",
            "Jun",
            "Jul",
            "Ago",
            "Set",
            "Out",
            "Nov",
            "Dez"
        )
        else -> emptyList()
    }

    Canvas(modifier = modifier) {
        val maxY = data.maxOrNull() ?: 1.0
        val barSpacing = size.width / (data.size + 1)
        val barWidth = barSpacing * 0.6f

        data.forEachIndexed { index, value ->
            val barHeight = (value / maxY) * size.height
            val barLeft = barSpacing * (index + 1) - barWidth / 2
            val barTop = size.height - barHeight

            drawRect(
                color = Color.Green,
                topLeft = Offset(barLeft, barTop.toFloat()),
                size = Size(barWidth, barHeight.toFloat())
            )

            labels.getOrNull(index)?.let { label ->
                drawContext.canvas.nativeCanvas.drawText(
                    label,
                    (barLeft + barSpacing * (index + 1)) / 2,
                    size.height + 20.dp.toPx(),
                    android.graphics.Paint().apply {
                        textSize = 36f
                        color = android.graphics.Color.BLACK
                        textAlign = android.graphics.Paint.Align.CENTER
                    }
                )
            }
        }
    }
}



@Composable
fun FilterChip(
    selectedFilter: String,
    onFilterChange: (String) -> Unit
) {
    val options = listOf("Semana", "Mês", "Ano")
    var selectedOption by remember { mutableStateOf(selectedFilter) }

    LazyRow(
        modifier = Modifier.fillMaxWidth().padding(start = 5.dp),
        horizontalArrangement = Arrangement.Start
    ) {
        items(options) { option ->
            FilterChip(
                selected = selectedOption == option,
                onClick = {
                    selectedOption = option
                    onFilterChange(option)
                },
                label = { Text(option) },
                leadingIcon = if (selectedOption == option) {
                    {
                        Icon(
                            imageVector = Icons.Filled.Done,
                            contentDescription = "Done icon",
                            modifier = Modifier.size(FilterChipDefaults.IconSize)
                        )
                    }
                } else {
                    null
                }
            )
        }
    }
}

@Composable
fun DateSelectors(
    currentDate: Long,
    selectedFilter: String,
    onDateChange: (Long) -> Unit
) {
    val calendar = remember { Calendar.getInstance().apply { timeInMillis = currentDate } }

    val dateText = remember(selectedFilter, currentDate) {
        when (selectedFilter) {
            "Semana" -> {
                val startOfWeek = (calendar.clone() as Calendar).apply {
                    firstDayOfWeek = Calendar.MONDAY
                    set(Calendar.DAY_OF_WEEK, Calendar.MONDAY)
                }
                val endOfWeek = (calendar.clone() as Calendar).apply {
                    firstDayOfWeek = Calendar.MONDAY
                    set(Calendar.DAY_OF_WEEK, Calendar.SUNDAY)
                }
                "${startOfWeek.get(Calendar.DAY_OF_MONTH)} - ${endOfWeek.get(Calendar.DAY_OF_MONTH)} " +
                        startOfWeek.getDisplayName(Calendar.MONTH, Calendar.LONG, Locale.getDefault())
            }

            "Mês" -> SimpleDateFormat("MMMM 'de' yyyy", Locale.getDefault()).format(calendar.time)
            "Ano" -> calendar.get(Calendar.YEAR).toString()
            else -> SimpleDateFormat("dd 'de' MMMM 'de' yyyy", Locale.getDefault()).format(calendar.time)
        }
    }

    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center,
        modifier = Modifier.fillMaxWidth()
    ) {
        IconButton(onClick = {
            when (selectedFilter) {
                "Semana" -> calendar.add(Calendar.WEEK_OF_YEAR, -1)
                "Mês" -> calendar.add(Calendar.MONTH, -1)
                "Ano" -> calendar.add(Calendar.YEAR, -1)
            }
            onDateChange(calendar.timeInMillis)
        }) {
            Icon(imageVector = Icons.Filled.ArrowBackIosNew, contentDescription = "Retroceder")
        }

        Text(
            text = dateText,
            fontSize = 16.sp,
            fontWeight = FontWeight.Medium,
            modifier = Modifier.padding(horizontal = 16.dp)
        )

        IconButton(onClick = {
            when (selectedFilter) {
                "Semana" -> calendar.add(Calendar.WEEK_OF_YEAR, 1)
                "Mês" -> calendar.add(Calendar.MONTH, 1)
                "Ano" -> calendar.add(Calendar.YEAR, 1)
            }
            onDateChange(calendar.timeInMillis)
        }) {
            Icon(imageVector = Icons.Filled.ArrowForwardIos, contentDescription = "Avançar")
        }
    }
}


fun getWeeksInMonth(firstDayOfMonth: Calendar): List<Pair<Int, Int>> {
    val weeks = mutableListOf<Pair<Int, Int>>()
    val calendar = firstDayOfMonth.clone() as Calendar
    calendar.set(Calendar.DAY_OF_MONTH, 1)

    while (calendar.get(Calendar.MONTH) == firstDayOfMonth.get(Calendar.MONTH)) {
        val startDay = calendar.get(Calendar.DAY_OF_MONTH)
        calendar.add(Calendar.DATE, 6) // Move para o último dia da semana

        val endDay = if (calendar.get(Calendar.MONTH) == firstDayOfMonth.get(Calendar.MONTH)) {
            calendar.get(Calendar.DAY_OF_MONTH)
        } else {
            firstDayOfMonth.getActualMaximum(Calendar.DAY_OF_MONTH) // Último dia do mês
        }

        weeks.add(Pair(startDay, endDay))
        calendar.add(Calendar.DATE, 1) // Move para o próximo dia
    }

    return weeks
}

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
    // Crie um AppState simulado
    val navController = rememberNavController()
    // Chame a HomeScreen com o estado simulado
    HomeScreen(appState = AppState(navController))
}