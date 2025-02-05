package com.example.mony.feature.home

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
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
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBackIosNew
import androidx.compose.material.icons.filled.ArrowForwardIos
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Done
import androidx.compose.material.icons.filled.Hardware
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
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
import androidx.compose.material3.TopAppBarDefaults.topAppBarColors
import androidx.compose.material3.adaptive.navigationsuite.NavigationSuiteScaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.nativeCanvas
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
import androidx.navigation.compose.rememberNavController
import com.example.mony.R
import com.example.mony.feature.home.classe.Expense
import com.example.mony.feature.home.classe.ExpenseItem
import com.example.mony.feature.home.dialog.AddDialog
import com.example.mony.feature.home.viewmodel.HomeViewModel
import com.example.mony.feature.utils.AppState
import com.example.mony.feature.utils.navegation.MyApp
import com.example.mony.feature.utils.navegation.topLevelDestinations
import com.example.mony.ui.theme.AmareloClaro
import com.example.mony.ui.theme.AmareloDark
import com.example.mony.ui.theme.AmareloMC
import com.example.mony.ui.theme.AmareloMedio
import com.example.mony.ui.theme.Gray
import com.example.mony.ui.theme.GrayLight
import com.example.mony.ui.theme.GreenLight
import com.example.mony.ui.theme.RedLight
import com.example.mony.ui.theme.White
import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale
import kotlin.math.max


class HomeActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            Surface {
                MyApp()
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class)
@Composable
fun HomeScreen(appState: AppState, homeViewModel: HomeViewModel = viewModel()) {
    var showAddExpenseDialog by remember { mutableStateOf(false) }
    var selectedFilter by remember { mutableStateOf("Semana") }
    var currentDate by remember { mutableLongStateOf(System.currentTimeMillis()) }
    val selectedItems = remember { mutableStateListOf<Expense>() }
    var showDeleteDialog by remember { mutableStateOf(false) }
    var showCheckboxes by remember { mutableStateOf(false) }
    val expenses by homeViewModel.expenses.collectAsState()

    LaunchedEffect(Unit) {
        if (expenses.isEmpty()) {
            homeViewModel.loadExpenses()
        }
    }

    val filteredExpenses = filterExpensesByPeriod(expenses, currentDate, selectedFilter)

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
                    onClick = { appState.navigateToTopLevelDestination(destination.route) }
                )
            }
        }
    ) {
        Column(
            modifier = Modifier.fillMaxSize().background(White),
            verticalArrangement = Arrangement.Top,
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            TopAppBar(
                modifier = Modifier.padding(bottom = 30.dp),
                title = {},
                actions = {
                    if (selectedItems.isNotEmpty()) {
                        IconButton(onClick = { showDeleteDialog = true }) {
                            Icon(imageVector = Icons.Filled.Delete, contentDescription = "Deletar")
                        }
                    }
                    IconButton(onClick = { showAddExpenseDialog = true }) {
                        Icon(imageVector = Icons.Filled.Hardware, contentDescription = "Adicionar")
                    }
                },
                colors = topAppBarColors(White)
            )


            Scaffold {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(it)
                        .background(White)
                ) {
                    item {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(90.dp)
                                .padding(start = 10.dp, end = 10.dp)
                        ) {
                            Card(
                                colors = CardDefaults.cardColors(White),
                                modifier = Modifier
                                    .width(170.dp)
                                    .fillMaxHeight()
                                    .align(Alignment.CenterVertically)
                                    .border(
                                        width = 2.dp, // Espessura do contorno
                                        color = AmareloClaro, // Cor do contorno
                                        shape = RoundedCornerShape(12.dp) // Forma do contorno (arredondada aqui)
                                    ),
                                elevation = CardDefaults.cardElevation(2.dp)

                            ) {


                                Column(Modifier.fillMaxSize(), horizontalAlignment = Alignment.CenterHorizontally) {
                                    val animatedBalance by animateFloatAsState(
                                        targetValue = expenses.sumOf { it.amount }.toFloat()
                                    )
                                    Row(
                                        modifier=Modifier.fillMaxWidth()
                                    ){

                                    Spacer(modifier = Modifier.width(30.dp).padding(top=5.dp))

                                    Image(
                                        painter = painterResource(id = R.drawable.expense),
                                        contentDescription = "Expense",
                                        modifier = Modifier.size(15.dp).align(Alignment.CenterVertically)
                                    )

                                        Spacer(modifier = Modifier.width(5.dp))

                                    Text(
                                        "Renda",
                                        fontSize = 15.sp,
                                        color = GrayLight,
                                        fontWeight = FontWeight.Bold,
                                        modifier=Modifier.padding(top=10.dp)
                                    )
                                    }
                                    Text(
                                        formatCurrency(expenses.filter { it.type.isIncome }.sumOf { it.amount }),
                                        fontSize = 29.sp,
                                        fontWeight = FontWeight.Medium,
                                        color = GreenLight,
                                        textAlign = TextAlign.Center,
                                        modifier = Modifier.padding()

                                    )
                                }
                            }

                            Spacer(modifier = Modifier.width(35.dp))

                            Card(
                                colors = CardDefaults.cardColors(White),
                                modifier = Modifier
                                    .width(170.dp)
                                    .fillMaxHeight()
                                    .padding(end = 5.dp)
                                    .background(White)
                                    .align(Alignment.CenterVertically)
                                    .border(
                                        width = 2.dp, // Espessura do contorno
                                        color = AmareloClaro, // Cor do contorno
                                        shape = RoundedCornerShape(12.dp) // Forma do contorno (arredondada aqui)
                                    ),
                                elevation = CardDefaults.cardElevation(2.dp)
                            ) {

                                Column(
                                    Modifier.fillMaxSize(),
                                    horizontalAlignment = Alignment.CenterHorizontally
                                ) {

                                    val totalIncome = remember(expenses) {
                                        expenses.filter { it.type.isIncome }.sumOf { it.amount }
                                    }
                                    val totalExpenses = remember(expenses) {
                                        expenses.filter { !it.type.isIncome }.sumOf { it.amount }
                                    }

                                    val animatedIncome by animateFloatAsState(totalIncome.toFloat())
                                    val animatedExpense by animateFloatAsState(totalExpenses.toFloat())

                                    Row(
                                        modifier = Modifier.fillMaxWidth()
                                    ) {

                                        Spacer(modifier = Modifier.width(30.dp).padding(top=5.dp))

                                        Image(
                                            painter = painterResource(id = R.drawable.income),
                                            contentDescription = "Expense",
                                            modifier = Modifier.size(15.dp)
                                                .align(Alignment.CenterVertically)
                                        )

                                        Spacer(modifier = Modifier.width(5.dp))

                                        Text(
                                            "Gasto",
                                            fontSize = 15.sp,
                                            color = GrayLight,
                                            fontWeight = FontWeight.Bold,
                                            modifier = Modifier.padding(top = 10.dp)
                                        )
                                    }
                                    Text(
                                        formatCurrency(expenses.filter { !it.type.isIncome }
                                            .sumOf { it.amount }),
                                        fontSize = 29.sp,
                                        fontWeight = FontWeight.Medium,
                                        color = RedLight,
                                        modifier = Modifier
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
                        val isSelected = selectedItems.contains(expense)
                        val alpha by animateFloatAsState(if (isSelected) 0.5f else 1f)

                        AnimatedVisibility(
                            visible = true,
                            enter = fadeIn() + slideInVertically(),
                            exit = fadeOut() + slideOutVertically()
                        ) {
                            Box(
                                modifier = Modifier
                                    .graphicsLayer(alpha = alpha)
                                    .combinedClickable(
                                        onClick = {
                                            if (showCheckboxes) {
                                                if (isSelected) selectedItems.remove(expense)
                                                else selectedItems.add(expense)
                                            }
                                        },
                                        onLongClick = {
                                            if (!isSelected) {
                                                selectedItems.add(expense)
                                                showCheckboxes = true
                                            }
                                        }
                                    )
                            ) {
                                ExpenseItem(
                                    expense = expense,
                                    isSelected = isSelected,
                                    onSelect = { selected ->
                                        if (selected) selectedItems.add(expense)
                                        else selectedItems.remove(expense)
                                    },
                                    onLongPress = {
                                        if (!isSelected) {
                                            selectedItems.add(expense)
                                            showCheckboxes = true
                                        }
                                    }
                                )
                            }
                        }
                    }
                }

                Column(
                    Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.Bottom,
                    horizontalAlignment = Alignment.End
                ) {
                    var buttonState by remember { mutableStateOf(false) }
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
                        modifier = Modifier.offset(y = floatAnimation.dp)
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
                        // Chama o método addExpense do ViewModel para adicionar a despesa
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

    if (showDeleteDialog) {
        AlertDialog(
            onDismissRequest = { showDeleteDialog = false },
            title = { Text("Confirmar Exclusão") },
            text = { Text("Deseja excluir os itens selecionados?") },
            confirmButton = {
                TextButton(onClick = {
                    selectedItems.forEach { homeViewModel.deleteExpense(it.id) }
                    selectedItems.clear()
                    showDeleteDialog = false
                }) { Text("Sim") }
            },
            dismissButton = { TextButton(onClick = { showDeleteDialog = false }) { Text("Não") } }
        )
    }
}


@Composable
fun BtnAdicionar(onClick: () -> Unit, modifier: Modifier = Modifier) {
    var rotated by remember { mutableStateOf(false) }
    val rotationAngle by animateFloatAsState(
        targetValue = if (rotated) 360f else 0f,
        animationSpec = tween(durationMillis = 600)
    )

    ExtendedFloatingActionButton(
        onClick = {
            rotated = !rotated  // Animação de rotação
            onClick()
        },
        icon = {
            Icon(
                imageVector = Icons.Filled.Add,
                contentDescription = "Adicionar",
                modifier = Modifier.rotate(rotationAngle)
            )
        },
        text = { Text(text = "Adicionar") },
        containerColor = AmareloClaro,
        contentColor = Color.Black,
        elevation = FloatingActionButtonDefaults.elevation(8.dp),
        modifier = modifier
            .padding(16.dp)
            .border(
                width = 2.dp,
                color = AmareloMC,
                shape = RoundedCornerShape(12.dp)
            ),
    )
}



@Composable
fun HomeWithGraph(
    selectedFilter: String,
    currentDate: Long,
    expenses: List<Expense>,
    onDateChange: (Long) -> Unit,
    onFilterChange: (String) -> Unit
) {
    val effectiveFilter = if (selectedFilter.isEmpty()) "Semana" else selectedFilter

    val chartData = remember(currentDate, effectiveFilter, expenses) {
        getChartData(
            expenses = expenses,
            currentDate = currentDate,
            filter = effectiveFilter
        )
    }

    Column(
        modifier = Modifier.fillMaxSize().padding(start = 5.dp, end = 5.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        DateSelectors(
            currentDate = currentDate,
            selectedFilter = effectiveFilter,
            onDateChange = onDateChange,
        )


        Spacer(modifier = Modifier.height(3.dp))

        ElevatedCard(
            modifier = Modifier
                .size(width = 600.dp, height = 310.dp)
                .padding(5.dp)
                .border(2.dp, AmareloClaro, RoundedCornerShape(12.dp)),
            colors = CardDefaults.cardColors(White),

        ) {
            Column(Modifier.padding(15.dp)) {
                Spacer(modifier = Modifier.height(8.dp))

                BarChart(
                    data = chartData,
                    selectedFilter = effectiveFilter,
                    modifier = Modifier.fillMaxSize()
                )
            }
        }
        Spacer(modifier = Modifier.height(6.dp))
        FilterChip(
            selectedFilter = effectiveFilter,
            onFilterChange = onFilterChange
        )
    }
}

@Composable
fun BarChart(
    data: List<Pair<Double, Double>>, // Pair<Ganhos, Perdas>
    selectedFilter: String,
    modifier: Modifier = Modifier
) {
    val labels = when (selectedFilter) {
        "Semana" -> listOf("Seg", "Ter", "Qua", "Qui", "Sex", "Sáb", "Dom")
        "Mês" -> (1..4).map { "Semana $it" }
        "Ano" -> listOf("Jan", "Fev", "Mar", "Abr", "Mai", "Jun", "Jul", "Ago", "Set", "Out", "Nov", "Dez")
        else -> emptyList()
    }

    var selectedBarIndex by remember { mutableStateOf<Int?>(null) }
    val tooltipPosition by remember { mutableStateOf(Offset.Zero) }

    Box(modifier = modifier) {
        Canvas(
            modifier = Modifier
                .fillMaxSize()
                .pointerInput(Unit) {
                    detectTapGestures { tapOffset ->
                        val canvasWidth = size.width
                        val canvasHeight = size.height
                        val barSpacing = canvasWidth / (data.size + 1)
                        val barWidth = barSpacing * 0.4f

                        selectedBarIndex = data.indices.firstOrNull { index ->
                            val barLeft = barSpacing * (index + 1) - barWidth / 2
                            val barRight = barLeft + barWidth
                            tapOffset.x in barLeft..barRight
                        }
                    }
                }
        ) {
            val canvasWidth = size.width
            val canvasHeight = size.height
            val baseline = canvasHeight / 2

            // Linha de base
            drawLine(
                color = Color.Gray,
                start = Offset(0f, baseline),
                end = Offset(canvasWidth, baseline),
                strokeWidth = 2f
            )

            val maxGain = data.maxOfOrNull { it.first } ?: 1.0
            val maxLoss = data.maxOfOrNull { it.second } ?: 1.0
            val maxValue = max(maxGain, maxLoss)

            data.forEachIndexed { index, (gain, loss) ->
                val barSpacing = canvasWidth / (data.size + 1)
                val barWidth = barSpacing * 0.4f
                val barLeft = barSpacing * (index + 1) - barWidth / 2

                // Ganhos (acima da linha)
                val gainHeight = (gain / maxValue) * (baseline - 20f)
                drawRect(
                    color = GreenLight,
                    topLeft = Offset(barLeft, (baseline - gainHeight).toFloat()),
                    size = Size(barWidth, gainHeight.toFloat())
                )

                // Perdas (abaixo da linha)
                val lossHeight = (loss / maxValue) * (baseline - 20f)
                drawRect(
                    color = RedLight,
                    topLeft = Offset(barLeft, baseline),
                    size = Size(barWidth, lossHeight.toFloat())
                )

                // Rótulos
                labels.getOrNull(index)?.let { label ->
                    drawContext.canvas.nativeCanvas.drawText(
                        label,
                        barLeft + barWidth / 2,
                        canvasHeight - 8.dp.toPx(),
                        android.graphics.Paint().apply {
                            textSize = 36f
                            color = android.graphics.Color.BLACK
                            textAlign = android.graphics.Paint.Align.CENTER
                        }
                    )
                }
            }
        }

        // Tooltip
        selectedBarIndex?.let { index ->
            val (gain, loss) = data[index]
            Box(
                modifier = Modifier
                    .offset(tooltipPosition.x.dp, tooltipPosition.y.dp)
                    .background(Color(0xCC6200EE), RoundedCornerShape(4.dp))
                    .padding(8.dp)
            ) {
                Column {
                    Text("Ganhos: ${formatCurrency(gain)}", color = Color.White)
                    Text("Perdas: ${formatCurrency(loss)}", color = Color.White)
                }
            }
        }
    }
}

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

                //design
                colors = FilterChipDefaults.filterChipColors(selectedContainerColor = AmareloMedio),
                border = BorderStroke(
                    width = 1.dp,
                color = if (selectedOption == option) {
                    AmareloDark
                } else {
                    Gray
                },
                ),
                //

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
                },

            )
        }
    }
}

fun getChartData(
    expenses: List<Expense>,
    currentDate: Long,
    filter: String
): List<Pair<Double, Double>> {
    val calendar = Calendar.getInstance().apply {
        timeInMillis = currentDate
        firstDayOfWeek = Calendar.MONDAY
    }

    return when (filter) {
        "Semana" -> handleWeek(calendar, expenses)
        "Mês" -> handleMonth(calendar, expenses)
        "Ano" -> handleYear(calendar, expenses)
        else -> emptyList()
    }
}

private fun handleWeek(calendar: Calendar, expenses: List<Expense>): List<Pair<Double, Double>> {
    val baseCalendar = calendar.cloneAsSafe()
    baseCalendar.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY)

    return (0..6).map { dayOffset ->
        val dayCal = baseCalendar.cloneAsSafe().apply {
            add(Calendar.DAY_OF_YEAR, dayOffset)
            setToDayStart()
        }

        val endCal = dayCal.cloneAsSafe().apply {
            add(Calendar.DAY_OF_YEAR, 1)
            add(Calendar.MILLISECOND, -1)
        }

        calculateDailyValues(expenses, dayCal.timeInMillis, endCal.timeInMillis)
    }
}

