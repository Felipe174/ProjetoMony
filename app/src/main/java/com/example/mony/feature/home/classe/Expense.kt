package com.example.mony.feature.home.classe

import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await

data class Expense(
    val id: String, // ID do documento no Firestore
    val date: Long, // Data do gasto ou ganho
    val amount: Double, // Valor do gasto ou ganho
    val type: ExpenseType = ExpenseType(0,"", false) // Tipo de gasto ou ganho
)

data class ExpenseType(
    val imageResId: Int, // ID do recurso da imagem
    val name: String, // Nome do tipo de gasto ou ganho
    val isGain: Boolean  // Indica se é um ganho (true) ou um gasto (false)
)



