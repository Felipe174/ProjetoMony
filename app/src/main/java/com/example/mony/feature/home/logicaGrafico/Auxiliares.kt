package com.example.mony.feature.home.logicaGrafico

import android.os.Build
import androidx.annotation.RequiresApi
import com.example.mony.feature.home.classe.Expense
import java.time.YearMonth
import java.time.ZoneId
import java.util.Calendar


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

private fun handleWeek(calendar: Calendar, expenses: List<Expense>): List<Pair<Double, Double>> {
    val startOfWeek = calendar.cloneAsSafe().apply {
        set(Calendar.DAY_OF_WEEK, Calendar.MONDAY)
        setToDayStart()
    }

    return (0..6).map { offset ->
        val dayStart = startOfWeek.cloneAsSafe().apply { add(Calendar.DAY_OF_YEAR, offset) }
        val dayEnd = dayStart.cloneAsSafe().apply { setToDayEnd() }
        calculateDailyValues(expenses, dayStart.timeInMillis, dayEnd.timeInMillis)
    }
}

private fun handleMonth(calendar: Calendar, expenses: List<Expense>): List<Pair<Double, Double>> {
    val firstDay = calendar.cloneAsSafe().apply {
        set(Calendar.DAY_OF_MONTH, 1)
        setToDayStart()
    }
    val lastDay = calendar.cloneAsSafe().apply {
        set(Calendar.DAY_OF_MONTH, getActualMaximum(Calendar.DAY_OF_MONTH))
        setToDayEnd()
    }

    val labels = listOf("SEMANA 1", "SEMANA 2", "SEMANA 3", "SEMANA 4", "SEMANA 5")
    val sums = labels.associateWith { 0.0 }.toMutableMap()

    var current = firstDay.cloneAsSafe()

    while (current.timeInMillis <= lastDay.timeInMillis) {
        val idx = ((current.get(Calendar.DAY_OF_MONTH) - 1) / 7).coerceAtMost(4)
        val label = labels[idx]

        val start = current.cloneAsSafe().apply { setToDayStart() }.timeInMillis
        val end = current.cloneAsSafe().apply { setToDayEnd() }.timeInMillis

        val (inc, exp) = calculateDailyValues(expenses, start, end)
        sums[label] = (sums[label] ?: 0.0) + inc - exp

        current.add(Calendar.DAY_OF_YEAR, 1)
    }

    return labels.map { label -> Pair(sums[label] ?: 0.0, 0.0) }
}

@RequiresApi(Build.VERSION_CODES.O)
private fun handleYear(calendar: Calendar, expenses: List<Expense>): List<Pair<Double, Double>> {
    val year = calendar.get(Calendar.YEAR)
    val months = listOf("JAN", "FEV", "MAR", "ABR", "MAI", "JUN", "JUL", "AGO", "SET", "OUT", "NOV", "DEZ")
    val sums = months.associateWith { 0.0 }.toMutableMap()

    months.forEachIndexed { index, label ->
        val ym = YearMonth.of(year, index + 1)
        val zone = ZoneId.systemDefault()

        val start = ym.atDay(1).atStartOfDay(zone)
        val end = ym.atEndOfMonth().atTime(23, 59, 59, 999_000_000).atZone(zone)

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

    return months.map { label -> Pair(sums[label] ?: 0.0, 0.0) }
}

// --------------------- Funções auxiliares ---------------------

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

private fun Calendar.cloneAsSafe(): Calendar = this.clone() as Calendar

private fun Calendar.setToDayStart() {
    set(Calendar.HOUR_OF_DAY, 0)
    set(Calendar.MINUTE, 0)
    set(Calendar.SECOND, 0)
    set(Calendar.MILLISECOND, 0)
}

private fun Calendar.setToDayEnd() {
    set(Calendar.HOUR_OF_DAY, 23)
    set(Calendar.MINUTE, 59)
    set(Calendar.SECOND, 59)
    set(Calendar.MILLISECOND, 999)
}
