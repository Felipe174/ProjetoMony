package com.example.mony.feature.home.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.mony.feature.home.classe.Expense
import com.example.mony.feature.home.classe.ExpenseType

import com.google.android.gms.tasks.Tasks
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

class HomeViewModel : ViewModel() {

    private val _expenses = MutableStateFlow<List<Expense>>(emptyList())
    val expenses: StateFlow<List<Expense>> = _expenses

    init {
        loadExpenses()  // Carregar despesas quando o ViewModel for inicializado
    }

    // Função para adicionar uma despesa
    fun addExpense(expense: Expense) {
        viewModelScope.launch {
            try {
                // Adiciona a despesa ao Firestore e atualiza a lista local
                addExpenseToFirestore(expense)
                _expenses.value += expense
            } catch (e: Exception) {
                Log.e("HomeViewModel", "Erro ao adicionar despesa: ${e.message}")
            }
        }
    }

    // Função para carregar todas as despesas do Firestore
    fun loadExpenses() {
        viewModelScope.launch {
            try {
                _expenses.value = getExpensesFromFirestore()
            } catch (e: Exception) {
                Log.e("HomeViewModel", "Erro ao carregar despesas: ${e.message}")
            }
        }
    }

    // Função para excluir uma despesa
    fun deleteExpense(expense: Expense) {
        if (expense.id.isBlank()) {
            Log.e("HomeViewModel", "Erro: ID da despesa está vazio. Não é possível excluir.")
            return
        }

        viewModelScope.launch {
            try {
                deleteExpenseFromFirestore(expense)
                // Atualiza a lista local removendo a despesa
                _expenses.value = _expenses.value.filter { it.id != expense.id }
                Log.d("HomeViewModel", "Despesa excluída com sucesso na lista local.")
            } catch (e: Exception) {
                Log.e("HomeViewModel", "Erro ao excluir despesa: ${e.message}")
            }
        }
    }

    private suspend fun addExpenseToFirestore(expense: Expense) {
        val firestore = FirebaseFirestore.getInstance()
        try {
            // Adiciona a despesa ao Firestore
            firestore.collection("expenses").add(mapOf(
                "date" to expense.date,
                "amount" to expense.amount,
                "type" to mapOf(
                    "imageResId" to expense.type.imageResId,
                    "name" to expense.type.name,
                    "isGain" to expense.type.isGain
                )
            )).await()

            Log.d("HomeViewModel", "Despesa adicionada com sucesso ao Firestore.")
        } catch (e: Exception) {
            Log.e("HomeViewModel", "Erro ao adicionar despesa ao Firestore: ${e.message}")
            throw e
        }
    }


    private suspend fun getExpensesFromFirestore(): List<Expense> {
        val firestore = FirebaseFirestore.getInstance()
        return try {
            val snapshot = firestore.collection("expenses").get().await()
            snapshot.documents.map { document ->
                // Obtendo os dados do documento
                val data = document.data ?: emptyMap<String, Any>()
                val expenseTypeMap = data["type"] as? Map<String, Any>

                // Convertendo para objetos
                Expense(
                    id = document.id,  // ID gerado automaticamente pelo Firestore
                    date = data["date"] as? Long ?: 0L,
                    amount = data["amount"] as? Double ?: 0.0,
                    type = ExpenseType(
                        imageResId = expenseTypeMap?.get("imageResId") as? Int ?: 0,
                        name = expenseTypeMap?.get("name") as? String ?: "",
                        isGain = expenseTypeMap?.get("isGain") as? Boolean ?: false
                    )
                )
            }
        } catch (e: Exception) {
            Log.e("HomeViewModel", "Erro ao buscar despesas do Firestore: ${e.message}")
            emptyList()
        }
    }


    // Função para excluir uma despesa no Firestore
    private suspend fun deleteExpenseFromFirestore(expense: Expense) {
        val firestore = FirebaseFirestore.getInstance()
        if (expense.id.isNotEmpty()) {
            try {
                firestore.collection("expenses").document(expense.id).delete().await()
                Log.d("HomeViewModel", "Despesa excluída com sucesso: ${expense.id}")
            } catch (e: Exception) {
                Log.e("HomeViewModel", "Erro ao excluir despesa do Firestore: ${e.message}")
                throw e
            }
        } else {
            Log.e("HomeViewModel", "ID da despesa está vazio, não é possível excluir.")
        }
    }
}
