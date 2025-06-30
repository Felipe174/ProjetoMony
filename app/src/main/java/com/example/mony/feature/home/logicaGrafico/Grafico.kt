package com.example.mony.feature.home.logicaGrafico

import android.util.Log
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
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForwardIos
import androidx.compose.material.icons.filled.ArrowBackIosNew
import androidx.compose.material.icons.filled.Done
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.runtime.toMutableStateList
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
import androidx.compose.ui.graphics.nativeCanvas
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


@Composable
fun HomeWithGraph(
    selectedFilter: String,
    currentDate: Long,
    expenses: List<Expense>,
    onDateChange: (Long) -> Unit,
    onFilterChange: (String) -> Unit
) {
    val effectiveFilter = selectedFilter.ifEmpty { "Semana" }

    val chartData = remember(currentDate, effectiveFilter, expenses) {
        getChartData(
            expenses = expenses,
            currentDate = currentDate,
            filter = effectiveFilter
        )
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 12.dp, vertical = 8.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        DateSelectors(
            currentDate = currentDate,
            selectedFilter = effectiveFilter,
            onDateChange = onDateChange
        )

        Spacer(modifier = Modifier.height(8.dp))

        ElevatedCard(
            modifier = Modifier
                .fillMaxWidth()
                .heightIn(min = 280.dp, max = 350.dp)
                .border(
                    width = 2.dp,
                    color = MaterialTheme.colorScheme.secondary,
                    shape = RoundedCornerShape(12.dp)
                ),
            colors = CardDefaults.cardColors(MaterialTheme.colorScheme.background),
        ) {
            BarChart(
                data = chartData,
                selectedFilter = effectiveFilter,
                modifier = Modifier
                    .fillMaxSize()
                    .padding(8.dp)
            )
        }

        Spacer(modifier = Modifier.height(10.dp))

        FilterChip(
            selectedFilter = effectiveFilter,
            onFilterChange = onFilterChange
        )
    }
}

