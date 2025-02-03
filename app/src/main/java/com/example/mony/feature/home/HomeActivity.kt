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
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
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
import androidx.compose.material3.ListItemDefaults.contentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SelectableChipColors
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults.topAppBarColors
import androidx.compose.material3.adaptive.navigationsuite.NavigationSuiteItemColors
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
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.glance.GlanceTheme.colors
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.rememberNavController
import com.example.mony.feature.home.classe.Expense
import com.example.mony.feature.home.classe.ExpenseItem
import com.example.mony.feature.home.dialog.AddDialog
import com.example.mony.feature.home.viewmodel.HomeViewModel
import com.example.mony.feature.utils.AppState
import com.example.mony.feature.utils.navegation.MyApp
import com.example.mony.feature.utils.navegation.topLevelDestinations
import com.example.mony.ui.theme.Amarelo
import com.example.mony.ui.theme.AmareloClaro
import com.example.mony.ui.theme.AmareloClaro2
import com.example.mony.ui.theme.AmareloDark
import com.example.mony.ui.theme.AmareloMC
import com.example.mony.ui.theme.AmareloMedio
import com.example.mony.ui.theme.Gray
import com.example.mony.ui.theme.OrangeLight
import com.example.mony.ui.theme.White
import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class HomeActivity : ComponentActivity() {
    private lateinit var homeViewModel: HomeViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        homeViewModel = HomeViewModel()
        setContent {
            Surface {
                MyApp()
            }
        }
    }
}@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(appState: AppState, homeViewModel: HomeViewModel = viewModel()) {
    var showAddExpenseDialog by remember { mutableStateOf(false) }
    var selectedFilter by remember { mutableStateOf("Semana") }
    var currentDate by remember { mutableStateOf(System.currentTimeMillis()) }
    val selectedItems = remember { mutableStateListOf<Expense>() }
    var showDeleteDialog by remember { mutableStateOf(false) }
    var showCheckboxes by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        homeViewModel.loadExpenses()
    }

    val expenses by homeViewModel.expenses.collectAsState()
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
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.Top,
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            TopAppBar(
                title = {},
                actions = {
                    if (selectedItems.isNotEmpty()) {
                        IconButton(onClick = { showDeleteDialog = true }) {
                            Icon(imageVector = Icons.Filled.Delete, contentDescription = "Deletar")
                        }
                    }
                    IconButton(onClick = { showAddExpenseDialog = true }) {
                        Icon(imageVector = Icons.Filled.Add, contentDescription = "Adicionar")
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
                                .height(48.dp)
                                .padding(start = 5.dp, end = 5.dp)
                        ) {
                            Column(Modifier.padding(start = 5.dp)) {
                                Text("Dinheiro", fontSize = 23.sp, fontWeight = FontWeight.Bold)
                                val animatedBalance by animateFloatAsState(
                                    targetValue = expenses.sumOf { it.amount }.toFloat()
                                )
                                Text(
                                    formatCurrency(animatedBalance.toDouble()),
                                    fontSize = 15.sp,
                                    fontWeight = FontWeight.Light
                                )
                            }
                            Spacer(modifier = Modifier.weight(1f))
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

                    items(filteredExpenses, key = { it.id }) { expense ->
                        AnimatedVisibility(
                            visible = true, // Aqui você pode condicionar a visibilidade se desejar
                            enter = fadeIn(animationSpec = tween(durationMillis = 300)) + slideInVertically(),
                            exit = fadeOut(animationSpec = tween(durationMillis = 300)) + slideOutVertically()
                        ) {
                            // Animação de opacidade para itens de despesa já existente (mantém o seu alpha)
                            val alpha by animateFloatAsState(
                                targetValue = if (selectedItems.contains(expense)) 0.5f else 1f,
                                animationSpec = tween(durationMillis = 300)
                            )
                            Box(modifier = Modifier.graphicsLayer(alpha = alpha)) {
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
                                        if (!selectedItems.contains(expense)) {
                                            selectedItems.add(expense)
                                        }
                                        showCheckboxes = true
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

    if (showDeleteDialog) {
        AlertDialog(
            onDismissRequest = { showDeleteDialog = false },
            title = { Text("Confirmar Exclusão") },
            text = { Text("Deseja excluir os itens selecionados?") },
            confirmButton = {
                TextButton(onClick = {
                    selectedItems.forEach { homeViewModel.deleteExpense(it) }
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
    expensesData: List<Expense>,
    onDateChange: (Long) -> Unit,
    onFilterChange: (String) -> Unit
) {
    // Se nenhum filtro estiver selecionado, defina "Semana" como padrão
    val effectiveFilter = if (selectedFilter.isEmpty()) "Semana" else selectedFilter
    val filteredExpenses = filterExpensesByPeriod(expensesData, currentDate, effectiveFilter)

    // Animação para o total de despesas
    val totalAmount = filteredExpenses.sumOf { it.amount }
    val animatedTotal by animateFloatAsState(
        targetValue = totalAmount.toFloat(),
        animationSpec = tween(durationMillis = 500)
    )

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
            colors = CardDefaults.cardColors(White),
            elevation = CardDefaults.cardElevation(
                defaultElevation = 3.dp,
            ),
            modifier = Modifier
                .size(width = 500.dp, height = 310.dp)
                .padding(5.dp)
                .border(
                    width = 2.dp, // Espessura do contorno
                    color = AmareloClaro, // Cor do contorno
                    shape = RoundedCornerShape(12.dp) // Forma do contorno (arredondada aqui)
                ),
        ) {
            Column(modifier = Modifier.fillMaxWidth().padding(start = 15.dp, top = 15.dp, end = 15.dp), horizontalAlignment = Alignment.Start) {
                Text(
                    text = String.format("%.2f€", animatedTotal), // Formatação para duas casas decimais
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
                // Animação do gráfico
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
    data: List<Double>,
    selectedFilter: String,
    barColor: Color = Color.Green,
    modifier: Modifier = Modifier
) {
    // Rótulos conforme o filtro selecionado
    val labels = when (selectedFilter) {
        "Semana" -> listOf("Seg", "Ter", "Qua", "Qui", "Sex", "Sáb", "Dom")
        "Mês" -> (1..4).map { "Semana $it" }
        "Ano" -> listOf("Jan", "Fev", "Mar", "Abr", "Mai", "Jun", "Jul", "Ago", "Set", "Out", "Nov", "Dez")
        else -> emptyList()
    }

    // Animação das alturas de cada barra
    val animatedData = data.map { value ->
        animateFloatAsState(
            targetValue = value.toFloat(),
            animationSpec = tween(durationMillis = 500)
        ).value
    }

    // Estado para armazenar o índice da barra selecionada (tooltip)
    var selectedBarIndex by remember { mutableStateOf<Int?>(null) }
    // Estado para armazenar a posição do tooltip (calculada fora do Canvas)
    var tooltipPosition by remember { mutableStateOf(Offset.Zero) }

    Box(modifier = modifier) {
        // Canvas desenha as barras e detecta toques
        Canvas(
            modifier = Modifier
                .fillMaxSize()
                .pointerInput(Unit) {
                    detectTapGestures { tapOffset ->
                        var barraSelecionada: Int? = null
                        val canvasWidth = size.width
                        val canvasHeight = size.height
                        val barSpacing = canvasWidth / (data.size + 1)
                        val barWidth = barSpacing * 0.6f

                        data.forEachIndexed { index, _ ->
                            val value = animatedData.getOrNull(index) ?: 0f
                            val maxValue = data.maxOrNull() ?: 1.0
                            val barHeight = (value / maxValue * canvasHeight)
                            val barLeft = barSpacing * (index + 1) - barWidth / 2
                            val barTop = canvasHeight - barHeight

                            if (tapOffset.x in barLeft..(barLeft + barWidth) &&
                                tapOffset.y in barTop..canvasHeight.toDouble()
                            ) {
                                barraSelecionada = index
                                // Calcula a posição do tooltip (centralizado acima da barra)
                                tooltipPosition = Offset(barLeft + barWidth / 2, barTop.toFloat())
                            }
                        }
                        selectedBarIndex = barraSelecionada
                    }
                }
        ) {
            val canvasWidth = size.width
            val canvasHeight = size.height
            val barSpacing = canvasWidth / (data.size + 1)
            val barWidth = barSpacing * 0.6f
            val maxValue = data.maxOrNull() ?: 1.0

            animatedData.forEachIndexed { index, value ->
                val barHeight = (value / maxValue * canvasHeight)
                val barLeft = barSpacing * (index + 1) - barWidth / 2
                val barTop = canvasHeight - barHeight

                // Desenha a barra
                drawRect(
                    color = barColor,
                    topLeft = Offset(barLeft, barTop.toFloat()),
                    size = Size(barWidth, barHeight.toFloat())
                )

                // Desenha o rótulo do eixo X abaixo de cada barra
                labels.getOrNull(index)?.let { label ->
                    drawContext.canvas.nativeCanvas.drawText(
                        label,
                        barLeft + barWidth / 2,
                        canvasHeight + 20.dp.toPx(),
                        android.graphics.Paint().apply {
                            textSize = 36f
                            color = android.graphics.Color.BLACK
                            textAlign = android.graphics.Paint.Align.CENTER
                        }
                    )
                }
            }
        }

        // Exibe o tooltip fora do Canvas (na camada superior do Box)
        selectedBarIndex?.let { index ->
            AnimatedVisibility(
                visible = true,
                enter = fadeIn(animationSpec = tween(300)) + scaleIn(animationSpec = tween(300)),
                exit = fadeOut(animationSpec = tween(300)) + scaleOut(animationSpec = tween(300))
            ) {
                Box(
                    modifier = Modifier
                        .offset {
                            // Posiciona o tooltip um pouco acima da barra
                            IntOffset(
                                tooltipPosition.x.toInt() - 40, // ajuste horizontal (metade da largura do tooltip)
                                (tooltipPosition.y - 60).toInt() // ajuste vertical para que o tooltip fique acima
                            )
                        }
                        .background(Color(0xFF6200EE), shape = RoundedCornerShape(8.dp))
                        .padding(8.dp)
                ) {
                    // Exibe o valor original da barra, não o animado
                    Text(
                        text = data[index].toString(),
                        color = Color.White,
                        style = TextStyle(fontWeight = FontWeight.Bold, fontSize = 16.sp)
                    )
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
    // Crie um AppState simulado
    val navController = rememberNavController()
    // Chame a HomeScreen com o estado simulado
    HomeScreen(appState = AppState(navController))
}