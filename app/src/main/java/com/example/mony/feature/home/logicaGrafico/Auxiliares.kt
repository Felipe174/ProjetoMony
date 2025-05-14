package com.example.mony.feature.home.logicaGrafico

import android.os.Build
import androidx.annotation.RequiresApi
import com.example.mony.feature.home.classe.Expense
import java.time.YearMonth
import java.time.ZoneId
import java.util.Calendar


// Função para obter os dados para o gráfico
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
        "Mês"    -> handleMonth(calendar, expenses)
        "Ano"    -> handleYear(calendar, expenses)
        else     -> emptyList()
    }
}

// Sempre 7 dias fixos: SEG a DOM, mesmo sem dados
private fun handleWeek(calendar: Calendar, expenses: List<Expense>): List<Pair<Double, Double>> {
    // Ajusta para a segunda-feira da semana
    val startOfWeek = calendar.cloneAsSafe()
        .apply {
            set(Calendar.DAY_OF_WEEK, Calendar.MONDAY)
            setToDayStart()
        }

    // Para cada dia de 0 a 6, soma ganhos e gastos
    return (0..6).map { offset ->
        val dayStart = startOfWeek.cloneAsSafe().apply { add(Calendar.DAY_OF_YEAR, offset) }
        val dayEnd = dayStart.cloneAsSafe().apply {
            set(Calendar.HOUR_OF_DAY, 23)
            set(Calendar.MINUTE, 59)
            set(Calendar.SECOND, 59)
            set(Calendar.MILLISECOND, 999)
        }
        calculateDailyValues(expenses, dayStart.timeInMillis, dayEnd.timeInMillis)
    }
}

// Sempre 5 semanas fixas, zeradas se não tiver dados
private fun handleMonth(calendar: Calendar, expenses: List<Expense>): List<Pair<Double, Double>> {
    val firstDay = calendar.cloneAsSafe().apply {
        set(Calendar.DAY_OF_MONTH, 1)
        setToDayStart()
    }
    val lastDay = calendar.cloneAsSafe().apply {
        set(Calendar.DAY_OF_MONTH, getActualMaximum(Calendar.DAY_OF_MONTH))
        set(Calendar.HOUR_OF_DAY, 23)
        set(Calendar.MINUTE, 59)
        set(Calendar.SECOND, 59)
        set(Calendar.MILLISECOND, 999)
    }

    // Rótulos fixos
    val labels = listOf("SEMANA 1","SEMANA 2","SEMANA 3","SEMANA 4","SEMANA 5")
    // Inicializa todas com zero
    val sums = labels.associateWith { 0.0 }.toMutableMap()

    var current = firstDay
    while (!current.timeInMillis.let { it > lastDay.timeInMillis }) {
        // Qual semana (0..4)
        val idx = ((current.get(Calendar.DAY_OF_MONTH) - 1) / 7).coerceAtMost(4)
        val weekLabel = labels[idx]
        // Soma ganhos e gastos desse dia
        val (inc, exp) = calculateDailyValues(
            expenses,
            current.timeInMillis,
            current.cloneAsSafe().apply {
                set(Calendar.HOUR_OF_DAY,23)
                set(Calendar.MINUTE,59)
                set(Calendar.SECOND,59)
                set(Calendar.MILLISECOND,999)
            }.timeInMillis
        )
        // Acumula
        sums[weekLabel] = (sums[weekLabel] ?: 0.0) + inc - exp /* ou ajustar conforme separa gain/loss */
        current.add(Calendar.DAY_OF_YEAR, 1)
    }

    // Constrói a lista final
    return labels.map { label ->
        val total = sums[label] ?: 0.0
        Pair(total, 0.0) // adapte se precisar retornar (gain, loss)
    }
}


// Sempre 12 meses fixos: JAN a DEZ, zerados sem dados
@RequiresApi(Build.VERSION_CODES.O)
private fun handleYear(calendar: Calendar, expenses: List<Expense>): List<Pair<Double, Double>> {
    val yearValue = calendar.get(Calendar.YEAR)
    // Rótulos fixos
    val months = listOf("JAN","FEV","MAR","ABR","MAI","JUN","JUL","AGO","SET","OUT","NOV","DEZ")
    // Inicializa soma por mês
    val sums = months.associateWith { 0.0 }.toMutableMap()

    months.forEachIndexed { idx, label ->
        val ym = YearMonth.of(yearValue, idx + 1)
        val start = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            ym.atDay(1).atStartOfDay(ZoneId.systemDefault())
        } else {
            TODO("VERSION.SDK_INT < O")
        }
        val end = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            ym.atEndOfMonth().atTime(23,59,59,999_000_000).atZone(ZoneId.systemDefault())
        } else {
            TODO("VERSION.SDK_INT < O")
        }

        var current = start
        while (current.isBefore(end)) {
            val (inc, exp) = calculateDailyValues(
                expenses,
                current.toInstant().toEpochMilli(),
                current.plusDays(1).minusNanos(1).toInstant().toEpochMilli()
            )
            sums[label] = (sums[label] ?: 0.0) + inc - exp
            current = current.plusDays(1)
        }
    }

    return months.map { label ->
        Pair(sums[label] ?: 0.0, 0.0) // adaptar gain/loss conforme necessidade
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

//calculando o valor semanal
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
//calculando o valor mensal
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

//calculando o valor anual
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