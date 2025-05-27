package com.example.mony.feature.home.logicaGrafico

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBackIosNew
import androidx.compose.material.icons.filled.ArrowForwardIos
import androidx.compose.material.icons.filled.Done
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.drawText
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.rememberTextMeasurer
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.mony.feature.home.classe.Expense
import com.example.mony.feature.home.formatCurrency
import com.example.mony.ui.theme.Gray
import com.example.mony.ui.theme.GreenLight
import com.example.mony.ui.theme.RedLight
import java.util.Calendar
import java.util.Locale


//Grafico Principal
@Composable
fun HomeWithGraph(
    selectedFilter: String,
    currentDate: Long,
    expenses: List<Expense>,
    onDateChange: (Long) -> Unit,
    onFilterChange: (String) -> Unit
) {
    val effectiveFilter = if (selectedFilter.isEmpty()) "Semana" else selectedFilter

    val chartData = remember (currentDate, effectiveFilter, expenses) {
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
                .border(2.dp, MaterialTheme.colorScheme.secondary, RoundedCornerShape(12.dp)),
            colors = CardDefaults.cardColors(MaterialTheme.colorScheme.background),

            ) {
            Column(Modifier.padding(5.dp)) {
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

//Barras do grafico
@Composable
fun BarChart(
    data: List<Pair<Double, Double>>,
    selectedFilter: String,
    modifier: Modifier = Modifier
) {
    // Define os labels conforme o filtro selecionado.
    val labels by remember(selectedFilter) {
        derivedStateOf {
            when (selectedFilter) {
                "Semana" -> listOf("Seg", "Ter", "Qua", "Qui", "Sex", "Sáb", "Dom")
                "Mês" -> (1..5).map { "Semana $it" }
                "Ano" -> listOf(
                    "Jan", "Fev", "Mar", "Abr", "Mai", "Jun",
                    "Jul", "Ago", "Set", "Out", "Nov", "Dez"
                )
                else -> emptyList()
            }
        }
    }

    // Estado para a barra selecionada e posição do tooltip.
    var selectedBarIndex by rememberSaveable { mutableStateOf<Int?>(null) }
    val tooltipPosition = remember { mutableStateOf(Offset.Zero) }
    val textMeasurer = rememberTextMeasurer()

    // Animação global para o "reveal" do gráfico.
    val animatedProgress by animateFloatAsState(
        targetValue = 1f,
        animationSpec = tween(600, easing = FastOutSlowInEasing),
        label = "chart_transition"
    )

    // Margens internas do Box.
    val horizontalPadding = 16.dp
    val verticalPadding = 24.dp

    // Para conversão de dp para pixels.
    val density = LocalDensity.current
    val tooltipWidthPx = with(density) { 120.dp.roundToPx() }
    val tooltipHeightPx = with(density) { 80.dp.roundToPx() }

    // Captura o tamanho do Box para calcular o posicionamento do tooltip e do gráfico.
    var boxSize by remember { mutableStateOf(IntSize.Zero) }

    // Animação para o tooltip (efeito de escala).
    val tooltipScale by animateFloatAsState(
        targetValue = if (selectedBarIndex != null) 1f else 0f,
        animationSpec = tween(durationMillis = 200)
    )

    // Animação para a largura da borda da barra selecionada (calculada fora do Canvas).
    val borderStrokeWidth by animateFloatAsState(
        targetValue = if (selectedBarIndex != null) 3f else 0f,
        animationSpec = tween(300)
    )

    // --- Cálculos dependentes do tamanho do Box ---
    val labelMarginPx = with(density) { 24.dp.toPx() }
    val canvasWidth = boxSize.width.toFloat()
    val canvasHeight = boxSize.height.toFloat()
    val chartHeight = if (canvasHeight > 0f) canvasHeight - labelMarginPx else 0f
    val baseline = chartHeight / 2f
    val maxValue = if (data.isEmpty()) 1f else data.maxOf { maxOf(it.first, it.second).toFloat() }

    // Pré-cálculo dos valores animados para cada barra.
    val animatedGainHeights = data.map { (gain, _) ->
        animateFloatAsState(
            targetValue = if (maxValue > 0f) ((gain.toFloat() / maxValue) * baseline) * animatedProgress else 0f,
            animationSpec = tween(600, easing = FastOutSlowInEasing)
        ).value
    }
    val animatedLossHeights = data.map { (_, loss) ->
        animateFloatAsState(
            targetValue = if (maxValue > 0f) ((loss.toFloat() / maxValue) * baseline) * animatedProgress else 0f,
            animationSpec = tween(600, easing = FastOutSlowInEasing)
        ).value
    }

    // Memoriza os brushes para evitar alocações repetidas.
    val gainBrush = remember {
        Brush.verticalGradient(colors = listOf(GreenLight, GreenLight.copy(alpha = 0.7f)))
    }
    val lossBrush = remember {
        Brush.verticalGradient(colors = listOf(RedLight, RedLight.copy(alpha = 0.7f)))
    }

    // Memoriza os layouts dos labels para que não sejam recalculados a cada frame.
    val labelTextLayouts = remember(labels, textMeasurer) {
        labels.map { label ->
            textMeasurer.measure(
                text = AnnotatedString(label),
                style = TextStyle(
                    color = Gray,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold
                ),
                maxLines = 1
            )
        }
    }



    Box(
        modifier = modifier
            .padding(horizontal = horizontalPadding, vertical = verticalPadding)
            .onGloballyPositioned { coordinates ->
                boxSize = coordinates.size
            }
    ) {
        if (data.isEmpty()) {
            Text(
                text = "Nenhum dado disponível",
                modifier = Modifier.align(Alignment.Center),
                color = Color.Gray
            )
        } else {
            Canvas(
                modifier = Modifier
                    .fillMaxSize()
                    .pointerInput(Unit) {
                        detectTapGestures { tapOffset ->
                            val barSpacing = canvasWidth / data.size
                            val barWidth = barSpacing * 0.7f

                            // Identifica qual barra foi tocada.
                            selectedBarIndex = data.indices.firstOrNull { index ->
                                val left = barSpacing * index + (barSpacing - barWidth) / 2f
                                tapOffset.x in left..(left + barWidth)
                            }
                            selectedBarIndex?.let { index ->
                                val barCenterX = barSpacing * index + barWidth / 2f
                                // Se o toque ocorrer na parte superior do canvas, posiciona o tooltip abaixo; caso contrário, acima.
                                val tooltipY = if (tapOffset.y < canvasHeight / 2f) {
                                    tapOffset.y + 40.dp.toPx()
                                } else {
                                    tapOffset.y - 40.dp.toPx()
                                }
                                tooltipPosition.value = Offset(barCenterX, tooltipY)
                            }
                        }
                    }
            ) {
                val barSpacing = canvasWidth / data.size
                val barWidth = barSpacing * 0.7f

                // Desenha as linhas de grade.
                val totalGridLines = 5 * 2 + 1
                val lineSpacing = if (totalGridLines > 1) chartHeight / (totalGridLines - 1) else 0f
                for (i in 0 until totalGridLines) {
                    val y = i * lineSpacing
                    drawLine(
                        color = Color.LightGray.copy(alpha = 0.3f),
                        start = Offset(0f, y),
                        end = Offset(canvasWidth, y),
                        strokeWidth = 1f
                    )
                }

                // Linha de base com efeito tracejado.
                drawLine(
                    color = Color.Gray.copy(alpha = 0.8f),
                    start = Offset(0f, baseline),
                    end = Offset(canvasWidth * animatedProgress, baseline),
                    strokeWidth = 2f,
                    pathEffect = PathEffect.dashPathEffect(floatArrayOf(10f, 5f))
                )

                data.forEachIndexed { index, (gain, loss) ->
                    val left = barSpacing * index + (barSpacing - barWidth) / 2f

                    // Usa os valores animados pré-calculados.
                    val animatedGainHeight = animatedGainHeights.getOrNull(index) ?: 0f
                    val animatedLossHeight = animatedLossHeights.getOrNull(index) ?: 0f

                    // Desenha a barra de ganhos (acima da linha de base).
                    drawRoundRect(
                        brush = gainBrush,
                        topLeft = Offset(left, baseline - animatedGainHeight),
                        size = Size(barWidth, animatedGainHeight),
                        cornerRadius = CornerRadius(4f, 4f)
                    )
                    // Desenha a barra de perdas (abaixo da linha de base).
                    drawRoundRect(
                        brush = lossBrush,
                        topLeft = Offset(left, baseline),
                        size = Size(barWidth, animatedLossHeight),
                        cornerRadius = CornerRadius(4f, 4f)
                    )

                    // Se a barra estiver selecionada, desenha um destaque com a borda animada.
                    if (selectedBarIndex == index) {
                        drawRoundRect(
                            color = Color.Yellow,
                            topLeft = Offset(left, baseline - animatedGainHeight),
                            size = Size(barWidth, animatedGainHeight + animatedLossHeight),
                            cornerRadius = CornerRadius(4f, 4f),
                            style = Stroke(width = borderStrokeWidth)
                        )
                    }

                    // Desenha o label (usando o layout memorizado).
                    labelTextLayouts.getOrNull(index)?.let { textLayoutResult ->
                        drawText(
                            textLayoutResult = textLayoutResult,
                            topLeft = Offset(
                                left + barWidth / 2 - textLayoutResult.size.width / 2,
                                chartHeight + 4.dp.toPx()
                            )
                        )
                    }
                }
            }

            // Legenda no canto superior direito.
            FlowRow(
                modifier = Modifier.padding(5.dp),
                itemVerticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                LegendItem(color = GreenLight, label = "Ganhos" )
                LegendItem(color = RedLight, label = "Gastos")
            }


            // Exibe o tooltip com animações (fade, expand e escala).
            AnimatedVisibility(
                visible = selectedBarIndex != null,
                enter = fadeIn(animationSpec = tween(300)) + expandVertically(animationSpec = tween(300)),
                exit = fadeOut(animationSpec = tween(300)) + shrinkVertically(animationSpec = tween(300)),
                modifier = Modifier.offset {
                    IntOffset(
                        x = (tooltipPosition.value.x - tooltipWidthPx / 2f)
                            .coerceIn(0f, (boxSize.width - tooltipWidthPx).toFloat())
                            .toInt(),
                        y = (tooltipPosition.value.y - tooltipHeightPx)
                            .coerceIn(0f, (boxSize.height - tooltipHeightPx).toFloat())
                            .toInt()
                    )
                }
            ) {
                Card(
                    modifier = Modifier.scale(tooltipScale),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    elevation = CardDefaults.cardElevation(4.dp),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(12.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = labels.getOrElse(selectedBarIndex ?: 0) { "#${(selectedBarIndex ?: 0) + 1}" },
                            color = Color.Black,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        selectedBarIndex?.let { index ->
                            val (gain, loss) = data[index]
                            Text("▲ ${formatCurrency(gain)}", color = GreenLight)
                            Text("▼ ${formatCurrency(loss)}", color = RedLight)
                        }
                    }
                }
            }
        }
    }
}


@Composable
private fun LegendItem(color: Color, label: String) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Box(
            modifier = Modifier
                .size(12.dp)
                .background(color, CircleShape)
        )
        Spacer(modifier = Modifier.width(4.dp))
        Text(text = label, fontSize = 12.sp)
    }
}

//Botões de Filtro
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
            androidx.compose.material3.FilterChip(
                selected = selectedOption == option,
                onClick = {
                    selectedOption = option
                    onFilterChange(option)
                },
                label = { Text(option,color = MaterialTheme.colorScheme.onSecondary) },

                //design
                colors = FilterChipDefaults.filterChipColors(selectedContainerColor = MaterialTheme.colorScheme.primary),
                //

                leadingIcon = if (selectedOption == option) {
                    {
                        Icon(
                            imageVector = Icons.Filled.Done,
                            contentDescription = "Done icon",
                            modifier = Modifier.size(FilterChipDefaults.IconSize),
                            tint = Color.White
                        )
                    }
                } else {
                    null
                },

                )
        }
    }
}