private fun handleMonth(calendar: Calendar, expenses: List<Expense>): List<Pair<Double, Double>> {
    val monthCal = calendar.cloneAsSafe().apply {
        set(Calendar.DAY_OF_MONTH, 1)
        setToDayStart()
    }

    val endOfMonth = monthCal.getActualMaximum(Calendar.DAY_OF_MONTH)
    val weeks = mutableListOf<Pair<Double, Double>>()

    var currentWeek = 1
    while (currentWeek <= 6) { // Máximo de 6 semanas para cobrir todos os cenários
        val startCal = monthCal.cloneAsSafe().apply {
            set(Calendar.WEEK_OF_MONTH, currentWeek)
            set(Calendar.DAY_OF_WEEK, Calendar.MONDAY)
            setToDayStart()
        }

        // Garantir que não ultrapasse o mês
        if (startCal.get(Calendar.MONTH) != monthCal.get(Calendar.MONTH)) break

        val endCal = startCal.cloneAsSafe().apply {
            add(Calendar.DAY_OF_YEAR, 6)
            set(Calendar.HOUR_OF_DAY, 23)
            set(Calendar.MINUTE, 59)
            set(Calendar.SECOND, 59)
            set(Calendar.MILLISECOND, 999)
        }

        // Ajustar para não passar o final do mês
        val adjustedEnd = minOf(endCal.timeInMillis, monthCal.apply {
            set(Calendar.DAY_OF_MONTH, endOfMonth)
        }.timeInMillis)

        weeks.add(calculateWeeklyValues(expenses, startCal.timeInMillis, adjustedEnd))
        currentWeek++
    }

    return weeks
}

