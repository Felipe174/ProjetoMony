package com.example.mony.feature.home.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.mony.feature.home.classe.Expense
import com.example.mony.feature.home.classe.TransactionType

import com.google.android.gms.tasks.Tasks
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.FirebaseFirestoreException
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.QuerySnapshot
import com.google.firebase.firestore.firestore
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import java.lang.String.valueOf
import kotlin.coroutines.cancellation.CancellationException


class HomeViewModel : ViewModel() {

    private val _authState = MutableStateFlow<AuthState>(AuthState.Loading)
    val authState: StateFlow<AuthState> = _authState

    private val _expenses = MutableStateFlow<List<Expense>>(emptyList())
    val expenses: StateFlow<List<Expense>> = _expenses

    private val _uiState = MutableStateFlow<UiState>(UiState.Idle)
    val uiState: StateFlow<UiState> = _uiState

    private val auth: FirebaseAuth by lazy { Firebase.auth }
    private val userId: String?
        get() = auth.currentUser?.uid

    init {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                auth.currentUser?.reload()?.await()
                _authState.value = if (auth.currentUser != null) {
                    loadExpenses()
                    AuthState.Authenticated
                } else {
                    AuthState.Unauthenticated
                }
            } catch (e: Exception) {
                _authState.value = AuthState.Error("Falha na verificação de autenticação")
            }
        }
    }

    sealed interface UiState {
        object Idle : UiState
        object Loading : UiState
        data class Success(val message: String? = null) : UiState
        data class Error(val message: String) : UiState
    }

    sealed class AuthState {
        object Authenticated : AuthState()
        object Unauthenticated : AuthState()
        object Loading : AuthState()
        data class Error(val message: String) : AuthState()
    }

    fun addExpense(expense: Expense) = viewModelScope.launch {
        _uiState.value = UiState.Loading

        val currentUserId = userId ?: run {
            _authState.value = AuthState.Unauthenticated
            return@launch
        }

        try {
            with(expense) {
                require(amount >= 0) { "Valor não pode ser negativo" }
                require(date in 1..System.currentTimeMillis()) { "Data inválida" }
            }

            val expenseMap = expense.toFirestoreMap()

            Firebase.firestore.collection("users")
                .document(currentUserId)
                .collection("transactions")
                .add(expenseMap)
                .await()

            _expenses.update { currentList ->
                currentList + expense.copy(id = expense.id)
            }

            _uiState.value = UiState.Success("Despesa adicionada com sucesso!")

        } catch (e: Exception) {
            handleFirestoreError(e, "Erro ao adicionar despesa")
        }
    }

    fun loadExpenses() = viewModelScope.launch(Dispatchers.IO) {
        _uiState.value = UiState.Loading

        val currentUserId = userId ?: run {
            _authState.value = AuthState.Unauthenticated
            return@launch
        }

        try {
            val snapshot = Firebase.firestore.collection("users")
                .document(currentUserId)
                .collection("transactions")
                .orderBy("date", Query.Direction.DESCENDING)
                .get()
                .await()

            val expensesList = snapshot.documents.mapNotNull { doc ->
                try {
                    doc.getString("type")?.let { TransactionType.valueOf(it) }?.let {
                        Expense(
                            id = doc.id,
                            amount = doc.getDouble("amount") ?: 0.0,
                            date = doc.getLong("date") ?: System.currentTimeMillis(),
                            type = it,
                            description = doc.getString("description") ?: ""
                        )
                    }
                } catch (e: Exception) {
                    Log.w("HomeVM", "Documento corrompido: ${doc.id}", e)
                    null
                }
            }

            _expenses.value = expensesList
            _uiState.value = if (expensesList.isEmpty()) {
                UiState.Success("Nenhuma despesa encontrada")
            } else {
                UiState.Success()
            }

        } catch (e: Exception) {
            handleFirestoreError(e, "Falha ao carregar despesas")
        }
    }

    fun deleteExpense(expenseId: String) = viewModelScope.launch(Dispatchers.IO) {
        _uiState.value = UiState.Loading

        val currentUserId = userId ?: run {
            _authState.value = AuthState.Unauthenticated
            return@launch
        }

        try {
            Firebase.firestore.collection("users")
                .document(currentUserId)
                .collection("transactions")
                .document(expenseId)
                .delete()
                .await()

            _expenses.update { it.filterNot { e -> e.id == expenseId } }
            _uiState.value = UiState.Success("Despesa removida com sucesso")

        } catch (e: Exception) {
            handleFirestoreError(e, "Erro ao excluir despesa")
        }
    }

    private fun handleFirestoreError(e: Exception, context: String) {
        Log.e("HomeVM", "$context: ${e.stackTraceToString()}")

        val errorMessage = when (e) {
            is FirebaseFirestoreException -> when (e.code) {
                FirebaseFirestoreException.Code.PERMISSION_DENIED -> "Acesso negado"
                FirebaseFirestoreException.Code.UNAVAILABLE -> "Serviço indisponível"
                else -> "Erro no banco de dados"
            }
            is CancellationException -> "Operação cancelada"
            is IllegalArgumentException -> "Dados inválidos: ${e.message}"
            else -> "Erro: ${e.localizedMessage ?: "Desconhecido"}"
        }

        _uiState.value = UiState.Error(errorMessage)
    }

    private fun Expense.toFirestoreMap() = mapOf(
        "amount" to amount,
        "date" to date,
        "type" to type.name,
        "description" to description
    )
}