//Os selectores de data
@Composable
fun DateSelectors(
    currentDate: Long,
    selectedFilter: String,
    onDateChange: (Long) -> Unit
) {
    // 1) Prepara o Calendar
    val calendar = remember(currentDate) {
        Calendar.getInstance().apply {
            timeInMillis = currentDate
            firstDayOfWeek = Calendar.MONDAY
        }
    }

    // 2) Labels estáticos para cada filtro
    val labels = when (selectedFilter) {
        "Semana" -> listOf("SEG", "TER", "QUA", "QUI", "SEX", "SAB", "DOM")
        "Mês"    -> listOf("SEM 1", "SEM 2", "SEM 3", "SEM 4", "SEM 5")
        "Ano"    -> listOf("JAN", "FEV", "MAR", "ABR", "MAI", "JUN", "JUL", "AGO", "SET", "OUT", "NOV", "DEZ")
        else     -> emptyList()
    }

    // 3) Texto de contexto (com ano incluso)
    val contextText = when (selectedFilter) {
        "Semana" -> {
            // Início e fim da semana corrente
            val start = (calendar.clone() as Calendar).apply {
                set(Calendar.DAY_OF_WEEK, Calendar.MONDAY)
            }
            val end = (calendar.clone() as Calendar).apply {
                set(Calendar.DAY_OF_WEEK, Calendar.SUNDAY)
            }
            "${start.get(Calendar.DAY_OF_MONTH)}/" +
                    "${start.get(Calendar.MONTH) + 1}/" +
                    "${start.get(Calendar.YEAR)} - " +
                    "${end.get(Calendar.DAY_OF_MONTH)}/" +
                    "${end.get(Calendar.MONTH) + 1}/" +
                    "${end.get(Calendar.YEAR)}"
        }
        "Mês" -> {
            // Nome do mês e ano
            val monthName = calendar.getDisplayName(Calendar.MONTH, Calendar.LONG, Locale.getDefault())
                .replaceFirstChar { it.uppercase() }
            val year = calendar.get(Calendar.YEAR)
            "$monthName $year"
        }
        "Ano" -> {
            // Apenas o ano
            calendar.get(Calendar.YEAR).toString()
        }
        else -> ""
    }

    // 4) Linha de navegação com ícones
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center,
        modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp)
    ) {
        IconButton(onClick = {
            when (selectedFilter) {
                "Semana" -> calendar.add(Calendar.WEEK_OF_YEAR, -1)
                "Mês"    -> calendar.add(Calendar.MONTH, -1)
                "Ano"    -> calendar.add(Calendar.YEAR, -1)
            }
            onDateChange(calendar.timeInMillis)
        }) {
            Icon(imageVector = Icons.Filled.ArrowBackIosNew, contentDescription = "Retroceder", tint = MaterialTheme.colorScheme.onSecondary)
        }

        Text(
            text = contextText,
            fontSize = 16.sp,
            fontWeight = FontWeight.Medium,
            modifier = Modifier.padding(horizontal = 16.dp),
            color = MaterialTheme.colorScheme.onSecondary
        )

        IconButton(onClick = {
            when (selectedFilter) {
                "Semana" -> calendar.add(Calendar.WEEK_OF_YEAR, 1)
                "Mês"    -> calendar.add(Calendar.MONTH, 1)
                "Ano"    -> calendar.add(Calendar.YEAR, 1)
            }
            onDateChange(calendar.timeInMillis)
        },) {
            Icon(imageVector = Icons.Filled.ArrowForwardIos, contentDescription = "Avançar", tint = MaterialTheme.colorScheme.onSecondary)
        }
    }

    // 5) Rótulos estáticos abaixo do contexto
    Text(
        text = labels.joinToString("  •  "),
        fontSize = 14.sp,
        color = Color.Gray,
        fontWeight = FontWeight.SemiBold,
        modifier = Modifier.fillMaxWidth().padding(bottom = 8.dp),
        textAlign = TextAlign.Center
    )
}