private fun handleYear(calendar: Calendar, expenses: List<Expense>): List<Pair<Double, Double>> {
    return (0..11).map { monthOffset ->
        val monthCal = calendar.cloneAsSafe().apply {
            set(Calendar.MONTH, calendar.get(Calendar.MONTH) + monthOffset)
            set(Calendar.DAY_OF_MONTH, 1)
            setToDayStart()
        }

        val endCal = monthCal.cloneAsSafe().apply {
            set(Calendar.DAY_OF_MONTH, getActualMaximum(Calendar.DAY_OF_MONTH))
            set(Calendar.HOUR_OF_DAY, 23)
            set(Calendar.MINUTE, 59)
            set(Calendar.SECOND, 59)
            set(Calendar.MILLISECOND, 999)
        }

        calculateMonthlyValues(expenses, monthCal.timeInMillis, endCal.timeInMillis)
    }
}

// Funções auxiliares
private fun Calendar.cloneAsSafe(): Calendar {
    return this.clone() as Calendar
}

private fun Calendar.setToDayStart() {
    set(Calendar.HOUR_OF_DAY, 0)
    set(Calendar.MINUTE, 0)
    set(Calendar.SECOND, 0)
    set(Calendar.MILLISECOND, 0)
}

private fun calculateDailyValues(
    expenses: List<Expense>,
    start: Long,
    end: Long
): Pair<Double, Double> {
    val filtered = expenses.filter { it.date in start..end }
    return Pair(
        filtered.filter { it.type.isIncome }.sumOf { it.amount },
        filtered.filter { !it.type.isIncome }.sumOf { it.amount }
    )
}

