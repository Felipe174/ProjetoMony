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
        "Mês" -> handleMonth(calendar, expenses)
        "Ano" -> handleYear(calendar, expenses)
        else -> emptyList()
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

    // 5 semanas possíveis
    val weeks = MutableList(5) { Pair(0.0, 0.0) }
    var current = firstDay.cloneAsSafe()

    while (current.timeInMillis <= lastDay.timeInMillis) {
        val idx = ((current.get(Calendar.DAY_OF_MONTH) - 1) / 7).coerceAtMost(4)
        val start = current.cloneAsSafe().apply { setToDayStart() }.timeInMillis
        val end = current.cloneAsSafe().apply { setToDayEnd() }.timeInMillis

        val (inc, exp) = calculateDailyValues(expenses, start, end)
        val (oldInc, oldExp) = weeks[idx]
        weeks[idx] = Pair(oldInc + inc, oldExp + exp)

        current.add(Calendar.DAY_OF_YEAR, 1)
    }

    return weeks
}

private fun handleYear(calendar: Calendar, expenses: List<Expense>): List<Pair<Double, Double>> {
    val year = calendar.get(Calendar.YEAR)

    return (0..11).map { monthIndex ->
        val start = calendar.cloneAsSafe().apply {
            set(Calendar.YEAR, year)
            set(Calendar.MONTH, monthIndex)
            set(Calendar.DAY_OF_MONTH, 1)
            setToDayStart()
        }.timeInMillis

        val end = calendar.cloneAsSafe().apply {
            set(Calendar.YEAR, year)
            set(Calendar.MONTH, monthIndex)
            set(Calendar.DAY_OF_MONTH, getActualMaximum(Calendar.DAY_OF_MONTH))
            setToDayEnd()
        }.timeInMillis

        calculateDailyValues(expenses, start, end)
    }
}



private fun calculateDailyValues(
    expenses: List<Expense>,
    start: Long,
    end: Long
): Pair<Double, Double> {
    val filtered = expenses.filter { it.date in start..end }
    val gain = filtered.filter { it.type.isIncome }.sumOf { it.amount ?: 0.0 }
    val loss = filtered.filter { !it.type.isIncome }.sumOf { it.amount ?: 0.0 }

    return Pair(gain, loss)
}

private fun Calendar.cloneAsSafe(): Calendar = (this.clone() as Calendar)

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
