package com.example.mony.feature.home.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.mony.feature.conta.viewmodel.ContaViewModel
import com.example.mony.feature.home.classe.Expense
import com.example.mony.feature.home.classe.TransactionType
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.auth
import com.google.firebase.firestore.FirebaseFirestoreException
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.firestore
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlin.coroutines.cancellation.CancellationException

open class HomeViewModel : ViewModel() {

    private val _authState = MutableStateFlow<AuthState>(AuthState.Loading)
    val authState: StateFlow<AuthState> = _authState.asStateFlow()

    private val _expenses = MutableStateFlow<List<Expense>>(emptyList())
    val expenses: StateFlow<List<Expense>> = _expenses.asStateFlow()

    private val _uiState = MutableStateFlow<UiState>(UiState.Idle)
    val uiState: StateFlow<UiState> = _uiState.asStateFlow()

    private val auth: FirebaseAuth by lazy { Firebase.auth }
    private val userId: String?
        get() = auth.currentUser?.uid

    fun checkUserAndLoadExpenses() = viewModelScope.launch(Dispatchers.IO) {
        _authState.value = AuthState.Loading
        delay(300) // Dá tempo da UI montar

        _authState.value = if (auth.currentUser != null) {
            loadExpenses()
            AuthState.Authenticated
        } else {
            AuthState.Unauthenticated
        }
    }

    fun addExpense(expense: Expense) = viewModelScope.launch {
        _uiState.value = UiState.Loading

        val currentUserId = userId ?: return@launch run {
            _authState.value = AuthState.Unauthenticated
        }

        try {
            require(expense.amount >= 0) { "Valor não pode ser negativo" }
            require(expense.date in 1..System.currentTimeMillis()) { "Data inválida" }

            val docRef = Firebase.firestore.collection("users")
                .document(currentUserId)
                .collection("transactions")
                .add(expense.toFirestoreMap())
                .await()

            _expenses.update { it + expense.copy(id = docRef.id) }

            _uiState.value = UiState.Success("Despesa adicionada com sucesso!")

        } catch (e: Exception) {
            handleFirestoreError(e, "Erro ao adicionar despesa")
        }
    }

    fun loadExpenses() = viewModelScope.launch(Dispatchers.IO) {
        _uiState.value = UiState.Loading

        val currentUserId = userId ?: return@launch run {
            _authState.value = AuthState.Unauthenticated
        }

        try {
            val snapshot = Firebase.firestore.collection("users")
                .document(currentUserId)
                .collection("transactions")
                .orderBy("date", Query.Direction.DESCENDING)
                .get()
                .await()

            val expensesList = snapshot.documents.mapNotNull { doc ->
                runCatching {
                    val type = TransactionType.valueOf(doc.getString("type") ?: return@runCatching null)
                    Expense(
                        id = doc.id,
                        amount = doc.getDouble("amount") ?: 0.0,
                        date = doc.getLong("date") ?: System.currentTimeMillis(),
                        type = type,
                        description = doc.getString("description") ?: ""
                    )
                }.onFailure {
                    Log.w("HomeVM", "Documento corrompido: ${doc.id}", it)
                }.getOrNull()
            }

            _expenses.value = expensesList
            _uiState.value = UiState.Success(
                if (expensesList.isEmpty()) "Nenhuma despesa encontrada" else null
            )

        } catch (e: Exception) {
            handleFirestoreError(e, "Falha ao carregar despesas")
        }
    }

    fun getExpenseById(expenseId: String): Expense? =
        _expenses.value.find { it.id == expenseId }

    open fun getExpense(expenseId: String): StateFlow<Expense?> {
        return _expenses
            .map { list -> list.find { it.id == expenseId } }
            .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)
    }

    fun deleteExpense(expenseId: String) = viewModelScope.launch(Dispatchers.IO) {
        _uiState.value = UiState.Loading
        val currentUserId = userId ?: run {
            Log.e("HomeVM", "Usuário não autenticado (userId é null)")
            _authState.value = AuthState.Unauthenticated
            return@launch
        }

        Log.d("HomeVM", "Deletando expense $expenseId para o user $currentUserId")

        try {
            Firebase.firestore.collection("users")
                .document(currentUserId)
                .collection("transactions")
                .document(expenseId)
                .delete()
                .await()

            Log.d("HomeVM", "Despesa deletada com sucesso: $expenseId")
            _expenses.update { it.filterNot { e -> e.id == expenseId } }
            _uiState.value = UiState.Success("Despesa removida com sucesso")
        } catch (e: Exception) {
            Log.e("HomeVM", "Erro ao deletar despesa: ${e.message}", e)
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

    private fun Expense.toFirestoreMap(): Map<String, Any> = mapOf(
        "amount" to amount,
        "date" to date,
        "type" to type.name,
        "description" to description
    )

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
}

class fakeHomeViewModel : HomeViewModel() {
    // Podes sobrescrever LiveData/StateFlow com valores fixos aqui se necessário
}