private fun calculateWeeklyValues(
    expenses: List<Expense>,
    start: Long,
    end: Long
): Pair<Double, Double> {
    val filtered = expenses.filter { it.date in start..end }
    return Pair(
        filtered.filter { it.type.isIncome }.sumOf { it.amount },
        filtered.filter { !it.type.isIncome }.sumOf { it.amount }
    )
}

private fun calculateMonthlyValues(
    expenses: List<Expense>,
    start: Long,
    end: Long
): Pair<Double, Double> {
    val filtered = expenses.filter { it.date in start..end }
    return Pair(
        filtered.filter { it.type.isIncome }.sumOf { it.amount },
        filtered.filter { !it.type.isIncome }.sumOf { it.amount }
    )
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

    // Animação de opacidade
    val alpha by animateFloatAsState(
        targetValue = if (currentDate > 0) 1f else 0f,
        animationSpec = tween(durationMillis = 300)
    )

    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center,
        modifier = Modifier.fillMaxWidth().padding(top = 10.dp, bottom = 5.dp)
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

        // Aplicando a animação de opacidade ao texto da data
        Text(
            text = dateText,
            fontSize = 16.sp,
            fontWeight = FontWeight.Medium,
            modifier = Modifier
                .padding(horizontal = 16.dp)
                .graphicsLayer(alpha = alpha) // Aplicando a opacidade
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
    // Usando um NavController simples
    val navController = rememberNavController()
    // Criando uma instância do AppState
    val appState = AppState(navController)

    HomeScreen(appState = AppState(navController = rememberNavController()), homeViewModel = HomeViewModel())
}