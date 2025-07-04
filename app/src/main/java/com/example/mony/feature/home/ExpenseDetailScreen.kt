package com.example.mony.feature.home

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
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
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.mony.feature.home.classe.Expense
import com.example.mony.feature.home.classe.TransactionType
import com.example.mony.feature.home.viewmodel.HomeViewModel
import com.example.mony.ui.theme.MonyTheme
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ExpenseDetailScreen(
    expenseId: String,
    onBack: () -> Unit,
    homeViewModel: HomeViewModel = viewModel()
) {
    val expense by homeViewModel.getExpense(expenseId).collectAsState(initial = null)
    val dateFormatter = remember { SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Detalhes da Transação") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Voltar")
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
            if (expense == null) {
                CircularProgressIndicator(modifier = Modifier.align(Alignment.CenterHorizontally))
            } else {
                val e = expense!!

                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    elevation = CardDefaults.cardElevation(1.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        TransactionDetailItem("Tipo", stringResource(e.type.labelRes))
                        Spacer(Modifier.height(8.dp))
                        TransactionDetailItem(
                            "Valor",
                            if (e.type.isIncome) "+ € ${"%.2f".format(e.amount)}"
                            else "- € ${"%.2f".format(e.amount)}"
                        )
                        Spacer(Modifier.height(8.dp))
                        TransactionDetailItem("Data", dateFormatter.format(Date(e.date)))
                        Spacer(Modifier.height(8.dp))
                        TransactionDetailItem("Descrição", e.description.ifBlank { "Sem descrição" })
                    }
                }
            }
        }
    }
}

@Composable
private fun TransactionDetailItem(label: String, value: String) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Text(
            text = value,
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurface
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
    MonyTheme {
        ExpenseDetailScreen(
            expenseId = "1",
            onBack = {},
            homeViewModel = mockViewModel
        )
    }

}