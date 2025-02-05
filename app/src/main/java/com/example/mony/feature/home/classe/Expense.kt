package com.example.mony.feature.home.classe



// Modelo simplificado
data class Expense(
    val id: String = "",
    val amount: Double = 0.0,
    val date: Long = System.currentTimeMillis(),
    val type: TransactionType, // Usa apenas um tipo
    val description: String = ""
)

