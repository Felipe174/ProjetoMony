package com.example.mony.feature.home

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.mony.feature.home.classe.Expense
import com.example.mony.feature.home.classe.TransactionType
import com.example.mony.feature.home.viewmodel.HomeViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.text.SimpleDateFormat
import java.util.Date

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ExpenseDetailScreen(
    expenseId: String,
    onBack: () -> Unit,
    homeViewModel: HomeViewModel = viewModel()
) {
    val expense by homeViewModel.getExpense(expenseId).collectAsState(initial = null)

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Detalhes da Transação") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, "Voltar")
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .padding(16.dp)
        ) {
            when {
                expense == null ->
                    CircularProgressIndicator(modifier = Modifier.align(Alignment.CenterHorizontally))

                else -> {
                    TransactionDetailItem("Tipo", stringResource(expense!!.type.labelRes))
                    TransactionDetailItem("Valor", if(expense!!.type.isIncome) "+ € ${"%.2f".format(expense!!.amount)}" else "- € ${"%.2f".format(expense!!.amount)}")
                    TransactionDetailItem(
                        "Data",
                        SimpleDateFormat("dd/MM/yyyy").format(Date(expense!!.date))
                    )
                    TransactionDetailItem("Descrição", expense!!.description)
                }
            }
        }
    }
}

@Composable
private fun TransactionDetailItem(label: String, value: String) {
    Column(modifier = Modifier.padding(vertical = 8.dp)) {
        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Text(
            text = value,
            style = MaterialTheme.typography.bodyLarge
        )
    }
}

@Preview
@Composable
fun ExpenseDetailScreenPreview() {
    val mockViewModel = object : HomeViewModel() {
        override fun getExpense(expenseId: String): StateFlow<Expense?> {
            return MutableStateFlow(
                Expense(
                    id = "1",
                    amount = 150.0,
                    date = System.currentTimeMillis(),
                    type = TransactionType.FOOD,
                    description = "Descrição de exemplo"
                )
            ).asStateFlow()
        }
    }

    ExpenseDetailScreen(
        expenseId = "1",
        onBack = {},
        homeViewModel = mockViewModel
    )
}