// Corrigido: evita animações desnecessárias e melhora a performance geral
@Composable
fun BarChart(
    data: List<Pair<Double, Double>>,
    selectedFilter: String,
    modifier: Modifier = Modifier
) {
    val labels = when (selectedFilter) {
        "Semana" -> listOf("Seg", "Ter", "Qua", "Qui", "Sex", "Sáb", "Dom")
        "Mês" -> (1..5).map { "Semana $it" }
        "Ano" -> listOf("Jan", "Fev", "Mar", "Abr", "Mai", "Jun", "Jul", "Ago", "Set", "Out", "Nov", "Dez")
        else -> emptyList()
    }

    var selectedBarIndex by rememberSaveable { mutableStateOf<Int?>(null) }
    val tooltipPosition = remember { mutableStateOf(Offset.Zero) }
    val textMeasurer = rememberTextMeasurer()
    val animatedProgress by remember { mutableFloatStateOf(1f) } // sem animação massiva

    val horizontalPadding = 16.dp
    val verticalPadding = 24.dp
    val density = LocalDensity.current
    val tooltipWidthPx = with(density) { 120.dp.roundToPx() }
    val tooltipHeightPx = with(density) { 80.dp.roundToPx() }
    var boxSize by remember { mutableStateOf(IntSize.Zero) }

    val tooltipScale by animateFloatAsState(
        targetValue = if (selectedBarIndex != null) 1f else 0f,
        animationSpec = tween(200), label = "tooltip"
    )

    val borderStrokeWidth by animateFloatAsState(
        targetValue = if (selectedBarIndex != null) 3f else 0f,
        animationSpec = tween(300), label = "border"
    )

    val labelMarginPx = with(density) { 24.dp.toPx() }
    val chartHeight = (boxSize.height.toFloat() - labelMarginPx).coerceAtLeast(1f)
    val baseline = chartHeight / 2f
    val maxValue = data.maxOfOrNull { maxOf(it.first, it.second).toFloat() }.takeIf { it!! > 0f } ?: 1f

    val gainBrush = remember { Brush.verticalGradient(listOf(GreenLight, GreenLight.copy(alpha = 0.7f))) }
    val lossBrush = remember { Brush.verticalGradient(listOf(RedLight.copy(alpha = 0.7f), RedLight)) }

    val labelTextLayouts = remember(labels, textMeasurer) {
        labels.map {
            textMeasurer.measure(
                AnnotatedString(it),
                style = TextStyle(color = Gray, fontSize = 12.sp, fontWeight = FontWeight.Bold),
                maxLines = 1
            )
        }
    }

    Box(
        modifier = modifier
            .padding(horizontal = horizontalPadding, vertical = verticalPadding)
            .onGloballyPositioned { boxSize = it.size }
    ) {
        if (data.isEmpty()) {
            Text("Nenhum dado disponível", Modifier.align(Alignment.Center), color = Color.Gray)
            return
        }

        Canvas(
            modifier = Modifier
                .fillMaxSize()
                .pointerInput(data.size) {
                    detectTapGestures { tapOffset ->
                        val canvasWidth = size.width
                        val barSpacing = canvasWidth / data.size
                        val barWidth = barSpacing * 0.7f
                        val tappedIndex = data.indices.firstOrNull { index ->
                            val left = barSpacing * index + (barSpacing - barWidth) / 2f
                            tapOffset.x in left..(left + barWidth)
                        }
                        if (tappedIndex != null) {
                            selectedBarIndex = tappedIndex.coerceIn(data.indices)
                            val centerX = barSpacing * tappedIndex + barWidth / 2f
                            val tooltipY = if (tapOffset.y < size.height / 2f)
                                tapOffset.y + 40.dp.toPx() else tapOffset.y - 40.dp.toPx()
                            tooltipPosition.value = Offset(centerX, tooltipY)
                        }
                    }
                }
        ) {
            val canvasWidth = size.width
            val barSpacing = canvasWidth / data.size
            val barWidth = barSpacing * 0.7f

            val totalGridLines = 11
            val lineSpacing = chartHeight / (totalGridLines - 1)

            repeat(totalGridLines) { i ->
                drawLine(
                    color = Color.LightGray.copy(alpha = 0.3f),
                    start = Offset(0f, i * lineSpacing),
                    end = Offset(canvasWidth, i * lineSpacing),
                    strokeWidth = 1f
                )
            }

            drawLine(
                color = Color.Gray.copy(alpha = 0.8f),
                start = Offset(0f, baseline),
                end = Offset(canvasWidth, baseline),
                strokeWidth = 2f,
                pathEffect = PathEffect.dashPathEffect(floatArrayOf(10f, 5f))
            )

            data.forEachIndexed { index, (gain, loss) ->
                val gainHeight = ((gain.toFloat() / maxValue) * baseline).coerceAtLeast(1f)
                val lossHeight = ((loss.toFloat() / maxValue) * baseline).coerceAtLeast(1f)
                val left = barSpacing * index + (barSpacing - barWidth) / 2f

                if (gain > 0.0) {
                    drawRoundRect(
                        brush = gainBrush,
                        topLeft = Offset(left, baseline - gainHeight),
                        size = Size(barWidth, gainHeight),
                        cornerRadius = CornerRadius(4f, 4f)
                    )
                }

                if (loss > 0.0) {
                    drawRoundRect(
                        brush = lossBrush,
                        topLeft = Offset(left, baseline),
                        size = Size(barWidth, lossHeight),
                        cornerRadius = CornerRadius(4f, 4f)
                    )
                }

                if (selectedBarIndex == index) {
                    drawRoundRect(
                        color = Color.Yellow,
                        topLeft = Offset(left, baseline - gainHeight),
                        size = Size(barWidth, gainHeight + lossHeight),
                        cornerRadius = CornerRadius(4f, 4f),
                        style = Stroke(width = borderStrokeWidth)
                    )
                }

                labelTextLayouts.getOrNull(index)?.let {
                    drawText(
                        it,
                        topLeft = Offset(
                            left + barWidth / 2 - it.size.width / 2,
                            chartHeight + 4.dp.toPx()
                        )
                    )
                }
            }
        }

        FlowRow(
            Modifier.padding(5.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            LegendItem(color = GreenLight, label = "Ganhos")
            LegendItem(color = RedLight, label = "Gastos")
        }

        AnimatedVisibility(
            visible = selectedBarIndex != null,
            enter = fadeIn(tween(300)) + expandVertically(tween(300)),
            exit = fadeOut(tween(300)) + shrinkVertically(tween(300)),
            modifier = Modifier.offset {
                IntOffset(
                    (tooltipPosition.value.x - tooltipWidthPx / 2f)
                        .coerceIn(0f, (boxSize.width - tooltipWidthPx).toFloat()).toInt(),
                    (tooltipPosition.value.y - tooltipHeightPx)
                        .coerceIn(0f, (boxSize.height - tooltipHeightPx).toFloat()).toInt()
                )
            }
        ) {
            Card(
                modifier = Modifier.scale(tooltipScale),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(4.dp),
                shape = RoundedCornerShape(8.dp)
            ) {
                Column(Modifier.padding(12.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        labels.getOrElse(selectedBarIndex ?: 0) { "#${(selectedBarIndex ?: 0) + 1}" },
                        color = Color.Black,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(Modifier.height(4.dp))
                    selectedBarIndex?.let { i ->
                        val (gain, loss) = data.getOrNull(i) ?: (0.0 to 0.0)
                        Text("▲ ${formatCurrency(gain)}", color = GreenLight)
                        Text("▼ ${formatCurrency(loss)}", color = RedLight)
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

@Composable
fun FilterChip(
    selectedFilter: String,
    onFilterChange: (String) -> Unit
) {
    val options = listOf("Semana", "Mês", "Ano")

    LazyRow(
        modifier = Modifier
            .fillMaxWidth()
            .padding(start = 8.dp),
    ) {
        items(options) { option ->
            FilterChip(
                selected = selectedFilter == option,
                onClick = { onFilterChange(option) },
                label = {
                    Text(
                        text = option,
                        color = if (selectedFilter == option)
                            MaterialTheme.colorScheme.onPrimary
                        else MaterialTheme.colorScheme.onSurface
                    )
                },
                colors = FilterChipDefaults.filterChipColors(
                    selectedContainerColor = MaterialTheme.colorScheme.primary,
                    containerColor = MaterialTheme.colorScheme.surface
                ),
                leadingIcon = if (selectedFilter == option) {
                    {
                        Icon(
                            imageVector = Icons.Filled.Done,
                            contentDescription = null,
                            modifier = Modifier.size(FilterChipDefaults.IconSize),
                            tint = MaterialTheme.colorScheme.onPrimary
                        )
                    }
                } else null
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
    val baseCalendar = remember(currentDate) {
        Calendar.getInstance().apply {
            timeInMillis = currentDate
            firstDayOfWeek = Calendar.MONDAY
        }
    }

    val labels = when (selectedFilter) {
        "Semana" -> listOf("SEG", "TER", "QUA", "QUI", "SEX", "SAB", "DOM")
        "Mês"    -> listOf("SEM 1", "SEM 2", "SEM 3", "SEM 4", "SEM 5")
        "Ano"    -> listOf("JAN", "FEV", "MAR", "ABR", "MAI", "JUN", "JUL", "AGO", "SET", "OUT", "NOV", "DEZ")
        else     -> emptyList()
    }

    val contextText = when (selectedFilter) {
        "Semana" -> {
            val start = (baseCalendar.clone() as Calendar).apply {
                set(Calendar.DAY_OF_WEEK, Calendar.MONDAY)
            }
            val end = (baseCalendar.clone() as Calendar).apply {
                set(Calendar.DAY_OF_WEEK, Calendar.SUNDAY)
            }
            "${String.format("%02d", start.get(Calendar.DAY_OF_MONTH))}/" +
                    "${String.format("%02d", start.get(Calendar.MONTH) + 1)}/" +
                    "${start.get(Calendar.YEAR)} - " +
                    "${String.format("%02d", end.get(Calendar.DAY_OF_MONTH))}/" +
                    "${String.format("%02d", end.get(Calendar.MONTH) + 1)}/" +
                    "${end.get(Calendar.YEAR)}"
        }

        "Mês" -> {
            val monthName = baseCalendar.getDisplayName(Calendar.MONTH, Calendar.LONG, Locale.getDefault())
                ?.replaceFirstChar { it.titlecase() } ?: "Mês"
            "$monthName ${baseCalendar.get(Calendar.YEAR)}"
        }

        "Ano" -> baseCalendar.get(Calendar.YEAR).toString()
        else -> ""
    }

    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center,
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
    ) {
        IconButton(onClick = {
            val cal = baseCalendar.clone() as Calendar
            when (selectedFilter) {
                "Semana" -> cal.add(Calendar.WEEK_OF_YEAR, -1)
                "Mês"    -> cal.add(Calendar.MONTH, -1)
                "Ano"    -> cal.add(Calendar.YEAR, -1)
            }
            onDateChange(cal.timeInMillis)
        }) {
            Icon(
                imageVector = Icons.Filled.ArrowBackIosNew,
                contentDescription = "Retroceder",
                tint = MaterialTheme.colorScheme.onSecondary
            )
        }

        Text(
            text = contextText,
            fontSize = 16.sp,
            fontWeight = FontWeight.Medium,
            modifier = Modifier.padding(horizontal = 16.dp),
            color = MaterialTheme.colorScheme.onSecondary
        )

        IconButton(onClick = {
            val cal = baseCalendar.clone() as Calendar
            when (selectedFilter) {
                "Semana" -> cal.add(Calendar.WEEK_OF_YEAR, 1)
                "Mês"    -> cal.add(Calendar.MONTH, 1)
                "Ano"    -> cal.add(Calendar.YEAR, 1)
            }
            onDateChange(cal.timeInMillis)
        }) {
            Icon(
                imageVector = Icons.AutoMirrored.Filled.ArrowForwardIos,
                contentDescription = "Avançar",
                tint = MaterialTheme.colorScheme.onSecondary
            )
        }
    }

    Text(
        text = labels.joinToString("  •  "),
        fontSize = 14.sp,
        color = Color.Gray,
        fontWeight = FontWeight.SemiBold,
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 8.dp),
        textAlign = TextAlign.Center
    )
}
