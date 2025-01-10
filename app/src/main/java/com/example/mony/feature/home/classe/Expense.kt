package com.example.mony.feature.home.classe

import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await

data class Expense(
    val id: String, // ID do documento no Firestore
    val date: Long, // Data do gasto ou ganho
    val amount: Double, // Valor do gasto ou ganho
    val type: ExpenseType // Tipo de gasto ou ganho
)

private val firestore = FirebaseFirestore.getInstance()

suspend fun addExpenseToFirestore(expense: Expense) {
    val expenseData = hashMapOf(
        "date" to expense.date,
        "amount" to expense.amount,
        "type" to expense.type.name // Supondo que ExpenseType tenha um campo 'name'
    )
    try {
        firestore.collection("expenses").add(expenseData).await()
    } catch (e: Exception) {
        // Trate o erro, por exemplo, logando ou mostrando uma mensagem ao usuário
        println("Error adding expense: ${e.message}")
    }
}

suspend fun getExpensesFromFirestore(): List<Expense> {
    val expenses = mutableListOf<Expense>()
    try {
        val snapshot = firestore.collection("expenses").get().await()
        for (document in snapshot.documents) {
            val data = document.data
            if (data != null) {
                val id = document.id // Obtém o ID do documento
                val date = data["date"] as? Long ?: continue // Verifica se é Long
                val amount = data["amount"] as? Double ?: continue // Verifica se é Double
                val typeName = data["type"] as? String ?: continue // Verifica se é String
                val type = exampleExpenseTypes().find { it.name == typeName } // Encontre o tipo correspondente
                if (type != null) {
                    expenses.add(Expense(id = id, date = date, amount = amount, type = type))
                }
            }
        }
    } catch (e: Exception) {
        // Trate o erro, por exemplo, logando ou mostrando uma mensagem ao usuário
        println("Error retrieving expenses: ${e.message}")
    }
    return expenses
}
