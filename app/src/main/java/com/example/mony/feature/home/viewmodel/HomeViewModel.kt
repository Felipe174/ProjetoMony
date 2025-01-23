package com.example.mony.feature.home.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.mony.feature.home.classe.Expense
import com.example.mony.feature.home.classe.getExpensesFromFirestore
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch


class HomeViewModel : ViewModel() {

    private val _expenses = MutableStateFlow<List<Expense>>(emptyList())
    val expenses: StateFlow<List<Expense>> = _expenses

    fun addExpense(expense: Expense) {
        viewModelScope.launch {
            try {
                addExpenseToFirestore(expense) // Primeiro, adicione ao Firestore
                _expenses.value = _expenses.value + expense // Depois, atualize o estado local
            } catch (e: Exception) {
                Log.e("HomeViewModel", "Erro ao adicionar despesa: ${e.message}")
            }
        }
    }

    fun loadExpenses() {
        viewModelScope.launch {
            val previousExpenses = _expenses.value // Salve a lista anterior
            try {
                _expenses.value = getExpensesFromFirestore()
            } catch (e: Exception) {
                Log.e("HomeViewModel", "Erro ao carregar despesas: ${e.message}")
                // Se a operação falhar, mantenha a lista anterior
                _expenses.value = previousExpenses
            }
        }
    }

    fun deleteExpense(expense: Expense) {
        viewModelScope.launch {
            try {
                deleteExpenseFromFirestore(expense) // Primeiro, remova do Firestore
                // Depois, remova da lista local
                _expenses.value = _expenses.value.filter { it.id != expense.id }
            } catch (e: Exception) {
                Log.e("HomeViewModel", "Erro ao excluir despesa: ${e.message}")
            }
        }
    }

    private suspend fun addExpenseToFirestore(expense: Expense) {
        val firestore = FirebaseFirestore.getInstance()
        // Aqui você deve ter um campo que identifica a despesa, como um ID
        firestore.collection("expenses").add(expense)
            .addOnSuccessListener { documentReference ->
                // Se necessário, você pode atualizar o ID da despesa aqui
                // expense.id = documentReference.id
            }
            .addOnFailureListener { e ->
                Log.e("HomeViewModel", "Erro ao adicionar despesa ao Firestore: ${e.message}")
            }
    }

    private suspend fun deleteExpenseFromFirestore(expense: Expense) {
        val firestore = FirebaseFirestore.getInstance()
        // Aqui você deve ter um campo que identifica a despesa, como um ID
        if (expense.id.isNotEmpty()) {
            try {
                firestore.collection("expenses").document(expense.id).delete()
                    .addOnSuccessListener {
                        Log.d("HomeViewModel", "Despesa excluída com sucesso: ${expense.id}")
                    }
                    .addOnFailureListener { e ->
                        Log.e("HomeViewModel", "Erro ao excluir despesa do Firestore: ${e.message}")
                    }
            } catch (e: Exception) {
                Log.e("HomeViewModel", "Erro ao excluir despesa: ${e.message}")
            }
        } else {
            Log.e("HomeViewModel", "ID da despesa está vazio, não é possível excluir.")
        }
    }
}